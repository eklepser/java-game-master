package games.common.callbacks;

public interface GameStateUpdateCallback {
    void onGamePreparing();
    void onGameStarted();
    void onGameFinished();
    void onGamePaused();

    void onMapUpdated(String newMap);

    void onNewTurn(String currentTurn);
}
