package controllers.menus.room_creation;

import core.logic.Room;
import core.scenes.SceneManager;
import core.network.FirebaseManager;
import core.network.FirebaseWriter;
import core.scenes.ScenePath;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import java.io.IOException;

public class RoomCreationController {
    @FXML private TextField nameTextField = new TextField();
    @FXML private TextField passwordTextField = new TextField();
    @FXML private ComboBox<String> gameModeComboBox;
    @FXML private CheckBox allowToWatchCheckBox;

    @FXML
    protected void onCreateButtonClick() {
        Room room = new Room();
        String password = passwordTextField.getText().isEmpty() ? "" :  passwordTextField.getText();
        String gameMode = gameModeComboBox.getValue();
        room.setName(nameTextField.getText());
        room.setGameMode(gameMode);
        room.setPassword(password);
        room.setAllowToWatch(String.valueOf(allowToWatchCheckBox.isSelected()));

        String roomId = FirebaseWriter.addRoom(room);
        FirebaseWriter.addMessageToRoom(roomId, "!!!Room \"" + nameTextField.getText() + "\" was created");
        FirebaseManager.attachClient(roomId);
    }

    @FXML
    protected void onBackButtonClick() throws IOException {
        SceneManager.loadScene(ScenePath.MAIN_MENU);
    }
}
