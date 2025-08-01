package games.common.controllers;

import java.util.HashMap;

public class RoomSelectionUtils {
    public static boolean isMatchingFilters(HashMap<String, String> roomInfo, HashMap<String, String> filters) {
        Boolean isMatching = (roomInfo.get("name").startsWith(filters.get("fname")));
        System.out.println(isMatching);
        return isMatching;
    }
}
