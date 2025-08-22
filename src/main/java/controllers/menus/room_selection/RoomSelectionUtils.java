package controllers.menus.room_selection;

import core.logic.Room;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class RoomSelectionUtils {
    public static BorderPane getRoomButtonGraphics(Room room) {
        Text name = new Text(room.getName());
        Text mode = new Text(" (" + room.getGameMode() + ")");
        mode.setStyle("-fx-font-style: italic; -fx-fill: derive(-fx-text-fill, 40%);");
        TextFlow leftTextFlow = new TextFlow(name, mode);
        leftTextFlow.setStyle("-fx-font-size: 16px;");

        Text fullness = new Text(room.getPlayersCount() + "/" + room.getSize());
        Text status = new Text(getRoomInfoIcons(room));
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

    private static String getRoomInfoIcons(Room room) {
        StringBuilder icons = new StringBuilder();
        String password = room.getPassword();
        if ((password != null) && !password.isBlank()) icons.append("\uD83D\uDD12");
        return icons.toString();
    }
}
