package ituvtu.server.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ituvtu.server.model.Server;
import ituvtu.server.controller.ServerController;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class ServerApp extends Application {
    private static Server server;
    private static Stage primaryStage;

    public static void initializeServer() throws Exception {
        if (server == null) {
            InputStream is = new FileInputStream("src/main/resources/ituvtu/server/config.properties");
            Properties props = new Properties();
            props.load(is);
            int port = Integer.parseInt(props.getProperty("srv.port"));
            server = Server.getInstance(port);
            server.start();
        }
    }

    public static void showMainScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(ServerApp.class.getResource("/ituvtu/server/server.fxml"));
        loader.setControllerFactory(c -> ServerController.getInstance(server));
        Parent root = loader.load();
        ServerController controller = loader.getController();
        controller.setServer(server);
        server.addObserver(controller);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(ServerApp.class.getResource("/ituvtu/server/server-styles.css")).toExternalForm());
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ServerApp.primaryStage = primaryStage;
        showConfigScreen();
    }

    public void showConfigScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ituvtu/server/config.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setTitle("Server Configuration");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (server != null) {
            try {
                server.stop();
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
