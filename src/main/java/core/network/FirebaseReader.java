package core.network;

import com.google.firebase.database.*;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class FirebaseReader {
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
}
