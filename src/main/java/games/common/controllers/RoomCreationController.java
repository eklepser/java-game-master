package games.common.controllers;

import javafx.scene.control.*;
import core.SceneManager;
import core.network.FirebaseManager;
import core.network.FirebaseWriter;

import javafx.application.Platform;
import javafx.fxml.FXML;

import java.io.IOException;
import java.util.HashMap;

public class RoomCreationController {
    private HashMap<String, String> roomInfo = new HashMap<>();

    @FXML private TextField nameTextField = new TextField();
    @FXML private TextField passwordTextField = new TextField();
    @FXML private ComboBox<String> gameModeComboBox;
    @FXML private CheckBox allowToWatchCheckBox;

    @FXML
    protected void onCreateButtonClick() throws IOException {
        roomInfo.put("name", nameTextField.getText());
        String gameModeFXML = getGameModeFXML(gameModeComboBox.getValue());
        roomInfo.put("gameMode", gameModeFXML);
        roomInfo.put("password", passwordTextField.getText());
        roomInfo.put("allowToWatch", String.valueOf(allowToWatchCheckBox.isSelected()));

        String roomId = FirebaseWriter.addRoom(roomInfo);
        FirebaseWriter.addMessageToRoom(roomId, "!!!Room \"" + nameTextField.getText() + "\" was created");
        FirebaseManager.attachClient(roomId);

        Platform.runLater(() -> {
            try {
                SceneManager.loadScene("games/" + gameModeFXML + ".fxml");
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
        switch (gameModeName) {
            case "Tictactoe classic": return "tictactoe_classic";
            case "Tictactoe advanced": return "tictactoe_advanced";
            default: return "";
        }
    }
}
