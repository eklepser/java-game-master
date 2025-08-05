package controllers.common.callbacks;

import core.logic.Player;

import java.util.HashMap;

public interface PlayersUpdateCallback {
    void onPlayerAdded(Player player);
    void onPlayerRemoved(Player player);
    void onPlayerInfoChanged(Player player);
}
