# Qatania

A digital multiplayer strategy game for Android, inspired by the board game *The Settlers of Catan*.
Players build settlements, trade resources and compete to expand their territory across a procedurally
generated island rendered in 3D.

---

## Table of Contents

1. [Project Idea](#project-idea)
2. [Features](#features)
3. [How to Play](#how-to-play)
4. [Game Rules](#game-rules)
5. [Technology Stack](#technology-stack)
6. [External Frameworks and Third-Party Code](#external-frameworks-and-third-party-code)
7. [Use of AI Tools](#use-of-ai-tools)
8. [Getting Started](#getting-started)
9. [Authors](#authors)

---

## Project Idea

Qatania is our take on a classic resource-management board game, built as a native Android
application.

The core motivation for this project was to **replace the previous Android client**. That earlier app
presented the game in 2D and was generated entirely by AI. It worked for the course, but it left
us with a codebase we had not really written ourselves. This project is a whole rewrite of the client.

Three design decisions shape the project:

- **A 3D game board instead of a flat one.** The island is rendered with real 3D models rather than
  2D sprites, so the board can be viewed from different angles and feels closer to a physical set of
  tiles on a table. All 3D models in the game are handmade.
- **A procedurally generated island.** The board is generated at the start of every match based on
  the number of players in the lobby, so no two games use the same layout and the game scales beyond
  the fixed board of the original.
- **Support for large groups.** Up to 16 players can join a single lobby, well beyond the player
  count of the physical game.
- **Online play from anywhere.** The game runs against a Java Quarkus game server that we host
  ourselves, so players do not need to share a local network. Anyone with an internet connection can
  join a match.

We also added mechanics of our own on top of the classic rule set, most notably the ability to
**steal resources directly from opponents**, at the risk of being caught (see
[Game Rules](#game-rules)).

**Scope of this course project:** the work submitted here is the Android client. The backend game
server was written by us in an earlier course and is reused as-is, so it is documented below as
pre-existing code rather than as part of this submission.

## Features

- Online multiplayer for up to 16 players per lobby, running against our self-hosted Quarkus game
  server
- Two ways to join a game: entering a shared lobby code, or picking an open lobby from the in-app
  lobby browser
- Procedurally generated hexagonal game board, sized to the number of players
- 3D rendering of tiles, roads, settlements and cities, using handmade models
- Complete implementation of the core game loop: setup phase, dice roll, resource production,
  trading, building and turn handover
- Dice rolling by tapping the dice button **or** by physically shaking the device
- Robber mechanic including tile blocking and forced resource discard
- Maritime trading with the bank, including improved ratios for harbour tiles
- Additional "stealing" mechanic that lets players take resources from opponents at their own risk
- Reporting system that lets players accuse an opponent they suspect of stealing
- Live overview of all opponents' progress via the player bar

## How to Play

### Starting a Game

1. Launch the application and create a new lobby.
2. The other players join in one of two ways: by entering the shared lobby code, or by selecting the
   lobby directly from the lobby browser, which lists the currently open lobbies on the server.
3. Each player picks a display name and marks themselves as ready.
4. Once everyone is ready, the host starts the game.

When the game begins, the turn order is randomised and a board is generated to match the number of
players in the lobby.

### Setup Phase

The setup phase consists of two rounds. In each round every player places one road followed by one
settlement. The first round follows the regular turn order, the second round runs in reverse order so
that no player is systematically disadvantaged by their position.

### A Turn

| Phase | Action |
| --- | --- |
| **Roll** | Tap the dice button or shake your device to roll two dice. |
| **Production** | Resources from all tiles matching the rolled number are distributed automatically to adjacent settlements and cities. |
| **Trade** | Trade with the bank at 4:1, or at 3:1 / 2:1 if you control a matching harbour. |
| **Build** | Enter **Build Mode** with the build button in the sidebar, then pick a location for a road or settlement, or an existing settlement to upgrade to a city. |
| **End turn** | Confirm with the arrow button in the bottom-right corner to pass play on. |

### Stealing Resources

When resources run short, a player can take a resource from an opponent by double-tapping that
resource in the bar at the bottom of the screen. The resource is taken from whoever currently holds
the most of it. Opponents may notice the attempt, and being caught costs the thief half of their own
cards.

### Reporting a Suspected Theft

Theft is not announced, so it is up to the other players to catch it. If you suspect someone of having
stolen a resource, tap that player in the player bar at the top of the screen and then tap the report
button. Catching a thief this way costs them half of their cards.

Accusations are not free, though: reporting a player who did not steal anything costs the reporter one
random resource. It therefore pays to watch how the other players' resource counts develop instead of
reporting on a hunch.

## Game Rules

### Objective

Be the first player to reach **10 Victory Points (VP)** on your own turn. The game ends immediately
when that happens.

### Victory Points

| Source | Value |
| --- | --- |
| Settlement | 1 VP |
| City | 2 VP (replaces the settlement it was upgraded from) |
| Longest Road | 2 VP |

Each player starts with 2 VP from the two settlements placed during the setup phase.

### Buildings and Costs

| Building | Cost | Effect | Limit |
| --- | --- | --- | --- |
| Road | 1x Brick + 1x Lumber | Connects your network; required to expand | 15 |
| Settlement | 1x Brick + 1x Lumber + 1x Wool + 1x Grain | 1 resource from each adjacent producing tile | 5 |
| City (upgrade) | 2x Grain + 3x Ore | 2 resources from each adjacent producing tile | 4 |

Building always happens in **Build Mode**: roads, settlements and city upgrades can only be placed
while Build Mode is active, which is entered with the build button in the sidebar. In Build Mode any
position on the board can be tapped; the game checks the placement against the rules and rejects
invalid spots.

**Distance rule:** a settlement must be at least two intersections away from any other settlement or
city, and must connect to your own road network.

**Longest Road:** a continuous path of five or more roads earns the Longest Road award and its 2 VP.
Another player who later builds a strictly longer path takes the award, along with the points, from you.

### Resources

Five resource types are produced by the tiles: **Brick, Lumber, Wool, Grain** and **Ore**.

### Dice and Production

Every tile carries a number from 2 to 12, excluding 7. Two dice are rolled each turn; every tile
showing the resulting sum produces resources for all adjacent settlements (1 card) and cities
(2 cards).

### The Robber

Rolling a **7** activates the robber. The rolling player places it on any tile of their choice. While
the robber occupies a tile, that tile produces nothing. On placement, every player holding **8 or
more** resource cards must discard half of them, rounded down.

### Strategy Notes

- Tiles marked **6** and **8** (highlighted in red) come up most often, so settlements bordering them
  produce more reliably.
- Trading actively is usually faster than waiting for the right roll.
- Roads and settlements can be used defensively to cut off an opponent's route to the Longest Road or
  to a key intersection.
- If one resource piles up, trade it away or claim a harbour that lets you convert it cheaply.

## Technology Stack

| Area | Technology |
| --- | --- |
| Client platform | Android |
| Client language | Kotlin |
| 3D rendering | [SceneView](https://sceneview.github.io/docs.html) |
| Game server | Java, [Quarkus](https://quarkus.io/) (from a previous course, see below) |
| Client/server communication | WebSocket |
| Hosting | Self-hosted on our own server |
| Build system | Gradle |

## External Frameworks and Third-Party Code

In accordance with the course requirements, all pre-existing code and libraries used in this project
are listed here.

### SceneView

- **Project:** SceneView for Android (<https://sceneview.github.io/docs.html>)
- **Repository:** <https://github.com/SceneView/sceneview-android>
- **Used for:** loading, rendering, updating and handling all 3D models in the game, including the
  hexagonal tiles, roads, settlements and cities, as well as the camera and scene graph handling.
- **Reason for choosing it:** SceneView provides a Kotlin-first, Jetpack-Compose-friendly wrapper
  around Filament and offers the 3D scene handling we needed without writing a renderer from scratch.

### Game Server (pre-existing work from a previous course)

The backend game server is a Java [Quarkus](https://quarkus.io/) application that hosts the lobbies,
keeps the game state and handles the communication between the connected Android clients. It was
developed by us in a **previous course** and is reused here as pre-existing code. It is therefore not
part of the work submitted for this course; the deliverable of this project is the Android client.

- **Repository:** <https://github.com/Qatania/qtania-server>
- **Configuration:** the address of our hosted server is already set as `SERVER_URL` in
  `gradle.properties` by default, so a freshly cloned client connects to it without any further setup:

  ```properties
  SERVER_URL=ws://qatania.q1studios.at/game
  ```

The server is hosted on our own server, which is what allows players to join from anywhere.

Beyond SceneView and Quarkus, standard Android, Kotlin and Java platform libraries are used as
provided by the Android SDK and the respective dependency management. Any further dependencies are
declared in the project's build files.

### Assets

All 3D models used in the game are handmade by us. No pre-built, purchased or downloaded model assets
are part of this project.

> **Note:** please verify the license text and version of each dependency in the Gradle build files
> before submission, and add anything else that ended up in the project (e.g. sound files or fonts).

## Use of AI Tools

As required by the course guidelines, we disclose our use of AI tools during the work process.

AI assistants were used in a **supporting role only**, in the following ways:

1. **Generation of individual code snippets.** Small, isolated pieces of code were generated with AI
   assistance and then reviewed, adapted and integrated by us.
2. **Research and understanding of the SceneView framework.** The official SceneView documentation is
   sparse in several areas. AI tools were used to explain framework concepts, to suggest possible API
   usage and to help interpret error messages, similar to how one would use a search engine or a
   forum. Any suggestion obtained this way was verified against the actual framework behaviour before
   being used.
3. **Writing this documentation.** AI was used as a helper in creating this documentation. This
   README was drafted with AI assistance based on our own project notes, game documentation and
   tutorial texts, and was subsequently reviewed, corrected and edited by us. The content, meaning the
   project idea, the rule set, the technology choices and the disclosures below, originates from us;
   the AI tool assisted with structuring and phrasing it.

No part of this project was generated wholesale by an AI tool. The architecture, the game logic and
all design decisions are our own.

This is a deliberate difference from the predecessor app described under
[Project Idea](#project-idea): that earlier 2D client was generated entirely by AI, and replacing it
with a client we wrote ourselves was one of the reasons for starting this project.

**We take sole responsibility for the produced code.**

## Getting Started

The instructions below cover the Android client, which is the deliverable of this course. The game
server is already running on our own server and its address is preconfigured as `SERVER_URL` in
`gradle.properties` (`ws://qatania.q1studios.at/game`), so no server setup is required in order to
build and play.

### Requirements

- Android Studio (recent stable version)
- Android SDK as configured in the project's Gradle files
- A physical Android device or emulator; a physical device is recommended, since the dice can be
  rolled by shaking

### Build and Run

```bash
git clone <repository-url>
cd <repository-directory>
```

Then open the project in Android Studio, let Gradle sync, and run the app configuration on your
device. Alternatively, build from the command line:

```bash
./gradlew assembleDebug
```

The resulting APK can be found under `app/build/outputs/apk/debug/`.

### Running the Server Locally (optional)

If you would rather not use our hosted instance, the game server can also be run on your own machine.
Check out the server repository and start it:

```bash
git clone https://github.com/Qatania/qtania-server
```

Refer to that repository for its own build and run instructions. Then point the client at your local
instance by changing `SERVER_URL` in `gradle.properties`, for example:

```properties
SERVER_URL=ws://10.0.2.2:8080/game
```

(`10.0.2.2` is the host machine as seen from the Android emulator. On a physical device, use your
computer's address on the local network instead.)

### Playing Together

One player creates a lobby. The others either enter the displayed lobby code or find the lobby in the
lobby browser. Because the game connects to our Quarkus game server, which we host ourselves, players
do not have to be on the same local network: an internet connection and the installed app are enough
to play together from anywhere.

## Authors

- **Philipp Wolfger**
- **Tobias Wurzer**
- **David Schwaiger**

Developed for the course **623.952 App Development (26S)** at the Alpen-Adria-Universität Klagenfurt.