package controllers.menus.room_selection;

import core.scenes.SceneManager;
import core.network.FirebaseListener;
import core.network.FirebaseManager;
import core.scenes.ScenePath;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.HashMap;

public class RoomSelectionController  {
    private RoomSelectionModel model;
    private RoomSelectionNetworkListener network;
    private final HashMap<String, String> searchFilters = new HashMap<>();
    private boolean isPasswordCorrect = false;
    private Stage submitStage;

    @FXML private VBox roomListBox;
    @FXML private HBox searchFiltersBox;
    @FXML private TextField searchTextField;
    @FXML private ComboBox<String> gameModeComboBox;
    @FXML private ToggleButton showLockedRoomsButton;
    @FXML private ToggleButton showFullRoomsButton;
    @FXML private Button submitButton;

    public void initialize() {
        model = new RoomSelectionModel();
        network = new RoomSelectionNetworkListener(model, this);

        submitButton = new Button("Enter");
        submitButton.setPrefWidth(100);
    }

    private void updateSearchFilters() {
        searchFilters.put("f_name", searchTextField.getText());
        searchFilters.put("f_mode", gameModeComboBox.getValue());
        searchFilters.put("f_locked", String.valueOf(showLockedRoomsButton.isSelected()));
        searchFilters.put("f_full", String.valueOf(showFullRoomsButton.isSelected()));
        System.out.println("Filters updated: " + searchFilters);
    }

    private boolean isMatchingFilters(HashMap<String, String> roomInfo) {
        String fNameCorrected = searchFilters.get("f_name").toLowerCase().replaceAll("\\s", "");;
        String nameCorrected = roomInfo.get("name").toLowerCase().replaceAll("\\s", "");;
        boolean isMatching = (nameCorrected.startsWith(fNameCorrected));

        String fMode = searchFilters.get("f_mode");
        if (!fMode.equals("All games")) isMatching = isMatching && (roomInfo.get("gameMode").equals(fMode));

        String fLocked = searchFilters.get("f_locked");
        if (fLocked.equals("false")) isMatching = isMatching && (roomInfo.get("password").isEmpty());

        String fFull = searchFilters.get("f_full");
        if (fFull.equals("false")) isMatching = isMatching && !(roomInfo.get("size").equals(roomInfo.get("playersCount")));

        return isMatching;
    }

    public void updateRoomButtons() {
        updateSearchFilters();
        roomListBox.getChildren().clear();
        for (String roomId : model.getRoomsInfo().keySet()) {
            HashMap<String, String> roomInfo = model.getRoomsInfo().get(roomId);
            if (!isMatchingFilters(roomInfo)) continue;

            Button btn = new Button();
            btn.setGraphic(RoomSelectionUtils.getRoomButtonGraphics(roomInfo));
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction((_) -> {
                try { onRoomButtonClick(roomId); }
                catch (IOException e) {throw new RuntimeException(e); }
            });
            roomListBox.getChildren().add(btn);
        }
    }

    @FXML
    private void onRoomButtonClick(String roomId) throws IOException {
        HashMap<String, String> roomInfo = model.getRoomsInfo().get(roomId);
        String roomPassword = roomInfo.get("password");
        if (!roomPassword.isEmpty()) {
            Stage submitStage = SceneManager.loadModalScene(ScenePath.PASSWORD_CONFIRMATION);
            //Stage submitStage = showPasswordSubmitStage(roomPassword);
            submitStage.showAndWait();
            if (isPasswordCorrect) {
                enterRoom(roomId);
            }
        }
        else enterRoom(roomId);
    }

    private void onSubmitButton(String userPassword, String roomPassword) {
        if (userPassword.equals(roomPassword))
        {
            isPasswordCorrect = true;
            submitStage.close();
        }
    }

    private void enterRoom(String roomId) {
        FirebaseManager.attachClient(roomId);
        Platform.runLater(() -> {
            try {
                SceneManager.loadScene(ScenePath.TICTACTOE_CLASSIC);
                FirebaseListener.removeRoomListListener();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void onResetButton() {
        searchTextField.clear();
        gameModeComboBox.setValue("All games");
        showLockedRoomsButton.setSelected(true);
        showFullRoomsButton.setSelected(false);
        searchFilters.clear();
        updateRoomButtons();
    }

    @FXML
    private void onBackButtonClick() throws IOException {
        SceneManager.loadScene(ScenePath.MAIN_MENU);
        FirebaseListener.removeRoomListListener();
    }

    public void onKeyPressed(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            SceneManager.loadScene(ScenePath.MAIN_MENU);
            FirebaseListener.removeRoomListListener();
        }
    }
}