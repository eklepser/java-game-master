package controllers.common.callbacks;

public interface GameStateUpdateCallback {
    void onGamePreparing();
    void onGameStarted();
    void onGameFinished(boolean isDraw);
    void onGamePaused();

    void onMapUpdated(String newMap);

    void onNewTurn(String currentTurn);
}
