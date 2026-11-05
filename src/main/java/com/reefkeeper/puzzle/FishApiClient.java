// Reef Keeper - CIS045-3 coursework.
// Assited by claude AI
package com.reefkeeper.puzzle;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Talks HTTP to the Fish Game API and nothing else.
 *
 * It knows WHERE the API is and HOW to call it, but not what the reply means.
 * Turning the reply into a Puzzle is FishApiPuzzleSource's job (high cohesion:
 * each class has one reason to change).
 *
 * Interoperability: the Fish API is a PHP service on another server. We reach
 * it with a plain HTTPS GET and it answers with JSON, so the two programs work
 * together even though they are written in different languages.
 */
public class FishApiClient {

    /** out=json asks for JSON (the API can also send CSV); base64=yes embeds the image. */
    public static final String DEFAULT_URL = "https://marcconrad.com/uob/fish/api.php?out=json&base64=yes";

    private final HttpClient http;
    private final URI apiUri;
    private final Duration requestTimeout;

    public FishApiClient(HttpClient http, URI apiUri, Duration requestTimeout) {
        this.http = http;
        this.apiUri = apiUri;
        this.requestTimeout = requestTimeout;
    }

    /** Calls the API once and returns the raw JSON text of the reply. */
    public String fetchPuzzleJson() {
        HttpRequest request = HttpRequest.newBuilder(apiUri)
                .timeout(requestTimeout)
                .header("Accept", "application/json")
                .GET()
                .build();
        return new String(send(request), java.nio.charset.StandardCharsets.UTF_8);
    }

    /** Downloads raw bytes, e.g. the PNG image when the API sends an image URL. */
    public byte[] fetchBytes(URI uri) {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(requestTimeout)
                .GET()
                .build();
        return send(request);
    }

    private byte[] send(HttpRequest request) {
        try {
            HttpResponse<byte[]> response = http.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                throw new PuzzleUnavailableException(
                        "Fish API answered HTTP " + response.statusCode() + " for " + request.uri());
            }
            return response.body();
        } catch (IOException e) {
            throw new PuzzleUnavailableException("Could not reach the Fish API: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PuzzleUnavailableException("Interrupted while calling the Fish API", e);
        }
    }
}
