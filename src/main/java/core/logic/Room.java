package core.logic;

import java.util.ArrayList;

public class Room {
    private String id;
    private String name;
    private String gameMode;
    private String password;
    private String allowToWatch;
    private int size;
    private final ArrayList<String> players;

    public Room() {
        id = "";
        name = "";
        gameMode = "";
        password = "";
        allowToWatch = "";
        size = 999;
        players = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String roomId) { id = roomId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGameMode() { return gameMode; }
    public void setGameMode(String gameMode) {this.gameMode = gameMode; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAllowToWatch() { return allowToWatch; }
    public void setAllowToWatch(String allowToWatch) { this.allowToWatch = allowToWatch; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public boolean isFull() { return size <= players.size(); }

    public ArrayList<String> getPlayers() { return players; }
    public void addPlayer(String player) { players.add(player); }

    public void clearPlayers() { players.clear(); }
    public int getPlayersCount() { return players.size(); }
}
