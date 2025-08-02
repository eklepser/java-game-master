package controllers.menus.room_selection;

import controllers.common.callbacks.RoomListUpdateCallback;
import core.network.FirebaseListener;
import java.util.HashMap;

public class RoomSelectionNetworkListener implements RoomListUpdateCallback {
    private final RoomSelectionModel model;
    private final RoomSelectionController controller;

    public RoomSelectionNetworkListener(RoomSelectionModel model, RoomSelectionController controller) {
        this.model = model;
        this.controller = controller;
        FirebaseListener.addRoomListListener(this);
    }

    @Override
    public void onRoomAdded(HashMap<String, String> roomInfo) {
        model.putRoomInfo(roomInfo);
        controller.updateRoomButtons();
        System.out.println("NEW ROOM ADDED");
    }

    @Override
    public void onRoomRemoved(HashMap<String, String> roomInfo) {
        model.removeRoomInfo(roomInfo.get("id"));
        controller.updateRoomButtons();
        System.out.println("ROOM REMOVED");
    }

    @Override
    public void onRoomChanged(HashMap<String, String> roomInfo) {
        model.putRoomInfo(roomInfo);
        controller.updateRoomButtons();
        System.out.println("ROOM CHANGED");
    }
}
