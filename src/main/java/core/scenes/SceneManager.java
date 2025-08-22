package core.scenes;

import core.Main;
import core.logic.Client;
import core.network.FirebaseWriter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.util.Objects;

public class SceneManager {
    private static Stage primaryStage;
    private static Stage modalStage;
    private static double sceneWidth;
    private static double sceneHeight;

    public static void init(Stage stage) {
        primaryStage = stage;
        sceneWidth = 600;
        sceneHeight = 400;
    }

    public static Object getUserData() {
        return primaryStage.getScene().getUserData();
    }

    public static void setUserData(Object userData) {
        primaryStage.getScene().setUserData(userData);
    }

    public static void loadScene(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/scenes" + fxmlPath));
        setupPrimaryStage();
        Scene scene = new Scene(loader.load(), sceneWidth, sceneHeight);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Stage loadModalScene(String fxmlPath, int width, int height) throws IOException {
        modalStage = new Stage();
        setupModalStage();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/scenes" + fxmlPath));
        Scene scene = new Scene(loader.load(), width, height);
        modalStage.setScene(scene);
        return modalStage;
    }

    public static void exit() {
        primaryStage.close();
    }

    private static void setupPrimaryStage() {
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.setTitle("Game Master");
        Image icon = new Image(Objects.requireNonNull(SceneManager.class.getResourceAsStream("/icon.png"))); // e.g., 16x16, 32x32, or 64x64 PNG
        primaryStage.getIcons().add(icon);
        if (primaryStage.getScene() != null) sceneWidth = primaryStage.getScene().getWidth();
        if (primaryStage.getScene() != null) sceneHeight = primaryStage.getScene().getHeight();
        primaryStage.setOnCloseRequest((_) -> {
            String roomId = Client.CurrentRoom.getId();
            String clientId = Client.getClientId();
            if ((clientId != null) && (roomId != null)) FirebaseWriter.removeClientFromRoom(clientId, roomId);
        });
    }

    private static void setupModalStage() {
        modalStage.initOwner(primaryStage);
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initStyle(StageStyle.UNDECORATED);
    }
}
