package core;

import core.logic.Client;
import core.network.FirebaseWriter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {
    private static Stage primaryStage;

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void loadScene(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/scenes/" + fxmlPath));
        System.out.println(loader.getLocation());
        Scene scene = new Scene(loader.load(), 800, 600);
        setupPrimaryStage();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void exit() {
        primaryStage.close();
    }

    private static void setupPrimaryStage() {
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest((_) -> {
            String roomId = Client.CurrentRoom.getRoomId();
            String clientId = Client.getClientId();
            if ((clientId != null) && (roomId != null)) FirebaseWriter.removeClientFromRoom(clientId, roomId);
            System.out.println("Terminated");
        });
    }
}
