package games.common.controllers;

import core.SceneManager;
import core.network.FirebaseManager;
import core.network.FirebaseWriter;
import core.network.FirebaseListener;
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
        String gameModeFXML = getGameModeFXML(gameModeComboBox.getValue());
        roomInfo.put("gameMode", gameModeFXML);
        String password = passwordTextField.getText().isEmpty() ? "" :  passwordTextField.getText();
        roomInfo.put("password", password);
        roomInfo.put("allowToWatch", String.valueOf(allowToWatchCheckBox.isSelected()));

        String roomId = FirebaseWriter.addRoom(roomInfo);
        FirebaseWriter.addMessageToRoom(roomId, "!!!Room \"" + nameTextField.getText() + "\" was created");
        FirebaseManager.attachClient(roomId);

        Platform.runLater(() -> {
            try {
                SceneManager.loadScene("games/" + gameModeFXML + ".fxml");
                //FirebaseListener.removeRoomListListener();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    protected void onBackButtonClick() throws IOException {
        SceneManager.loadScene("common/main_menu.fxml");
    }

    private String getGameModeFXML(String gameModeName) {
        return switch (gameModeName) {
            case "Tictactoe classic" -> "tictactoe_classic";
            case "Tictactoe advanced" -> "tictactoe_advanced";
            default -> "";
        };
    }
}
