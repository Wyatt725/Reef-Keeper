// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.puzzle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.Base64;

/**
 * Gets puzzles from the live Fish API and converts each reply into a Puzzle.
 *
 * Security note: an image URL from the API ends in something like _004x008.png,
 * i.e. it contains the chest and fish counts. So we never pass that URL to the
 * browser. We always turn the image into a data: URI here, on the server.
 */
public class FishApiPuzzleSource implements PuzzleSource {

    private static final String PNG_PREFIX = "data:image/png;base64,";

    private final FishApiClient client;
    private final ObjectMapper mapper;

    public FishApiPuzzleSource(FishApiClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public Puzzle nextPuzzle() {
        return parse(client.fetchPuzzleJson());
    }

    /** Package-private so the unit tests can feed it sample JSON without a network. */
    Puzzle parse(String json) {
        JsonNode root;
        try {
            root = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new PuzzleUnavailableException("Fish API reply was not valid JSON", e);
        }
        String question = requireText(root, "question");
        int fish = requireInt(root, "fish");
        int chest = requireInt(root, "chest");
        return new Puzzle(toDataUri(question), fish, chest);
    }

    /** Accepts any of the three forms the image can arrive in and returns a data: URI. */
    String toDataUri(String question) {
        String q = question.strip();
        if (q.startsWith("data:")) {
            return q;                                   // already a data URI
        }
        if (q.startsWith("http://") || q.startsWith("https://")) {
            byte[] png = client.fetchBytes(URI.create(q)); // download it so the URL never leaves the server
            return PNG_PREFIX + Base64.getEncoder().encodeToString(png);
        }
        return PNG_PREFIX + q;                          // plain base64 text
    }

    private static String requireText(JsonNode root, String field) {
        JsonNode node = root.get(field);
        if (node == null || node.isNull() || node.asText().isBlank()) {
            throw new PuzzleUnavailableException("Fish API reply has no '" + field + "' field");
        }
        return node.asText();
    }

    private static int requireInt(JsonNode root, String field) {
        JsonNode node = root.get(field);
        if (node == null || node.isNull()) {
            throw new PuzzleUnavailableException("Fish API reply has no '" + field + "' field");
        }
        if (node.isInt()) {
            return node.intValue();
        }
        try {
            return Integer.parseInt(node.asText().strip()); // the API might send "8" as text
        } catch (NumberFormatException e) {
            throw new PuzzleUnavailableException("Fish API field '" + field + "' is not a number: " + node, e);
        }
    }
}
