package core.logic;

public class GameState {
    private String globalState;
    private String currentTurn;
    private String gameMap = "---------";

    public GameState() {
        globalState = "";
        currentTurn = "";
        gameMap = "";
    }

    public void setGlobalState(String newGlobalState) { globalState = newGlobalState;}
    public String getGlobalState() { return globalState; }

    public void setTurn(String turn) { currentTurn = turn;}
    public String getTurn() { return currentTurn; }

    public void setGameMap(String map) { gameMap = map; }
    public String getGameMap() { return gameMap; }
}
