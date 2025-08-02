package controller.menus.main_menu;

import core.logic.Client;
import core.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TextField;

import java.io.IOException;

public class MainMenuController {
    @FXML
    private Label playerNameLabel;
    @FXML
    private TextField textField = new TextField();

    public void initialize() {
        playerNameLabel.setText("Your name: " + Client.getClientName());
    }

    @FXML
    protected void onRoomCreationButtonClick() throws IOException {
        SceneManager.loadScene("menus/room_creation.fxml");
    }

    @FXML
    protected void onRoomSelectionButtonClick() throws IOException {
        SceneManager.loadScene("menus/room_selection.fxml");
    }

    public void onKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            String playerName = textField.getText();
            if (playerName.isBlank()) playerName = Client.getClientName();
            else {
                Client.setClientName(playerName);
                textField.setFocusTraversable(false);
                textField.getParent().requestFocus();
            }
            playerNameLabel.setText("Your name: " + playerName);
        }
    }

    public void onExitButton() {
        SceneManager.exit();
    }
}