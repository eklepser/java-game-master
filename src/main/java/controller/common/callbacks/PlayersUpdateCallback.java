package controller.common.callbacks;

import java.util.HashMap;

public interface PlayersUpdateCallback {
    void onPlayerAdded(HashMap<String, String> playerInfo);
    void onPlayerRemoved(HashMap<String, String> playerInfo);
    void onPlayerInfoChanged(HashMap<String, String> playerInfo);
}
