package core.network;

import core.logic.Client;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class FirebaseReader {
    public static CompletableFuture<HashMap<String, HashMap<String, String>>> getAllRoomsInfo() {
        CompletableFuture<HashMap<String, HashMap<String, String>>> future = new CompletableFuture<>();
        HashMap<String, HashMap<String, String>> roomsInfo = new HashMap<>();

        FirebaseTools.roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> rooms = dataSnapshot.getChildren();
                for (DataSnapshot ds : rooms) {
                    HashMap<String, String> roomInfo = new HashMap<>() {{
                        put("name", (String) ds.child("info/name").getValue());
                        put("gameMode", (String) ds.child("info/gameMode").getValue());
                        put("password", (String) ds.child("info/password").getValue());
                        put("allowToWatch", (String) ds.child("info/allowToWatch").getValue());
                        put("size", (String) ds.child("info/size").getValue());
                        put("playersCount", String.valueOf(ds.child("players").getChildrenCount()));
                    }};
                    roomsInfo.put(ds.getKey(), roomInfo);
                }
                future.complete(roomsInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("getAllRoomsInfo -> ERROR");
            }
        });
        return future;
    }

    public static CompletableFuture<HashMap<String, String>> getRoomInfo() {
        return getRoomInfo(Client.CurrentRoom.getRoomId());
    }

    public static CompletableFuture<HashMap<String, String>> getRoomInfo(String roomId) {
        CompletableFuture<HashMap<String, String>> future = new CompletableFuture<>();

        FirebaseTools.getRoomInfoRefByRoomId(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                HashMap<String, String> roomInfo = new HashMap<>() {{
                    put("name", (String) ds.child("name").getValue());
                    put("gameMode", (String) ds.child("gameMode").getValue());
                    put("password", (String) ds.child("password").getValue());
                    put("allowToWatch", (String) ds.child("allowToWatch").getValue());
                    put("size", (String) ds.child("size").getValue());
                }};
                future.complete(roomInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("getAllRoomsInfo -> ERROR");
            }
        });
        return future;
    }

    public static CompletableFuture<Integer> getPlayersReadyAmount(String roomId) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        Integer[] playersReadyAmount = { 0 };

        FirebaseTools.getPlayersRefByRoomId(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> players = dataSnapshot.getChildren();
                for (DataSnapshot ds : players) {
                    if (ds.child("isReady").getValue(Boolean.class) == true) {
                        playersReadyAmount[0] += 1;
                    }
                }
                future.complete(playersReadyAmount[0]);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("getAllRoomsInfo -> ERROR");
            }
        });
        return future;
    }

    public static CompletableFuture<ArrayList<String>> getChatMessages(String roomId) {
        CompletableFuture<ArrayList<String>> future = new CompletableFuture<>();
        ArrayList<String> messagesList = new ArrayList<>();

        FirebaseTools.getChatRefByRoomId(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> messages = dataSnapshot.getChildren();
                for (DataSnapshot ds : messages) {
                    String message = (String) ds.getValue();
                    messagesList.add(message);
                }
                future.complete(messagesList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("getChatMessages -> ERROR");
            }
        });
        return future;
    }

    public static CompletableFuture<Boolean> getGlobalGameState(String roomId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Boolean messagesList = true;

        FirebaseTools.getChatRefByRoomId(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> messages = dataSnapshot.getChildren();

                future.complete(messagesList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("getChatMessages -> ERROR");
            }
        });
        return future;
    }

    public static CompletableFuture<String> getPlayerTeam(String roomId, String playerId) {
        CompletableFuture<String> future = new CompletableFuture<>();

        DatabaseReference playerRef =  FirebaseTools.getPlayersRefByRoomId(roomId).child(playerId);
        playerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String team = dataSnapshot.child("team").getValue(String.class);
                future.complete(team);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("getPlayerTeam -> ERROR");
            }
        });
        return future;
    }
}
