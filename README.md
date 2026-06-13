## Features and Project Architecture

Our project delivers a full-featured implementation of the game, built on a robust, decoupled architecture that bridges multiple user interfaces and networking protocols, topped with persistent advanced functionalities.

---

### Core Implementation

* **Complete Rulebook Integration** Every single game rule, edge case, and turn-based logic component has been meticulously implemented into the core engine.

### Networking and Architecture
The system supports dual-protocol communication, allowing seamless, concurrent matchmaking and gameplay through:
* **RMI (Remote Method Invocation):** High-level object-oriented network communication.
* **Sockets:** Reliable, stream-based TCP connection handling.

### User Interfaces
We developed two distinct, fully swappable presentation layers:
* **TUI (Text User Interface):** A clean, lightweight, and fast command-line experience.
* **GUI (Graphical User Interface):** A modern, rich, and highly interactive visual interface.

---

## Advanced Features (FA)

To elevate the project beyond the standard requirements, we integrated a dedicated data and storage layer:

| Feature | Description | Tech Stack |
| :--- | :--- | :--- |
| **Database Leaderboard** | A dynamic scoreboard system that tracks, ranks, and displays real-time player statistics and historical match outcomes. | MySQL |
| **Data Persistence** | Full system state persistence. Game states, user profiles, and active match histories are safely serialized and saved to the local disk, ensuring seamless crash recovery and data retention even after a server reboot. | File System / Disk Storage |

---
