package controllers.common.callbacks;

import core.logic.Room;

public interface RoomListUpdateCallback {
    void onRoomAdded(Room room);
    void onRoomRemoved(Room room);
    void onRoomChanged(Room room);
}
