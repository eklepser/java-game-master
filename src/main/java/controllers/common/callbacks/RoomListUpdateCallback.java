package controllers.common.callbacks;

import java.util.HashMap;

public interface RoomListUpdateCallback {
    void onRoomAdded(HashMap<String, String> roomInfo);
    void onRoomRemoved(HashMap<String, String> roomInfo);
    void onRoomChanged(HashMap<String, String> roomInfo);
}
