package controllers.menus.room_selection;

import core.logic.Room;
import core.scenes.SceneManager;
import core.network.FirebaseListener;
import core.scenes.ScenePath;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;

public class RoomSelectionController  {
    private RoomSelectionModel model;
    private final HashMap<String, String> searchFilters = new HashMap<>();

    @FXML private VBox roomListBox;
    @FXML private TextField searchTextField;
    @FXML private ComboBox<String> gameModeComboBox;
    @FXML private ToggleButton showLockedRoomsButton;
    @FXML private ToggleButton showFullRoomsButton;

    public void initialize() {
        model = new RoomSelectionModel();
        new RoomSelectionNetworkListener(model, this);
    }

    @FXML
    private void onRoomButtonClick(String roomId) throws IOException {
        model.setSelectedRoom(roomId);
        Room room = model.getSelectedRoom();
        String roomPassword = room.getPassword();
        if (!roomPassword.isEmpty()) {
            SceneManager.setUserData(model);
            Stage submitStage = SceneManager.loadModalScene(ScenePath.PASSWORD_CONFIRMATION, 300, 120);
            submitStage.showAndWait();
        }
        else model.enterRoom(roomId);
    }

    @FXML
    private void onBackButtonClick() throws IOException {
        SceneManager.loadScene(ScenePath.MAIN_MENU);
        FirebaseListener.removeRoomListListener();
    }

    @FXML
    private void onResetButton() {
        searchTextField.clear();
        gameModeComboBox.setValue("All games");
        showLockedRoomsButton.setSelected(true);
        showFullRoomsButton.setSelected(false);
        searchFilters.clear();
        updateRoomButtons();
    }

    @FXML
    private void onKeyPressed(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            SceneManager.loadScene(ScenePath.MAIN_MENU);
            FirebaseListener.removeRoomListListener();
        }
    }

    private void updateSearchFilters() {
        searchFilters.put("f_name", searchTextField.getText());
        searchFilters.put("f_mode", gameModeComboBox.getValue());
        searchFilters.put("f_locked", String.valueOf(showLockedRoomsButton.isSelected()));
        searchFilters.put("f_full", String.valueOf(showFullRoomsButton.isSelected()));
    }

    private boolean isMatchingFilters(Room room) {
        String fNameCorrected = searchFilters.get("f_name").toLowerCase().replaceAll("\\s", "");
        String nameCorrected = room.getName().toLowerCase().replaceAll("\\s", "");
        boolean isMatching = (nameCorrected.startsWith(fNameCorrected));

        String fMode = searchFilters.get("f_mode");
        String fLocked = searchFilters.get("f_locked");
        String fFull = searchFilters.get("f_full");
        String playersCount = String.valueOf(room.getPlayersCount());
        if (!fMode.equals("All games")) isMatching = isMatching && (room.getGameMode().equals(fMode));
        if (fLocked.equals("false")) isMatching = isMatching && (room.getPassword().isEmpty());
        if (fFull.equals("false")) isMatching = isMatching && !(room.getSize().equals(playersCount));

        return isMatching;
    }

    public void updateRoomButtons() {
        updateSearchFilters();
        roomListBox.getChildren().clear();
        for (String roomId : model.getAllRooms().keySet()) {
            Room room = model.getAllRooms().get(roomId);
            if (!isMatchingFilters(room)) continue;
            Button btn = new Button();
            btn.setGraphic(RoomSelectionUtils.getRoomButtonGraphics(room));
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction((_) -> {
                try { onRoomButtonClick(roomId); }
                catch (IOException e) {throw new RuntimeException(e); }
            });
            roomListBox.getChildren().add(btn);
        }
    }
}