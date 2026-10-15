# Reef Keeper: starter project (Week 3-4)

A counting game built on the Fish Game API for CIS045-3.
This starter covers milestone 1 of the build plan: **project setup, the Fish API
client and the puzzle sources**, plus a small test page that lets you play one
puzzle at a time.

> Referencing: this starter skeleton was generated with Claude (Anthropic AI) on
> 8 Oct 2026. Every file says so in its header comment. Keep those comments,
> and add your own note whenever you change or add code.

## Import into Eclipse

1. Install JDK 21 and **Eclipse IDE for Enterprise Java and Web Developers**.
2. File > Import > Maven > **Existing Maven Projects** > Browse to this
   `reef-keeper` folder > Finish. Wait for Eclipse to download the libraries
   (bottom-right progress bar).
3. Open `src/main/java/com/reefkeeper/ReefKeeperApplication.java`, right-click it
   > **Run As > Java Application**.
4. Open http://localhost:8080 . You should see a fish picture. Type a count and press Check.
5. Stop the server with the red square in the Console before running it again.

Run the tests: right-click `src/test/java` > Run As > JUnit Test.

## First thing to check

Open this in your browser:

    https://marcconrad.com/uob/fish/api.php?out=json&base64=yes

The code expects a reply shaped like `{"question": "...", "fish": 8, "chest": 4}`.
If the real field names differ, change them in `FishApiPuzzleSource.parse()`.
They have not been checked against the live API yet, because the API could not
be reached from the machine this was written on.

## What is where

| Path | Job |
| --- | --- |
| `puzzle/Puzzle` | One round's data. The image is safe for the browser; the counts are not. |
| `puzzle/PuzzleSource` | Interface the game depends on. Any puzzle supplier implements it. |
| `puzzle/FishApiClient` | HTTP only: calls the Fish API, returns raw text or bytes. |
| `puzzle/FishApiPuzzleSource` | Turns the JSON reply into a `Puzzle`; never lets the answer-bearing image URL reach the browser. |
| `puzzle/CachedPuzzleSource` | Wraps another source and serves saved puzzles if the API is down (Decorator pattern). |
| `config/PuzzleConfig` | The one place that wires the sources together (dependency injection). |
| `web/TestRoundController` | Temporary REST endpoints for the test page; keeps the answer in the HttpSession. |
| `static/` | Test page: `index.html`, `style.css`, `test-round.js`. |

The `puzzle` package has no Spring annotations: it is plain Java, wired up in
`PuzzleConfig`. Mention this in the video as an example of low coupling.

## Next (Week 5)

- `game` package: `Aquarium`, `Tank` and `DeliveryService` (a delivery every 3 minutes,
  a 30-second deadline kept on the server, placing fish in a tank).
- Events: `DeliveryArrivedEvent` and `DeliveryAcceptedEvent`, published with Spring's
  `ApplicationEventPublisher`.
- `GameController` replaces `TestRoundController`; `aquarium.js` replaces `test-round.js`.
