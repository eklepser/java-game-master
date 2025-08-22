package core.network;

import core.logic.Client;
import com.google.firebase.database.DatabaseReference;
import core.logic.Player;
import core.logic.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class FirebaseWriter {
    private static final Random random = new Random();

    public static String addRoom(Room room) {
       return addRoom(room, 2);
    }

    public static String addRoom(Room room, int roomSize) {
        DatabaseReference newRoomRef = FirebaseTools.roomsRef.push();
        String roomId = newRoomRef.getKey();
        DatabaseReference newRoomInfoRef = FirebaseTools.getRoomInfoRefByRoomId(roomId);
        DatabaseReference newRoomGameStateRef = FirebaseTools.getGameStateRefByRoomId(roomId);

        newRoomInfoRef.child("name").setValueAsync(room.getName());
        newRoomInfoRef.child("gameMode").setValueAsync(room.getGameMode());
        newRoomInfoRef.child("password").setValueAsync(room.getPassword());
        newRoomInfoRef.child("allowToWatch").setValueAsync(room.getAllowToWatch());
        newRoomInfoRef.child("size").setValueAsync(roomSize);

        newRoomGameStateRef.child("globalState").setValueAsync("preparing");
        newRoomGameStateRef.child("currentTurn").setValueAsync("0");
        newRoomGameStateRef.child("gameMap").setValueAsync("---------");
        return roomId;
    }

    public static void removeRoom(String roomId) {
        DatabaseReference roomRef = FirebaseTools.getRoomRefByRoomId(roomId);
        roomRef.removeValueAsync();
    }

    public static String addClientToRoom(String roomId) {
        DatabaseReference playersRef = FirebaseTools.getPlayersRefByRoomId(roomId);
        DatabaseReference newPlayerRef = playersRef.push();
        newPlayerRef.child("name").setValueAsync(Client.getClientName());
        newPlayerRef.child("team").setValueAsync("none");
        newPlayerRef.child("isReady").setValueAsync(false);
        return newPlayerRef.getKey(); //returning newClientId
    }

    public static void addMessageToRoom(String roomId, String message) {
        DatabaseReference chatRef = FirebaseTools.getChatRefByRoomId(roomId);
        DatabaseReference newMessageRef = chatRef.push();
        newMessageRef.setValueAsync(message);
    }

    public static void setGameMap(String roomId, String newGameMap) {
        DatabaseReference gameMapRef = FirebaseTools.getGameStateRefByRoomId(roomId);
        gameMapRef.child("gameMap").setValueAsync(newGameMap);
    }

    public static void setGameGlobalState(String roomId, String globalGameState) {
        DatabaseReference gameStateRef = FirebaseTools.getGameStateRefByRoomId(roomId);
        gameStateRef.child("globalState").setValueAsync(globalGameState);
    }

    public static void setPlayerIsReady(String roomId, String playerId, boolean isReady) {
        DatabaseReference playerRef = FirebaseTools.getPlayersRefByRoomId(roomId);
        playerRef.child(playerId).child("isReady").setValueAsync(isReady);
    }

    public static void setPlayerTeam(String roomId, String playerId, String team) {
        DatabaseReference playerRef = FirebaseTools.getPlayersRefByRoomId(roomId);
        playerRef.child(playerId).child("team").setValueAsync(team);
    }

    public static void setCurrentTurn(String roomId, String currentTurn) {
        DatabaseReference currentTurnRef = FirebaseTools.getGameStateRefByRoomId(roomId);
        currentTurnRef.child("currentTurn").setValueAsync(currentTurn);
    }

    public static void removeClientFromRoom(String clientId, String roomId) {
        DatabaseReference playersRef = FirebaseTools.getPlayersRefByRoomId(roomId);
        playersRef.child(clientId).removeValueAsync();
    }

    public static void setRandomTeams(String roomId, HashMap<String, Player> allPlayers) {
        ArrayList<String> teams = new ArrayList<>();
        int team = random.nextInt(2);
        teams.add(String.valueOf(team));
        System.out.println("set random team: " + team);
        teams.add(String.valueOf(Math.abs(team - 1)));

        int i = 0;
        for (Player player : allPlayers.values()) {
            setPlayerTeam(roomId, player.getId(), teams.get(i));
            i++;
            if (i == 2) i = 0;
        }
    }
}
