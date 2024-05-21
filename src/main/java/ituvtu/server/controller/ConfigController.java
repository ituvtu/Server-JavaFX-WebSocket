package ituvtu.server.controller;

import ituvtu.server.view.ServerApp;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ConfigController {

    @FXML
    private TextField portField;
    @FXML
    private TextField dbUrlField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    public void handleSave() {
        try (OutputStream output = new FileOutputStream("src/main/resources/ituvtu/server/config.properties")) {
            Properties prop = new Properties();

            // set the properties value
            prop.setProperty("srv.port", portField.getText());
            prop.setProperty("db.url", dbUrlField.getText());
            prop.setProperty("db.user", usernameField.getText());
            prop.setProperty("db.password", passwordField.getText());

            // save properties to project root folder
            prop.store(output, null);

            // Close the configuration window
            Stage stage = (Stage) portField.getScene().getWindow();
            stage.close();

            // Proceed to start the server
            ServerApp.initializeServer();
            ServerApp.showMainScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
