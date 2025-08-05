package core.logic;

public class Player {
    private String id;
    private String name;
    private boolean isReady;
    private String team;

    public Player() {
        id = "none";
        name = "none";
        isReady = false;
        team = "none";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isReady() { return isReady; }
    public void setReady(boolean ready) { isReady = ready; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }
}
