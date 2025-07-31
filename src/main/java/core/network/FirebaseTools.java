package core.network;

import com.google.firebase.database.*;

public class FirebaseTools {

    public static FirebaseDatabase firebase;
    public static DatabaseReference rootRef;
    public static DatabaseReference roomsCounterRef;
    public static DatabaseReference roomsRef;

    public static DatabaseReference getRoomRefByRoomId(String roomId) {
        return roomsRef.child(roomId).getRef();
    }

    public static DatabaseReference getRoomInfoRefByRoomId(String roomId) {
        return roomsRef.child(roomId).child("info").getRef();
    }

    public static DatabaseReference getPlayersRefByRoomId(String roomId) {
        return getRoomRefByRoomId(roomId).child("players").getRef();
    }

    public static DatabaseReference getChatRefByRoomId(String roomId) {
        return getRoomRefByRoomId(roomId).child("chat").getRef();
    }

    public static DatabaseReference getGameStateRefByRoomId(String roomId) {
        return getRoomRefByRoomId(roomId).child("gameState").getRef();
    }
}
