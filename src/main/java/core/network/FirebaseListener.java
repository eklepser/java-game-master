package core.network;

import core.logic.Client;
import controllers.common.callbacks.*;
import com.google.firebase.database.*;
import core.logic.Room;
import javafx.application.Platform;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class FirebaseListener {
    private static final HashMap<String, ChildEventListener> childEventListeners = new HashMap<>();
    private static final HashMap<String, ValueEventListener> valueEventListeners = new HashMap<>();

    private static DatabaseReference chatRef;
    private static DatabaseReference clientTeamRef;
    private static DatabaseReference globalGameStateRef;
    private static DatabaseReference gameMapRef;
    private static DatabaseReference currentTurnRef;
    private static DatabaseReference playersRef;

    public static void addChatListener(String roomId, ChatUpdateCallback callback) {
        ChildEventListener chatListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String newMessage = dataSnapshot.getValue(String.class);
                System.out.println("NEW MSG: " + newMessage);
                Platform.runLater(() -> callback.onMessageAdded(newMessage));
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override public void onCancelled(DatabaseError databaseError) { }
        };

        chatRef = FirebaseTools.getChatRefByRoomId(roomId);
        chatRef.addChildEventListener(chatListener);
        childEventListeners.put("chatListener", chatListener);
    }

    public static void addClientListener(String roomId, ClientUpdateCallback callback) {
        ValueEventListener clientTeamListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String newTeam = dataSnapshot.getValue(String.class);
                if (newTeam == null) return;
                Client.setClientTeam(newTeam);
                Platform.runLater(() -> callback.onClientTeamChanged(newTeam));
            }

            @Override public void onCancelled(DatabaseError databaseError) { }
        };

        clientTeamRef = FirebaseTools.getPlayersRefByRoomId(roomId).child(Client.getClientId()).child("team");
        clientTeamRef.addValueEventListener(clientTeamListener);
        valueEventListeners.put("clientTeamListener", clientTeamListener);
    }

    public static void addGameStateListener(String roomId, GameStateUpdateCallback callback) {
        ValueEventListener globalGameStateListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Platform.runLater(() -> {
                    String globalGameState = (String) dataSnapshot.getValue();
                    Client.CurrentGameState.setGlobalState(globalGameState);
                    switch (globalGameState) {
                        case "preparing":
                            Platform.runLater(callback::onGamePreparing);
                            break;
                        case "playing":
                            Platform.runLater(callback::onGameStarted);
                            break;
                        case "finished":
                            Platform.runLater(() -> callback.onGameFinished(false));
                            break;
                        case "finished with draw":
                            Platform.runLater(() -> callback.onGameFinished(true));
                            break;
                        case "paused":
                            Platform.runLater(callback::onGamePaused);
                            break;
                    }
                });
            }
            @Override public void onCancelled(DatabaseError databaseError) { }
        };

        AtomicBoolean isInitialLoad = new AtomicBoolean(true);
        ValueEventListener gameMapListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isInitialLoad.getAndSet(false)) return;
                Platform.runLater(() -> {
                    String newMap = dataSnapshot.getValue(String.class);
                    callback.onMapUpdated(newMap);
                });
            }
            @Override public void onCancelled(DatabaseError databaseError) { }
        };

        ValueEventListener currentTurnListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Client.CurrentGameState.setTurn(dataSnapshot.getValue(String.class));
                Platform.runLater(() -> {
                    String newTurn = dataSnapshot.getValue(String.class);
                    callback.onNewTurn(newTurn);
                });
            }
            @Override public void onCancelled(DatabaseError databaseError) { }
        };

        globalGameStateRef = FirebaseTools.getGameStateRefByRoomId(roomId).child("globalState");
        globalGameStateRef.addValueEventListener(globalGameStateListener);
        valueEventListeners.put("globalGameStateListener", globalGameStateListener);

        gameMapRef = FirebaseTools.getGameStateRefByRoomId(roomId).child("gameMap");
        gameMapRef.addValueEventListener(gameMapListener);
        valueEventListeners.put("gameMapListener", gameMapListener);

        currentTurnRef = FirebaseTools.getGameStateRefByRoomId(roomId).child("currentTurn");
        currentTurnRef.addValueEventListener(currentTurnListener);
        valueEventListeners.put("currentTurnListener", currentTurnListener);
    }

    public static void addPlayersListener(String roomId, PlayersUpdateCallback callback) {
        ChildEventListener playersListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Platform.runLater(() -> {
                    HashMap<String, String> playerInfo = getPlayerInfo(dataSnapshot);
                    System.out.println("new player " + playerInfo.get("name"));
                    callback.onPlayerAdded(playerInfo);
                });
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Platform.runLater(() -> {
                    HashMap<String, String> playerInfo = getPlayerInfo(dataSnapshot);
                    callback.onPlayerRemoved(playerInfo);
                });
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Platform.runLater(() -> {
                    HashMap<String, String> playerInfo = getPlayerInfo(dataSnapshot);
                    callback.onPlayerInfoChanged(playerInfo);
                });
            }

            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override public void onCancelled(DatabaseError databaseError) { }

            private HashMap<String, String> getPlayerInfo(DataSnapshot ds) {
                return new HashMap<>() {{
                    put("id", ds.getKey());
                    put("name", (String) ds.child("name").getValue());
                    Boolean isReady = ds.child("isReady").getValue(Boolean.class);
                    put("isReady", String.valueOf(isReady));
                    put("team", (String) ds.child("team").getValue());
                }};
            }
        };

        playersRef = FirebaseTools.getPlayersRefByRoomId(roomId);
        playersRef.addChildEventListener(playersListener);
        childEventListeners.put("playersListener", playersListener);
    }

    public static void addRoomListListener(RoomListUpdateCallback callback) {
        ChildEventListener roomListListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Platform.runLater(() -> {
                    Room room = getRoom(dataSnapshot);
                    callback.onRoomAdded(room);
                });
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Platform.runLater(() -> {
                    Room room = getRoom(dataSnapshot);
                    callback.onRoomRemoved(room);
                });
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Platform.runLater(() -> {
                    Room room = getRoom(dataSnapshot);
                    callback.onRoomChanged(room);
                });
            }

            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override public void onCancelled(DatabaseError databaseError) { }

            private Room getRoom(DataSnapshot ds) {
                Room room = new Room();
                room.setId(ds.getKey());
                room.setName((String) ds.child("info/name").getValue());
                room.setGameMode((String) ds.child("info/gameMode").getValue());
                room.setPassword((String) ds.child("info/password").getValue());
                room.setAllowToWatch((String) ds.child("info/allowToWatch").getValue());
                room.setSize((String) ds.child("info/size").getValue());
                room.setPlayersCount((int) ds.child("players").getChildrenCount());
                return room;
            }
        };

        FirebaseTools.roomsRef.addChildEventListener(roomListListener);
        childEventListeners.put("roomListListener", roomListListener);
    }

    public static void removeRoomListListener() {
        FirebaseTools.roomsRef.removeEventListener(childEventListeners.get("roomListListener"));
    }

    public static void removeAllListeners() {
        chatRef.removeEventListener(childEventListeners.get("chatListener"));
        clientTeamRef.removeEventListener(valueEventListeners.get("clientTeamListener"));
        currentTurnRef.removeEventListener(valueEventListeners.get("currentTurnListener"));
        globalGameStateRef.removeEventListener(valueEventListeners.get("globalGameStateListener"));
        gameMapRef.removeEventListener(valueEventListeners.get("gameMapListener"));
        playersRef.removeEventListener(childEventListeners.get("playersListener"));
    }
}

