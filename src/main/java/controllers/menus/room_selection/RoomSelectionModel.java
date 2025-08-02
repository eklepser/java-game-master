package controllers.menus.room_selection;

import java.util.HashMap;

public class RoomSelectionModel {
    private final HashMap<String, HashMap<String, String>> roomsInfo = new HashMap<>();

    public HashMap<String, HashMap<String, String>> getRoomsInfo() { return roomsInfo; }

    public void putRoomInfo(HashMap<String, String> roomInfo) { roomsInfo.put(roomInfo.get("id"), roomInfo); }

    public void removeRoomInfo(String id) { roomsInfo.remove(id); }
}
