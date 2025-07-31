package games.tictactoe;

import core.SceneManager;
import core.logic.Client;
import core.logic.GameState;
import core.logic.Room;
import core.network.FirebaseListener;
import core.network.FirebaseManager;
import core.network.FirebaseReader;
import core.network.FirebaseWriter;
import games.common.GameController;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class TictactoeController extends GameController {
    TictactoeModel model;
    TictactoeNetworkListener network;

    @FXML public BorderPane root;
    @FXML public ScrollPane chatScrollPane;
    @FXML public Label roomInfoLabel;
    @FXML public TextField messageField;
    @FXML public VBox messageBox;
    @FXML public VBox gameFieldBox;
    @FXML public GridPane btnGrid;
    @FXML public Region rg;
    @FXML public Button readyButton;
    @FXML public Label currentTurnLabel;

    @Override
    protected void extraInitialize() {
        model = new TictactoeModel();
        network = new TictactoeNetworkListener(this, model);
    }

    @Override
    protected void initializeUI() {
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

    public void onExitButton(ActionEvent actionEvent) throws IOException {
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
        model.isReadyForGame = !model.isReadyForGame;
        if (model.isReadyForGame) {
            FirebaseWriter.setPlayerIsReady(model.roomId, Client.getClientId(), true);
            FirebaseReader.getPlayersReadyAmount(model.roomId).thenAccept(playersReady -> {
                String maxSize = model.currentRoom.getRoomInfo().get("size");
                sendMessage(TictactoeUtils.getReadyMessage(String.valueOf(playersReady), maxSize), true);
                boolean isAllPlayersReady = Objects.equals(String.valueOf(playersReady), maxSize);
                if (isAllPlayersReady) {
                    startGame();
                }
            });
            readyButton.setText("Cancel");
        }
        else {
            FirebaseWriter.setPlayerIsReady(model.roomId, Client.getClientId(), false);
            sendMessage(Client.getClientName() + " is not ready", true);
            readyButton.setText("Ready");
        }
    }

    public void onGameFieldButton(Button btn) {
        if ((btn.getText().isBlank()) && Client.getClientTeam().equals(model.currentGameState.getTurn())) {
            if (Client.getClientTeam().equals("0")) btn.setText("x");
            else if (Client.getClientTeam().equals("1")) btn.setText("o");
            String opponentTeam = TictactoeUtils.getOpponentTeam(Client.getClientTeam());
            FirebaseWriter.setCurrentTurn(model.roomId, opponentTeam);
            FirebaseWriter.setGameMap(model.roomId, TictactoeUtils.getGameMap(model.gameFieldButtons));
        }
    }

    public void onFinishButton(Button btn) {
        gameFieldBox.getChildren().remove(btn);
        gameFieldBox.getChildren().remove(rg);
        root.getChildren().remove(gameFieldBox);

        model.isReadyForGame = false;
        readyButton.setText("Ready");
        root.setCenter(readyButton);

        FirebaseWriter.setGameGlobalState(model.roomId, "preparing");
    }

    public void updateGameField() {
        gameFieldBox.getChildren().remove(btnGrid);
        btnGrid.getChildren().clear();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button btn = new Button();
                if (Objects.equals(model.currentGameState.getGameMap().charAt(3 * i + j), 'x')) {
                    btn.setText("x");
                }
                else if (Objects.equals(model.currentGameState.getGameMap().charAt(3 * i + j), 'o')) {
                    btn.setText("o");
                }
                btn.setPrefSize(60, 60);
                btn.setOnAction(event -> {
                    onGameFieldButton(btn);
                });
                model.gameFieldButtons[3 * i + j] = btn;
                btnGrid.add(btn, i, j);
            }
        }
        gameFieldBox.getChildren().add(btnGrid);
    }

    public void roomInfoLabelUpdate() {
        StringBuilder text = new StringBuilder();
        text.append("Room: ").append(model.currentRoom.getRoomInfo().get("name")).append("\n");
        text.append("Players: ");
        for (HashMap<String, String> playerInfo : model.playersInfo.values()) {
            text.append("\n").append(playerInfo.get("name"));
        }
        Platform.runLater(() -> roomInfoLabel.setText(text.toString()));
    }

    public void sendMessage(String message, boolean isServiceMessage) {
        String text;
        if (!isServiceMessage) {
            text = Client.getClientName() + ": " + message;
            messageField.clear();
        }
        else text = "!!!" + message;
        FirebaseWriter.addMessageToRoom(model.roomId, text);
    }

    public void startGame() {
        FirebaseWriter.setRandomTeams(model.roomId, model.playersInfo);
        FirebaseWriter.setGameGlobalState(model.roomId, "playing");
        FirebaseWriter.setCurrentTurn(model.roomId, "0");
        sendMessage("Game started", true);
    }

}
