package controllers.menus.room_creation;

import core.scenes.SceneManager;
import core.network.FirebaseManager;
import core.network.FirebaseWriter;
import core.scenes.ScenePath;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import java.io.IOException;
import java.util.HashMap;

public class RoomCreationController {
    private final HashMap<String, String> roomInfo = new HashMap<>();

    @FXML private TextField nameTextField = new TextField();
    @FXML private TextField passwordTextField = new TextField();
    @FXML private ComboBox<String> gameModeComboBox;
    @FXML private CheckBox allowToWatchCheckBox;

    @FXML
    protected void onCreateButtonClick() {
        roomInfo.put("name", nameTextField.getText());
        String gameMode = gameModeComboBox.getValue();
        roomInfo.put("gameMode", gameMode);
        String password = passwordTextField.getText().isEmpty() ? "" :  passwordTextField.getText();
        roomInfo.put("password", password);
        roomInfo.put("allowToWatch", String.valueOf(allowToWatchCheckBox.isSelected()));

        String roomId = FirebaseWriter.addRoom(roomInfo);
        FirebaseWriter.addMessageToRoom(roomId, "!!!Room \"" + nameTextField.getText() + "\" was created");
        FirebaseManager.attachClient(roomId);

        Platform.runLater(() -> {
            try {
                SceneManager.loadScene(getGameModeFXML(gameMode));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    protected void onBackButtonClick() throws IOException {
        SceneManager.loadScene(ScenePath.MAIN_MENU);
    }

    private String getGameModeFXML(String gameModeName) {
        return switch (gameModeName) {
            case "Tic-tac-toe Classic" -> ScenePath.TICTACTOE_CLASSIC;
            case "Chat game" -> ScenePath.CHAT_GAME;
            default -> "";
        };
    }
}
