package ituvtu.server.view;

import ituvtu.server.controller.*;
import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ituvtu.server.model.*;
import java.io.*;
import java.util.*;

@SuppressWarnings("CallToPrintStackTrace")
public class ServerApp extends Application {
    private static IServerController serverController;
    private static IServer server;
    private static Stage primaryStage;


    public static void initializeServer() throws Exception {
        InputStream is = new FileInputStream("src/main/resources/ituvtu/server/config.properties");
        Properties props = new Properties();
        props.load(is);
        int port = Integer.parseInt(props.getProperty("srv.port"));
        server = Server.getInstance(port);
        if (serverController == null) {
            serverController = new ServerController();
        }
        serverController.setServer(server);
        server.addObserver((IServerObserver) serverController);
        server.startserver();
    }

    public void showConfigScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ituvtu/server/config.fxml"));
        Parent root = loader.load();
        ConfigController configController = loader.getController();
        System.out.println("Config: " + configController);
        Scene scene = new Scene(root);
        primaryStage.setTitle("Server Configuration");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showMainScreen() {
        Platform.runLater(() -> {
            try {
                serverController.clearObservers();
                FXMLLoader loader = new FXMLLoader(ServerApp.class.getResource("/ituvtu/server/server.fxml"));
                Parent root = loader.load();
                IServerController mainController = loader.getController();
                mainController.setServer(server);
                serverController = loader.getController();
                serverController.setServer(server);
                Scene scene = new Scene(root);
                scene.getStylesheets().add(Objects.requireNonNull(ServerApp.class.getResource("/ituvtu/server/server-styles.css")).toExternalForm());
                primaryStage.setTitle("Server");
                primaryStage.setScene(scene);
                System.out.println(server.getObservers());
                primaryStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ServerApp.primaryStage = primaryStage;
        showConfigScreen();
    }



    @Override
    public void stop() {
        if (server != null) {
            try {
                server.stopserver();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Server failed to stop cleanly: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
