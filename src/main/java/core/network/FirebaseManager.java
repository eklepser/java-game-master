package core.network;

import core.logic.Client;
import core.logic.GameState;
import core.logic.Room;
import core.Main;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;

public class FirebaseManager {
    public static void init() throws URISyntaxException {
        File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        String jarDir = jarFile.getParentFile().getPath();

        try {
            FileInputStream serviceAccount = new FileInputStream(jarDir + "/key.json");
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://jmpg-252c8-default-rtdb.europe-west1.firebasedatabase.app")
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            FirebaseTools.firebase = FirebaseDatabase.getInstance();
            FirebaseTools.rootRef = FirebaseTools.firebase.getReference();
            FirebaseTools.roomsCounterRef = FirebaseTools.firebase.getReference("room_counter");
            FirebaseTools.roomsRef = FirebaseTools.firebase.getReference("rooms");
        }
        catch (Exception e) {
            System.err.println("Firebase initializing error");
        }
    }

    public static void attachClient(String roomId) {
        Client.CurrentRoom.setRoomId(roomId);
        FirebaseReader.getRoomInfo(roomId).thenAccept(roomInfo -> {
            Client.CurrentRoom.setRoomInfo(roomInfo);
            System.out.println("Client attached to room " + roomId);
        });
        String clientId = FirebaseWriter.addClientToRoom(roomId);
        Client.setClientId(clientId);
    }

    public static void releaseClient() {
        Client.setClientId("");
        Client.setClientTeam("");
        Client.CurrentRoom = new Room();
        Client.CurrentGameState = new GameState();
    }
}
