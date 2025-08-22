package controllers.common;

import controllers.common.callbacks.*;
import core.logic.Player;

public abstract class GameNetworkListener implements
        PlayersUpdateCallback, ChatUpdateCallback, GameStateUpdateCallback, ClientUpdateCallback {

    @Override
    public void onMessageAdded(String newMessage) { }

    @Override
    public void onClientTeamChanged(String newTeam) { }

    @Override
    public void onGamePreparing() { }

    @Override
    public void onGameStarted() { }

    @Override
    public void onGameFinished(boolean isDraw) { }

    @Override
    public void onGamePaused() { }

    @Override
    public void onMapUpdated(String newMap) { }

    @Override
    public void onNewTurn(String currentTurn) { }

    @Override
    public void onPlayerAdded(Player player) { }

    @Override
    public void onPlayerRemoved(Player player) { }

    @Override
    public void onPlayerInfoChanged(Player player) { }
}
