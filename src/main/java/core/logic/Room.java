package core.logic;

import java.util.HashMap;

public class Room {
    private String currentRoomId;
    private HashMap<String, String> currentRoomInfo;

    public Room() {
        currentRoomId = "";
        currentRoomInfo = new HashMap<>();
    }

    public void setRoomId(String roomId) {
        currentRoomId = roomId;
    }
    public String getRoomId() {
        return currentRoomId;
    }

    public void setRoomInfo(HashMap<String, String> roomInfo) { currentRoomInfo = roomInfo; }
    public HashMap<String, String> getRoomInfo() { return currentRoomInfo; }
}
