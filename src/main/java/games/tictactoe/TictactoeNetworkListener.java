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

    public TictactoeNetworkListener(TictactoeController controller, TictactoeModel model) {
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
        controller.roomInfoLabelUpdate();
    }

    @Override
    public void onPlayerRemoved(HashMap<String, String> playerInfo) {
        model.playersInfo.remove(playerInfo.get("id"));
        controller.roomInfoLabelUpdate();
    }

    @Override
    public void onPlayerInfoChanged(HashMap<String, String> playerInfo) {
        model.playersInfo.put(playerInfo.get("id"), playerInfo);
    }

    @Override
    public void onClientTeamChanged(String newTeam) {
        controller.currentTurnLabel.setText(TictactoeUtils.getCurrentTurnText("0"));
    }

    @Override
    public void onMessageAdded(String newMessage) {
        Platform.runLater(() -> {
            if (!isChatHistoryLoaded) {
                isChatHistoryLoaded = true;
                controller.sendMessage(Client.getClientName() + " entered the room", true);
            }
            controller.messageBox.getChildren().add(TictactoeUtils.getMessageText(newMessage));
        });
    }

    @Override
    public void onGamePreparing() {
        if (isFirstInit) {
            model.isReadyForGame = false;
            controller.readyButton.setText("Ready");
            controller.root.setCenter(controller.readyButton);
            isFirstInit = false;
        }
    }

    @Override
    public void onGameStarted() {
        controller.root.getChildren().remove(controller.readyButton);
        model.currentGameState.setGameMap("---------");
        controller.updateGameField();
        controller.root.setCenter(controller.gameFieldBox);
    }

    @Override
    public void onGameFinished() {
        if (isFirstInit) return;

        FirebaseWriter.setPlayerIsReady(model.roomId, Client.getClientId(), false);

        Button finishButton = new Button("New Game");
        finishButton.setOnAction(event -> {
            controller.onFinishButton(finishButton);
        });

        controller.gameFieldBox.getChildren().add(controller.rg);
        controller.gameFieldBox.getChildren().add(finishButton);
        controller.currentTurnLabel.setText("Winner is " + TictactoeUtils.getPlayerInfoByTurn(model.playersInfo, model.currentGameState.getTurn()).get("name"));

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
        controller.currentTurnLabel.setText(TictactoeUtils.getCurrentTurnText( currentTurn));
    }
}