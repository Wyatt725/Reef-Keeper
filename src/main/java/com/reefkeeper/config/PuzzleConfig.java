// Reef Keeper - CIS045-3 coursework.
// Assited by claude AI
package com.reefkeeper.config;

import com.reefkeeper.puzzle.CachedPuzzleSource;
import com.reefkeeper.puzzle.FishApiClient;
import com.reefkeeper.puzzle.FishApiPuzzleSource;
import com.reefkeeper.puzzle.PuzzleSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Random;

/**
 * The ONE place that decides which PuzzleSource the game uses.
 *
 * The classes in the puzzle package have no Spring annotations at all; they are
 * plain Java. Spring builds them here and injects the result wherever a
 * PuzzleSource is asked for (dependency injection). To switch the whole game to
 * a different source, you change only this method.
 */
@Configuration
public class PuzzleConfig {

    @Bean
    public FishApiClient fishApiClient(@Value("${fish.api.url}") String apiUrl) {
        HttpClient http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        return new FishApiClient(http, URI.create(apiUrl), Duration.ofSeconds(8));
    }

    @Bean
    public PuzzleSource puzzleSource(FishApiClient client, ObjectMapper mapper) {
        PuzzleSource live = new FishApiPuzzleSource(client, mapper);
        return new CachedPuzzleSource(live, 50, new Random());
    }
}
