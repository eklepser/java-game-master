package controllers.menus.room_selection;

import controllers.common.callbacks.RoomListUpdateCallback;
import core.logic.Room;
import core.network.FirebaseListener;

public class RoomSelectionNetworkListener implements RoomListUpdateCallback {
    private final RoomSelectionModel model;
    private final RoomSelectionController controller;

    public RoomSelectionNetworkListener(RoomSelectionModel model, RoomSelectionController controller) {
        this.model = model;
        this.controller = controller;
        FirebaseListener.addRoomListListener(this);
    }

    @Override
    public void onRoomAdded(Room room) {
        model.putRoom(room);
        controller.updateRoomButtons();
    }

    @Override
    public void onRoomRemoved(Room room) {
        model.removeRoom(room.getId());
        controller.updateRoomButtons();
    }

    @Override
    public void onRoomChanged(Room room) {
        model.putRoom(room);
        controller.updateRoomButtons();
    }
}
