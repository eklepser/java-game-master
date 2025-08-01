package games.common.controllers;

import core.SceneManager;
import core.network.FirebaseListener;
import core.network.FirebaseManager;
import games.common.callbacks.RoomListUpdateCallback;
import javafx.geometry.HPos;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.fxml.FXML;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class RoomSelectionController implements RoomListUpdateCallback {
    private final HashMap<String, HashMap<String, String>> roomsInfo = new HashMap<>();

    @FXML private VBox container;

    public void initialize() {
        //onUpdateButtonClick();
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
        container.getChildren().clear();
        for (String roomId : roomsInfo.keySet()) {
            HashMap<String, String> roomInfo = roomsInfo.get(roomId);

            GridPane gp = new GridPane();
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setHalignment(HPos.LEFT);
            col1.setPercentWidth(20);
            col1.setHgrow(Priority.NEVER);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setHalignment(HPos.CENTER);
            col2.setHgrow(Priority.ALWAYS);
            ColumnConstraints col3 = new ColumnConstraints();
            col3.setHalignment(HPos.RIGHT);
            col3.setPercentWidth(20);
            col3.setHgrow(Priority.NEVER);
            gp.getColumnConstraints().addAll(col1, col2, col3);

            String playersCount = roomInfo.get("playersCount");
            String size = roomInfo.get("size");
            gp.add(new Label(playersCount + "/" + size), 0, 1);

            gp.add(new Label(roomInfo.get("name") + "\ntictactoe"), 1, 1);
            gp.add(new Label(getRoomInfoIcons(roomInfo)), 2, 1);
            gp.setMaxWidth(Double.MAX_VALUE);

            Button btn = new Button();
            btn.setGraphic(gp);
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

    private String getRoomInfoIcons(HashMap<String, String> roomInfo) {
        StringBuilder icons = new StringBuilder();
        String password = roomInfo.get("password");
        if ((password != null) && !password.isBlank()) icons.append("\uD83D\uDD12");
        if (Objects.equals(roomInfo.get("allowToWatch"), "true")) icons.append("\uD83D\uDC41");
        return icons.toString();
    }
}