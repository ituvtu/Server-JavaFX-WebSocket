# Messenger Server

This repository contains the server-side implementation of a messaging application. The server handles client connections, message routing, user authentication, and interactions with the MySQL database.

## Features

- **WebSocket Server**: Manages real-time communication with clients.
- **User Authentication**: Validates user credentials and manages sessions.
- **Message Routing**: Routes messages between connected clients.
- **Database Integration**: Stores and retrieves messages and user data from a MySQL database.
- **Configuration Page**: Allows configuration of server settings such as port and database credentials.


### Configuration

Before starting the server, a configuration page will appear where you can set the port and database credentials. These settings will be saved in the `config.properties` file.

### Server Application Structure

- **Main Application**: `ServerApp.java` initializes and starts the server.
- **Controllers**: Handles UI interactions.
  - `ConfigController.java`: Manages the configuration page.
  - `ServerController.java`: Manages the main server interface.
- **Models**: Contains the server logic.
  - `Server.java`: Core server functionalities.
- **Views**: FXML files for UI.
  - `config.fxml`: Configuration page layout.
  - `server.fxml`: Main server interface layout.

## Related Projects

- **Client Application**: The client-side implementation can be found [here](https://github.com/ituvtu/Client-JavaFX-WebSocket).

