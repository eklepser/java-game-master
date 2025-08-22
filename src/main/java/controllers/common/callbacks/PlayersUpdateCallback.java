package controllers.common.callbacks;

import core.logic.Player;

public interface PlayersUpdateCallback {
    void onPlayerAdded(Player player);
    void onPlayerRemoved(Player player);
    void onPlayerInfoChanged(Player player);
}
