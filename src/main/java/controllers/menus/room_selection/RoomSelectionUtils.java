package controllers.menus.room_selection;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.HashMap;

public class RoomSelectionUtils {
    public static String getRoomInfoIcons(HashMap<String, String> roomInfo) {
        StringBuilder icons = new StringBuilder();
        String password = roomInfo.get("password");
        if ((password != null) && !password.isBlank()) icons.append("\uD83D\uDD12");
        return icons.toString();
    }

    public static BorderPane getRoomButtonGraphics(HashMap<String, String> roomInfo) {
        Text name = new Text(roomInfo.get("name"));
        Text mode = new Text(" (" + roomInfo.get("gameMode") + ")");
        mode.setStyle("-fx-font-style: italic; -fx-fill: derive(-fx-text-fill, 40%);");
        TextFlow leftTextFlow = new TextFlow(name, mode);
        leftTextFlow.setStyle("-fx-font-size: 16px;");

        String playersCount = roomInfo.get("playersCount");
        String size = roomInfo.get("size");
        Text fullness = new Text(playersCount + "/" + size);
        Text status = new Text(getRoomInfoIcons(roomInfo));
        TextFlow rightTextFlow = new TextFlow(fullness, status);
        rightTextFlow.setStyle("-fx-font-size: 16px;");

        VBox leftBox = new VBox(leftTextFlow);
        leftBox.setAlignment(Pos.CENTER_LEFT);
        VBox rightBox = new VBox(rightTextFlow);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(leftBox);
        borderPane.setRight(rightBox);
        return borderPane;
    }
}
