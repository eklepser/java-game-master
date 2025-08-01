package core.logic;

import java.util.Random;

public class Client {
    private static final Random rnd = new Random();

    private static String clientId = "";
    private static String clientTeam = "";
    private static String clientName = "player" + (int)(rnd.nextFloat()*1000);

    public static Room CurrentRoom = new Room();
    public static GameState CurrentGameState = new GameState();

    public static void setClientId(String id) {
        clientId = id;
    }
    public static String getClientId() {
        return clientId;
    }

    public static void setClientName(String name) {
        clientName = name;
    }
    public static String getClientName() {
       return clientName;
    }

    public static void setClientTeam(String team) {
        clientTeam = team;
    }
    public static String getClientTeam() {
        return clientTeam;
    }
}

