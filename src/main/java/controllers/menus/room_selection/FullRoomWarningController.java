package controllers.menus.room_selection;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class FullRoomWarningController {
    @FXML private Button okButton;

    @FXML
    private void onOkButton() {
        Stage submitStage = (Stage) okButton.getScene().getWindow();
        submitStage.close();
    }

    @FXML
    private void onKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            onOkButton();
        }
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            onOkButton();
        }
    }
}
