package controllers.menus.room_selection;

import java.util.HashMap;

public class RoomSelectionModel {
    private final HashMap<String, HashMap<String, String>> roomsInfo = new HashMap<>();
    private final HashMap<String, String> searchFilters = new HashMap<>();

    public HashMap<String, HashMap<String, String>> getRoomsInfo() { return roomsInfo; }

    public void putRoomInfo(HashMap<String, String> roomInfo) { roomsInfo.put(roomInfo.get("id"), roomInfo); }

    public void removeRoomInfo(String id) { roomsInfo.remove(id); }

    public void updateSearchFilters(String fname) {
        searchFilters.put("fname", fname);
        System.out.println("Filters updated: " + searchFilters);
    }

    public void clearSearchFilters() {
        searchFilters.clear();
    }

    public boolean isMatchingFilters(HashMap<String, String> roomInfo) {
        Boolean isMatching = (roomInfo.get("name").startsWith(searchFilters.get("fname")));
        System.out.println(isMatching);
        return isMatching;
    }
}
