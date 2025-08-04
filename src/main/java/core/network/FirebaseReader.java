package core.network;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import core.logic.Room;

import java.util.concurrent.CompletableFuture;

public class FirebaseReader {
    public static CompletableFuture<Room> getRoom(String roomId) {
        CompletableFuture<Room> future = new CompletableFuture<>();

        FirebaseTools.getRoomInfoRefByRoomId(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                Room room = new Room();
                room.setId(roomId);
                room.setName((String) ds.child("name").getValue());
                room.setGameMode((String) ds.child("gameMode").getValue());
                room.setPassword((String) ds.child("password").getValue());
                room.setAllowToWatch((String) ds.child("allowToWatch").getValue());
                room.setSize((String) ds.child("size").getValue());
                future.complete(room);
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
