package controllers.menus.room_selection;

import core.network.FirebaseListener;
import core.network.FirebaseManager;
import core.scenes.SceneManager;
import core.scenes.ScenePath;
import javafx.application.Platform;

import java.io.IOException;
import java.util.HashMap;

public class RoomSelectionModel {
    private final HashMap<String, HashMap<String, String>> roomsInfo = new HashMap<>();
    private String selectedRoomId;

    public HashMap<String, HashMap<String, String>> getRoomsInfo() { return roomsInfo; }

    public void putRoomInfo(HashMap<String, String> roomInfo) { roomsInfo.put(roomInfo.get("id"), roomInfo); }

    public void removeRoomInfo(String id) { roomsInfo.remove(id); }

    public void setSelectedRoom(String roomId) { selectedRoomId = roomId; }

    public HashMap<String, String> getSelectedRoomInfo() { return roomsInfo.get(selectedRoomId); }

    public void enterRoom(String roomId) {
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
}
