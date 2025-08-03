package controllers.menus.room_selection;

import core.scenes.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class PasswordConfirmationController {
    @FXML private PasswordField passwordField;
    @FXML private Button backButton;
    @FXML private Button submitButton;

    public void initialize() {

    }

    public void onBackButton(ActionEvent actionEvent) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    public void onSubmitButton(ActionEvent actionEvent) {
    }
}

