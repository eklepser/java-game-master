package games.tictactoe;

import core.logic.Client;
import core.network.FirebaseManager;
import games.common.GameModel;
import javafx.scene.layout.Region;
import core.SceneManager;

import core.network.FirebaseListener;
import core.network.FirebaseReader;
import core.network.FirebaseWriter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class Controller extends GameModel {

    private boolean isReadyForGame = false;
    private boolean isFirstInit = true;
    private boolean isChatHistoryLoaded = false;
    private Button[] gameFieldButtons = new Button[9];

    @FXML private BorderPane root;
    @FXML private ScrollPane chatScrollPane;
    @FXML private Label roomInfoLabel;
    @FXML private TextField messageField;
    @FXML private VBox messageBox;
    @FXML private VBox gameFieldBox;
    @FXML private GridPane btnGrid;
    @FXML private Region rg;
    @FXML private Button readyButton;
    @FXML private Label currentTurnLabel;

    @Override
    protected void extraInitialize() {

        readyButton = new Button();
        readyButton.setText("Ready");
        readyButton.setPrefWidth(60);
        readyButton.setOnAction(this::onReadyButton);

        gameFieldBox = new VBox();
        gameFieldBox.setAlignment(Pos.CENTER);
        currentTurnLabel = new Label();
        btnGrid = new GridPane();
        btnGrid.setAlignment(Pos.CENTER);
        gameFieldBox.getChildren().add(currentTurnLabel);
        rg = new Region();
        rg.setPrefHeight(20);

        messageBox.heightProperty().addListener((obs, oldVal, newVal) -> {
            chatScrollPane.setVvalue(1.0);
        });
    }

    @Override
    public void onPlayerAdded(HashMap<String, String> playerInfo) {
        playersInfo.put(playerInfo.get("id"), playerInfo);
        roomInfoLabelUpdate();
    }

    @Override
    public void onPlayerRemoved(HashMap<String, String> playerInfo) {
        playersInfo.remove(playerInfo.get("id"));
        roomInfoLabelUpdate();
    }

    @Override
    public void onPlayerInfoChanged(HashMap<String, String> playerInfo) {
        playersInfo.put(playerInfo.get("id"), playerInfo);
    }

    @Override
    public void onClientTeamChanged(String newTeam) {
        currentTurnLabel.setText(Tools.getCurrentTurnText("0"));
    }

    @Override
    public void onMessageAdded(String newMessage) {
        Platform.runLater(() -> {
            if (!isChatHistoryLoaded) {
                isChatHistoryLoaded = true;
                sendMessage(Client.getClientName() + " entered the room", true);
            }
            messageBox.getChildren().add(Tools.getMessageText(newMessage));
        });
    }

    @Override
    public void onGamePreparing() {
        if (isFirstInit) {
            isReadyForGame = false;
            readyButton.setText("Ready");
            root.setCenter(readyButton);
            isFirstInit = false;
        }
    }

    @Override
    public void onGameStarted() {
        root.getChildren().remove(readyButton);
        currentGameState.setGameMap("---------");
        updateGameField();
        root.setCenter(gameFieldBox);
    }

    @Override
    public void onGameFinished() {
        if (isFirstInit) return;

        FirebaseWriter.setPlayerIsReady(roomId, Client.getClientId(), false);

        Button finishButton = new Button("New Game");
        finishButton.setOnAction(event -> {
            onFinishButton(finishButton);
        });

        gameFieldBox.getChildren().add(rg);
        gameFieldBox.getChildren().add(finishButton);
        currentTurnLabel.setText("Winner is " + Tools.getPlayerInfoByTurn(playersInfo, currentGameState.getTurn()).get("name"));

        for (Button btn : gameFieldButtons) {
            btn.setDisable(true);
        }
    }

    @Override
    public void onMapUpdated(String newMap) {
        System.out.println(newMap);
        currentGameState.setGameMap(newMap);
        updateGameField();

        if (Tools.isWinner(newMap)) {
            FirebaseWriter.setGameGlobalState(roomId, "finished");
        }

        else if (Tools.isDraw(newMap)) {
            FirebaseWriter.setGameGlobalState(roomId, "finished");
        }
    }

    @Override
    public void onNewTurn(String currentTurn) {
        currentTurnLabel.setText(Tools.getCurrentTurnText( currentTurn));
    }

    public void onExitButton(ActionEvent actionEvent) throws IOException {
        String clientId = Client.getClientId();
        FirebaseWriter.removeClientFromRoom(clientId, roomId);

        FirebaseListener.removeAllListeners();
        FirebaseManager.releaseClient();

        sendMessage(Client.getClientName() + " left the room", true);

        SceneManager.loadScene("common/room_selection.fxml");
    }

    public void onSendMessageButton(ActionEvent actionEvent) {
        sendMessage(messageField.getText(), false);
    }

    public void onKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) sendMessage(messageField.getText(), false);
    }

    public void onReadyButton(ActionEvent actionEvent) {
        isReadyForGame = !isReadyForGame;
        if (isReadyForGame) {
            FirebaseWriter.setPlayerIsReady(roomId, Client.getClientId(), true);
            FirebaseReader.getPlayersReadyAmount(roomId).thenAccept(playersReady -> {
                String maxSize = currentRoom.getRoomInfo().get("size");
                sendMessage(Tools.getReadyMessage(String.valueOf(playersReady), maxSize), true);
                boolean isAllPlayersReady = Objects.equals(String.valueOf(playersReady), maxSize);
                if (isAllPlayersReady) {
                    startGame();
                }
            });
            readyButton.setText("Cancel");
        }
        else {
            FirebaseWriter.setPlayerIsReady(roomId, Client.getClientId(), false);
            sendMessage(Client.getClientName() + " is not ready", true);
            readyButton.setText("Ready");
        }
    }

    private void onGameFieldButton(Button btn) {
        if ((btn.getText().isBlank()) && Client.getClientTeam().equals(currentGameState.getTurn())) {
            if (Client.getClientTeam().equals("0")) btn.setText("x");
            else if (Client.getClientTeam().equals("1")) btn.setText("o");
            String opponentTeam = Tools.getOpponentTeam(Client.getClientTeam());
            FirebaseWriter.setCurrentTurn(roomId, opponentTeam);
            FirebaseWriter.setGameMap(roomId, Tools.getGameMap(gameFieldButtons));
        }
    }

    private void onFinishButton(Button btn) {
        gameFieldBox.getChildren().remove(btn);
        gameFieldBox.getChildren().remove(rg);
        root.getChildren().remove(gameFieldBox);

        isReadyForGame = false;
        readyButton.setText("Ready");
        root.setCenter(readyButton);

        FirebaseWriter.setGameGlobalState(roomId, "preparing");
    }

    private void updateGameField() {
        gameFieldBox.getChildren().remove(btnGrid);
        btnGrid.getChildren().clear();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button btn = new Button();
                if (Objects.equals(currentGameState.getGameMap().charAt(3 * i + j), 'x')) {
                    btn.setText("x");
                }
                else if (Objects.equals(currentGameState.getGameMap().charAt(3 * i + j), 'o')) {
                    btn.setText("o");
                }
                btn.setPrefSize(60, 60);
                btn.setOnAction(event -> {
                    onGameFieldButton(btn);
                });
                gameFieldButtons[3 * i + j] = btn;
                btnGrid.add(btn, i, j);
            }
        }
        gameFieldBox.getChildren().add(btnGrid);
    }

    private void roomInfoLabelUpdate() {
        StringBuilder text = new StringBuilder();
        text.append("Room: ").append(currentRoom.getRoomInfo().get("name")).append("\n");
        text.append("Players: ");
        for (HashMap<String, String> playerInfo : playersInfo.values()) {
            text.append("\n").append(playerInfo.get("name"));
        }
        Platform.runLater(() -> roomInfoLabel.setText(text.toString()));
    }

    private void sendMessage(String message, boolean isServiceMessage) {
        String text;
        if (!isServiceMessage) {
            text = Client.getClientName() + ": " + message;
            messageField.clear();
        }
        else text = "!!!" + message;
        FirebaseWriter.addMessageToRoom(roomId, text);
    }

    private void startGame() {
        FirebaseWriter.setRandomTeams(roomId, playersInfo);
        FirebaseWriter.setGameGlobalState(roomId, "playing");
        FirebaseWriter.setCurrentTurn(roomId, "0");
        sendMessage("Game started", true);
    }
}