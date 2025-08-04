package controllers.menus.room_selection;

import core.logic.Room;
import core.network.FirebaseListener;
import core.scenes.SceneManager;
import core.scenes.ScenePath;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.HashMap;

public class PasswordConfirmationController {
    private RoomSelectionModel model;
    @FXML private PasswordField passwordField;
    @FXML private Button backButton;
    @FXML private Button submitButton;

    public void initialize() {
        model = (RoomSelectionModel) SceneManager.getUserData();
    }

    @FXML
    private void onBackButton() {
        Stage submitStage = (Stage) backButton.getScene().getWindow();
        submitStage.close();
    }

    @FXML
    private void onSubmitButton() {
        Room selectedRoom = model.getSelectedRoom();
        String userInput = passwordField.getText();
        String roomPassword = selectedRoom.getPassword();
        if (userInput.equals(roomPassword)) {
            model.enterRoom(selectedRoom.getId());
            Stage submitStage = (Stage) backButton.getScene().getWindow();
            submitStage.close();
        }
    }

    @FXML
    private void onKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            onSubmitButton();
        }
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            Stage submitStage = (Stage) backButton.getScene().getWindow();
            submitStage.close();
        }
    }
}

