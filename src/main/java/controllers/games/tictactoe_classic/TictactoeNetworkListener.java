package controllers.games.tictactoe_classic;

import core.logic.Client;
import core.logic.Player;
import core.network.FirebaseListener;
import core.network.FirebaseWriter;
import controllers.common.GameNetworkListener;
import javafx.application.Platform;

public class TictactoeNetworkListener extends GameNetworkListener {
    private TictactoeModel model;
    private TictactoeController controller;

    private boolean isFirstInit = true;
    private boolean isChatHistoryLoaded = false;

    public TictactoeNetworkListener(TictactoeModel model, TictactoeController controller) {
        this.model = model;
        this.controller = controller;
        FirebaseListener.addPlayersListener(model.roomId, this);
        FirebaseListener.addChatListener(model.roomId, this);
        FirebaseListener.addGameStateListener(model.roomId, this);
        FirebaseListener.addClientListener(model.roomId, this);
    }

    @Override
    public void onPlayerAdded(Player player) {
        model.putPlayer(player);
        controller.updateRoomInfoLabel();
    }

    @Override
    public void onPlayerRemoved(Player player) {
        model.removePlayerInfo(player.getId());
        controller.updateRoomInfoLabel();
    }

    @Override
    public void onPlayerInfoChanged(Player player) {
        model.putPlayer(player);
    }

    @Override
    public void onClientTeamChanged(String newTeam) {
        controller.updateCurrentTurnLabel(TictactoeUtils.getCurrentTurnText("0"));
    }

    @Override
    public void onMessageAdded(String newMessage) {
        Platform.runLater(() -> {
            if (!isChatHistoryLoaded) {
                isChatHistoryLoaded = true;
                model.sendMessage(Client.getClientName() + " entered the room", true);
            }
            controller.updateMessageBox(TictactoeUtils.getMessageText(newMessage));
        });
    }

    @Override
    public void onGamePreparing() {
        if (isFirstInit) {
            controller.setReadyButton(false);
            isFirstInit = false;
        }
    }

    @Override
    public void onGameStarted() {
        controller.removeReadyButton();
        model.currentGameState.setGameMap("---------");
        controller.updateGameField();
        controller.setGameFieldBox();
    }

    @Override
    public void onGameFinished(boolean isDraw) {
        if (isFirstInit) return;
        FirebaseWriter.setPlayerIsReady(model.roomId, Client.getClientId(), false);
        controller.setFinishButton();
        model.disableGameFieldButtons();

        String finishMessage;
        if (isDraw) finishMessage = "Game finished with a draw";
        else finishMessage = "Winner is " + model.getCurrentPlayer().getName();
        controller.updateCurrentTurnLabel(finishMessage);
    }

    @Override
    public void onMapUpdated(String newMap) {
        System.out.println(newMap);
        model.currentGameState.setGameMap(newMap);
        controller.updateGameField();

        if (model.isPreviousTurnClient()) {
            if (TictactoeUtils.isWinner(newMap)) {
                FirebaseWriter.setGameGlobalState(model.roomId, "finished");
            }
            else if (TictactoeUtils.isDraw(newMap)) {
                FirebaseWriter.setGameGlobalState(model.roomId, "finished with draw");
            }
        }
    }

    @Override
    public void onNewTurn(String currentTurn) {
        String message = TictactoeUtils.getCurrentTurnText(currentTurn);
        controller.updateCurrentTurnLabel(message);
    }
}