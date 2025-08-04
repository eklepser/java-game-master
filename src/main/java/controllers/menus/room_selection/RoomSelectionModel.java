package controllers.menus.room_selection;

import core.logic.Room;
import core.network.FirebaseListener;
import core.network.FirebaseManager;
import core.scenes.SceneManager;
import core.scenes.ScenePath;
import javafx.application.Platform;

import java.io.IOException;
import java.util.HashMap;

public class RoomSelectionModel {
    private final HashMap<String, Room> allRooms = new HashMap<>();
    private String selectedRoomId;

    public HashMap<String, Room> getAllRooms() { return allRooms; }

    public void putRoom(Room room) { allRooms.put(room.getId(), room); }
    public void removeRoom(String id) { allRooms.remove(id); }

    public Room getSelectedRoom() { return allRooms.get(selectedRoomId); }
    public void setSelectedRoom(String id) { selectedRoomId = id; }

    public void enterRoom(String roomId) {
        FirebaseListener.removeRoomListListener();
        FirebaseManager.attachClient(roomId);
    }
}
