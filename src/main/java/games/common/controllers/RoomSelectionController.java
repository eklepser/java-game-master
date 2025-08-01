package games.common.controllers;

import core.SceneManager;
import core.logic.Client;
import core.network.FirebaseListener;
import core.network.FirebaseManager;
import games.common.callbacks.RoomListUpdateCallback;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.fxml.FXML;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class RoomSelectionController implements RoomListUpdateCallback {
    private final HashMap<String, HashMap<String, String>> roomsInfo = new HashMap<>();
    private final HashMap<String, String> searchFilters = new HashMap<>();

    @FXML private VBox container;
    @FXML private TextField searchTextField;

    public void initialize() {
        FirebaseListener.addRoomListListener(this);
    }

    @Override
    public void onRoomAdded(HashMap<String, String> roomInfo) {
        roomsInfo.put(roomInfo.get("id"), roomInfo);
        makeRoomButtons();
        System.out.println("NEW ROOM ADDED");
    }

    @Override
    public void onRoomRemoved(HashMap<String, String> roomInfo) {
        roomsInfo.remove(roomInfo.get("id"));
        makeRoomButtons();
        System.out.println("ROOM REMOVED");
    }

    @Override
    public void onRoomChanged(HashMap<String, String> roomInfo) {
        roomsInfo.put(roomInfo.get("id"), roomInfo);
        makeRoomButtons();
        System.out.println("ROOM CHANGED");
    }

    private void makeRoomButtons() {
        updateSearchFilters();
        container.getChildren().clear();
        for (String roomId : roomsInfo.keySet()) {
            HashMap<String, String> roomInfo = roomsInfo.get(roomId);
            if (!RoomSelectionUtils.isMatchingFilters(roomInfo, searchFilters)) continue;

            BorderPane bp = new BorderPane();
            String playersCount = roomInfo.get("playersCount");
            String size = roomInfo.get("size");

            bp.setLeft(new Label(" " + roomInfo.get("gameMode") + " " + roomInfo.get("name")));
            bp.setRight(new Label(playersCount + "/" + size + " " + getRoomInfoIcons(roomInfo)));

            Button btn = new Button();
            btn.setGraphic(bp);
            btn.setContentDisplay(ContentDisplay.LEFT);
            btn.setMaxWidth(1000);
            btn.setOnAction((_) -> {
                try {
                    onRoomButtonClick(roomId);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            container.getChildren().add(btn);
        }
    }

    @FXML
    private void onRoomButtonClick(String roomId) throws IOException {
        FirebaseManager.attachClient(roomId);
        Platform.runLater(() -> {
            try {
                SceneManager.loadScene("games/tictactoe_classic.fxml");
                System.out.println("Client added to room " + roomId);
                FirebaseListener.removeRoomListListener();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void onBackButtonClick() throws IOException {
        SceneManager.loadScene("common/main_menu.fxml");
    }

    public void onKeyReleased() {
        updateSearchFilters();
        makeRoomButtons();
    }

    private String getRoomInfoIcons(HashMap<String, String> roomInfo) {
        StringBuilder icons = new StringBuilder();
        String password = roomInfo.get("password");
        if ((password != null) && !password.isBlank()) icons.append("\uD83D\uDD12");
        if (Objects.equals(roomInfo.get("allowToWatch"), "true")) icons.append("\uD83D\uDC41");
        return icons.toString();
    }

    private void updateSearchFilters() {
        searchFilters.put("fname", searchTextField.getText());
        System.out.println("Filters updated: " + searchFilters);
    }

    public void onResetButton() {
        searchTextField.clear();
        searchFilters.clear();
        makeRoomButtons();
    }
}