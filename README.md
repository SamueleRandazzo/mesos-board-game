# Mesos - Board Game (Java Implementation)

This repository contains the final project for the **Software Engineering** course at **Politecnico di Milano** (Academic Year 2025/2026). 

It features a full-featured, distributed Java application of the board game **Mesos**, implementing client-server architecture, dual network protocols (RMI & Sockets), dual user interfaces (GUI & TUI), database and local persistence.

---

## Features and Project Architecture

Our project delivers a full-featured implementation of the game, built on a robust, decoupled architecture that bridges multiple user interfaces and networking protocols, topped with persistent advanced functionalities.

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

## Getting Started and Execution Guide

The application features a single unified entry point (`Main`). Depending on the arguments provided at startup, the system will dynamically launch either the Server application or a Client instance.

### Running the Server

To launch the system in **Server Mode**, the very first command-line argument must be `--server`. You can also provide the username, password and db port for the database as subsequent arguments to initialize the leaderboard and database persistence mechanisms.

> **Prerequisite & Database Info:** A MySQL database server (DBMS) must be active and running on the host machine. If no database credentials are provided at startup, or if the system cannot establish a connection to the DBMS, the server will still start successfully, but the **Leaderboard functionality will be disabled**.<br>Note on DB Port: If the <db_port> argument is omitted, the system will automatically fall back to 3306, which is the standard default port for MySQL.
```bash
# Complete syntax
java -jar GC35_MESOS.jar --server <db_username> <db_password> <db_port>

# Example without port (uses default 3306)
java -jar GC35_MESOS.jar --server <db_username> <db_password>
```

### Running the Client
If the --server flag is omitted, the application automatically boots into Client Mode.

### Default Behavior
By default, running the client without specific modifier flags will start the Graphical User Interface (GUI) using RMI network communication, attempting to connect to localhost (127.0.0.1).

### Configuration Flags
You can customize the user interface and network protocol using the following flags, which can be placed in any position among the arguments:

> --cli : Forces the client to start using the Text User Interface instead of the GUI.

> --socket : Forces the client to use TCP Stream Sockets instead of RMI.

### Server IP Address
The last parameter passed to the command line is always treated as the target Server IP address. If no IP address is specified, the application will fallback to the default loopback address 127.0.0.1.

### Execution Examples
```bash
# 1. Default Mode (Launches GUI + RMI, connecting to 127.0.0.1)
java -jar GC35_MESOS.jar

# 2. GUI via Sockets (Connecting to a specific server IP)
java -jar GC35_MESOS.jar --socket 192.168.1.50

# 3. CLI via RMI (Connecting to a specific server IP)
java -jar GC35_MESOS.jar --cli 192.168.1.50

# 4. Full Command Line Mode (CLI + Sockets, connecting to a specific server IP)
java -jar GC35_MESOS.jar --cli --socket 10.0.0.5
```

---

## License & Copyright

Il Gioco da tavolo Mesos e tutto il relativo materiale grafico è di esclusiva proprietà di Cranio Creations.

> *The board game Mesos and all related graphic material are the exclusive property of Cranio Creations.*
