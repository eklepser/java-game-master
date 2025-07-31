package games.tictactoe;

import core.logic.Client;
import core.logic.GameState;
import core.network.FirebaseListener;
import core.network.FirebaseWriter;
import games.common.GameModel;
import javafx.application.Platform;
import javafx.scene.control.Button;

import java.util.HashMap;

public class NetworkListener extends GameModel {

    Controller controller;
    private boolean isFirstInit = true;
    private boolean isChatHistoryLoaded = false;
    private String roomId;
    public HashMap<String, HashMap<String, String>> playersInfo = new HashMap<>();
    public boolean isReadyForGame = false;

    public GameState currentGameState;
    public Button[] gameFieldButtons;

    public NetworkListener(Controller controller) {
        this.controller = controller;
        roomId = controller.roomId;
        System.out.println("roomId: " + roomId);
        playersInfo = controller.playersInfo;
        isReadyForGame = controller.isReadyForGame;
        currentGameState = controller.currentGameState;
        gameFieldButtons = controller.gameFieldButtons;

        FirebaseListener.addPlayersListener(roomId, this);
        FirebaseListener.addChatListener(roomId, this);
        FirebaseListener.addGameStateListener(roomId, this);
        FirebaseListener.addClientListener(roomId, this);
    }

    @Override
    public void onPlayerAdded(HashMap<String, String> playerInfo) {
        playersInfo.put(playerInfo.get("id"), playerInfo);
        controller.roomInfoLabelUpdate();
    }

    @Override
    public void onPlayerRemoved(HashMap<String, String> playerInfo) {
        playersInfo.remove(playerInfo.get("id"));
        controller.roomInfoLabelUpdate();
    }

    @Override
    public void onPlayerInfoChanged(HashMap<String, String> playerInfo) {
        playersInfo.put(playerInfo.get("id"), playerInfo);
    }

    @Override
    public void onClientTeamChanged(String newTeam) {
        controller.currentTurnLabel.setText(Tools.getCurrentTurnText("0"));
    }

    @Override
    public void onMessageAdded(String newMessage) {
        Platform.runLater(() -> {
            if (!isChatHistoryLoaded) {
                isChatHistoryLoaded = true;
                controller.sendMessage(Client.getClientName() + " entered the room", true);
            }
            controller.messageBox.getChildren().add(Tools.getMessageText(newMessage));
        });
    }

    @Override
    public void onGamePreparing() {
        if (isFirstInit) {
            isReadyForGame = false;
            controller.readyButton.setText("Ready");
            controller.root.setCenter(controller.readyButton);
            isFirstInit = false;
        }
    }

    @Override
    public void onGameStarted() {
        controller.root.getChildren().remove(controller.readyButton);
        currentGameState.setGameMap("---------");
        controller.updateGameField();
        controller.root.setCenter(controller.gameFieldBox);
    }

    @Override
    public void onGameFinished() {
        if (isFirstInit) return;

        FirebaseWriter.setPlayerIsReady(roomId, Client.getClientId(), false);

        Button finishButton = new Button("New Game");
        finishButton.setOnAction(event -> {
            controller.onFinishButton(finishButton);
        });

        controller.gameFieldBox.getChildren().add(controller.rg);
        controller.gameFieldBox.getChildren().add(finishButton);
        controller.currentTurnLabel.setText("Winner is " + Tools.getPlayerInfoByTurn(playersInfo, currentGameState.getTurn()).get("name"));

        for (Button btn : gameFieldButtons) {
            btn.setDisable(true);
        }
    }

    @Override
    public void onMapUpdated(String newMap) {
        System.out.println(newMap);
        currentGameState.setGameMap(newMap);
        controller.updateGameField();

        if (Tools.isWinner(newMap)) {
            FirebaseWriter.setGameGlobalState(roomId, "finished");
        }

        else if (Tools.isDraw(newMap)) {
            FirebaseWriter.setGameGlobalState(roomId, "finished");
        }
    }

    @Override
    public void onNewTurn(String currentTurn) {
        controller.currentTurnLabel.setText(Tools.getCurrentTurnText( currentTurn));
    }
}