package controller.common;

import controller.common.callbacks.*;
import java.util.HashMap;

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
    public void onGameFinished() { }

    @Override
    public void onGamePaused() { }

    @Override
    public void onMapUpdated(String newMap) { }

    @Override
    public void onNewTurn(String currentTurn) { }

    @Override
    public void onPlayerAdded(HashMap<String, String> playerInfo) { }

    @Override
    public void onPlayerRemoved(HashMap<String, String> playerInfo) { }

    @Override
    public void onPlayerInfoChanged(HashMap<String, String> playerInfo) { }
}
