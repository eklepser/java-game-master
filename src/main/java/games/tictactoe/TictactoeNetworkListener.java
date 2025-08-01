package games.tictactoe;

import core.logic.Client;
import core.network.FirebaseListener;
import core.network.FirebaseWriter;
import games.common.GameNetworkListener;
import javafx.application.Platform;
import javafx.scene.control.Button;
import java.util.HashMap;

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
    public void onPlayerAdded(HashMap<String, String> playerInfo) {
        model.playersInfo.put(playerInfo.get("id"), playerInfo);
        controller.updateRoomInfoLabel();
    }

    @Override
    public void onPlayerRemoved(HashMap<String, String> playerInfo) {
        model.playersInfo.remove(playerInfo.get("id"));
        controller.updateRoomInfoLabel();
    }

    @Override
    public void onPlayerInfoChanged(HashMap<String, String> playerInfo) {
        model.playersInfo.put(playerInfo.get("id"), playerInfo);
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
    public void onGameFinished() {
        if (isFirstInit) return;

        FirebaseWriter.setPlayerIsReady(model.roomId, Client.getClientId(), false);

        controller.setFinishButton();
        String winnerName = TictactoeUtils.getPlayerInfoByTurn(model.playersInfo, model.currentGameState.getTurn()).get("name");
        controller.updateCurrentTurnLabel("Winner is " + winnerName);

        for (Button btn : model.gameFieldButtons) {
            btn.setDisable(true);
        }
    }

    @Override
    public void onMapUpdated(String newMap) {
        System.out.println(newMap);
        model.currentGameState.setGameMap(newMap);
        controller.updateGameField();

        if (TictactoeUtils.isWinner(newMap)) {
            FirebaseWriter.setGameGlobalState(model.roomId, "finished");
        }

        else if (TictactoeUtils.isDraw(newMap)) {
            FirebaseWriter.setGameGlobalState(model.roomId, "finished");
        }
    }

    @Override
    public void onNewTurn(String currentTurn) {
        String message = TictactoeUtils.getCurrentTurnText(currentTurn);
        controller.updateCurrentTurnLabel(message);
    }
}