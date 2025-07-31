package games.common.controllers;

import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import core.SceneManager;
import core.network.FirebaseManager;
import core.network.FirebaseReader;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.fxml.FXML;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class RoomSelectionController {
    @FXML
    private VBox container;

    public void initialize() throws IOException {
        onUpdateButtonClick();
    }

    @FXML
    protected void onUpdateButtonClick() throws IOException {
        FirebaseReader.getAllRoomsInfo().thenAccept(roomsInfo -> {
            Platform.runLater(() -> makeRoomButtons(roomsInfo));
        });
        System.out.println("room list updated");
    }

    private void makeRoomButtons(HashMap<String, HashMap<String, String>> roomsInfo) {
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
            gp.add(new Label("1/2"), 0, 1);
            gp.add(new Label(roomInfo.get("name") + "\ntictactoe"), 1, 1);
            gp.add(new Label(getRoomInfoIcons(roomInfo)), 2, 1);
            gp.setMaxWidth(Double.MAX_VALUE);

            Button btn = new Button();
            btn.setGraphic(gp);
            btn.setContentDisplay(ContentDisplay.LEFT);
            btn.setMaxWidth(1000);
            btn.setOnAction(event -> {
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
    protected void onRoomButtonClick(String roomId) throws IOException {
        FirebaseManager.attachClient(roomId);
        Platform.runLater(() -> {
            try {
                SceneManager.loadScene("games/tictactoe_classic.fxml");
                System.out.println("Client added to room " + roomId);
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

    private String getRoomInfoIcons(HashMap<String, String> roomInfo) {
        System.out.println(roomInfo);
        StringBuilder icons = new StringBuilder();
        if (!roomInfo.get("password").isBlank()) icons.append("\uD83D\uDD12");
        if (Objects.equals(roomInfo.get("allowToWatch"), "true")) icons.append("\uD83D\uDC41");
        return icons.toString();
    }
}