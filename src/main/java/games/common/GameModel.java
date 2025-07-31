package games.common;

import core.logic.Client;
import core.logic.GameState;
import core.logic.Room;
import core.network.FirebaseListener;
import core.network.FirebaseReader;
import games.common.callbacks.*;
import java.util.HashMap;

public class GameModel implements
        PlayersUpdateCallback, ChatUpdateCallback, GameStateUpdateCallback, ClientUpdateCallback {

    protected Room currentRoom;
    protected String roomId;
    protected GameState currentGameState;
    protected HashMap<String, HashMap<String, String>> playersInfo = new HashMap<>();

    public void initialize() {
        FirebaseReader.getRoomInfo().thenAccept(roomInfo -> {
            currentRoom.setRoomInfo(roomInfo);
        });

        currentRoom = Client.CurrentRoom;
        roomId = currentRoom.getRoomId();
        currentGameState = Client.CurrentGameState;

        FirebaseListener.addPlayersListener(roomId, this);
        FirebaseListener.addChatListener(roomId, this);
        FirebaseListener.addGameStateListener(roomId, this);
        FirebaseListener.addClientListener(roomId, this);

        extraInitialize();
    }

    protected void extraInitialize() {

    }

    @Override
    public void onMessageAdded(String newMessage) {

    }

    @Override
    public void onClientTeamChanged(String newTeam) {

    }

    @Override
    public void onGamePreparing() {

    }

    @Override
    public void onGameStarted() {

    }

    @Override
    public void onGameFinished() {

    }

    @Override
    public void onGamePaused() {

    }

    @Override
    public void onMapUpdated(String newMap) {

    }

    @Override
    public void onNewTurn(String currentTurn) {

    }

    @Override
    public void onPlayerAdded(HashMap<String, String> playerInfo) {

    }

    @Override
    public void onPlayerRemoved(HashMap<String, String> playerInfo) {

    }

    @Override
    public void onPlayerInfoChanged(HashMap<String, String> playerInfo) {

    }
}
