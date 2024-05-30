package ituvtu.server.controller;

import ituvtu.server.database.DatabaseConnection;
import ituvtu.server.view.ServerApp;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ConfigController {

    @FXML
    private TextField portField;
    @FXML
    private TextField dbUrlField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private String serverPort;
    private String databaseUrl;
    private String username;
    private String password;

    @FXML
    public void handleSave() {
        try {
            // Set the variables with values from the text fields
            serverPort = portField.getText();
            databaseUrl = dbUrlField.getText();
            username = usernameField.getText();
            password = passwordField.getText();

            // Close the configuration window
            Stage stage = (Stage) portField.getScene().getWindow();
            stage.close();

            // Initialize the database connection
            DatabaseConnection.initialize(databaseUrl, username, password);

            // Proceed to start the server
            ServerApp.initializeServer(serverPort);
            ServerApp.showMainScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
