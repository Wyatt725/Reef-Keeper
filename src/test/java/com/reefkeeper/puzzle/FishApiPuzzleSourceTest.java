// Reef Keeper - CIS045-3 coursework.
// Source: starter skeleton generated with Claude (Anthropic AI), 8 Oct 2026.
package com.reefkeeper.puzzle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Tests the JSON-to-Puzzle conversion with sample replies, no network needed. */
class FishApiPuzzleSourceTest {

    // client is null: these tests never reach the network
    private final FishApiPuzzleSource source = new FishApiPuzzleSource(null, new ObjectMapper());

    @Test
    void readsCountsAndBase64Image() {
        Puzzle p = source.parse("{\"question\":\"iVBORw0KGgo=\",\"fish\":8,\"chest\":4}");
        assertEquals(8, p.fishCount());
        assertEquals(4, p.chestCount());
        assertEquals("data:image/png;base64,iVBORw0KGgo=", p.imageDataUri());
    }

    @Test
    void keepsAnExistingDataUri() {
        Puzzle p = source.parse("{\"question\":\"data:image/png;base64,AAAA\",\"fish\":3,\"chest\":0}");
        assertEquals("data:image/png;base64,AAAA", p.imageDataUri());
    }

    @Test
    void acceptsCountsSentAsText() {
        Puzzle p = source.parse("{\"question\":\"AAAA\",\"fish\":\"5\",\"chest\":\"2\"}");
        assertEquals(5, p.fishCount());
        assertEquals(2, p.chestCount());
    }

    @Test
    void rejectsReplyWithoutFishCount() {
        assertThrows(PuzzleUnavailableException.class,
                () -> source.parse("{\"question\":\"AAAA\",\"chest\":2}"));
    }

    @Test
    void rejectsInvalidJson() {
        assertThrows(PuzzleUnavailableException.class, () -> source.parse("<html>error</html>"));
    }
}
