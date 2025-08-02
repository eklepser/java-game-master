package controllers.menus.room_selection;

import core.SceneManager;
import core.network.FirebaseListener;
import core.network.FirebaseManager;
import javafx.application.Platform;
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

public class RoomSelectionController  {
    private RoomSelectionModel model;

    @FXML private VBox container;
    @FXML private TextField searchTextField;

    public void initialize() {
        model = new RoomSelectionModel();
        RoomSelectionNetworkListener network = new RoomSelectionNetworkListener(model, this);
    }

    public void makeRoomButtons() {
        model.updateSearchFilters(searchTextField.getText());
        container.getChildren().clear();
        for (String roomId : model.getRoomsInfo().keySet()) {
            HashMap<String, String> roomInfo = model.getRoomsInfo().get(roomId);
            if (!model.isMatchingFilters(roomInfo)) continue;

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
        SceneManager.loadScene("menus/main_menu.fxml");
    }

    public void onKeyReleased() {
        model.updateSearchFilters(searchTextField.getText());
        makeRoomButtons();
    }

    private String getRoomInfoIcons(HashMap<String, String> roomInfo) {
        StringBuilder icons = new StringBuilder();
        String password = roomInfo.get("password");
        if ((password != null) && !password.isBlank()) icons.append("\uD83D\uDD12");
        if (Objects.equals(roomInfo.get("allowToWatch"), "true")) icons.append("\uD83D\uDC41");
        return icons.toString();
    }

    public void onResetButton() {
        searchTextField.clear();
        model.clearSearchFilters();
        makeRoomButtons();
    }
}