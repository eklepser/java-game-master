package games.tictactoe;

import core.SceneManager;
import core.logic.Client;
import core.logic.GameState;
import core.logic.Room;
import core.network.FirebaseListener;
import core.network.FirebaseManager;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class Controller {

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

    public String roomId;
    public Room currentRoom;
    public GameState currentGameState;
    public HashMap<String, HashMap<String, String>> playersInfo = new HashMap<>();

    public boolean isReadyForGame = false;
    public Button[] gameFieldButtons = new Button[9];

    public void initialize() {
        currentRoom = Client.CurrentRoom;
        currentGameState = Client.CurrentGameState;
        roomId = currentRoom.getRoomId();
        NetworkListener network = new NetworkListener(this);

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

    public void onGameFieldButton(Button btn) {
        if ((btn.getText().isBlank()) && Client.getClientTeam().equals(currentGameState.getTurn())) {
            if (Client.getClientTeam().equals("0")) btn.setText("x");
            else if (Client.getClientTeam().equals("1")) btn.setText("o");
            String opponentTeam = Tools.getOpponentTeam(Client.getClientTeam());
            FirebaseWriter.setCurrentTurn(roomId, opponentTeam);
            FirebaseWriter.setGameMap(roomId, Tools.getGameMap(gameFieldButtons));
        }
    }

    public void onFinishButton(Button btn) {
        gameFieldBox.getChildren().remove(btn);
        gameFieldBox.getChildren().remove(rg);
        root.getChildren().remove(gameFieldBox);

        isReadyForGame = false;
        readyButton.setText("Ready");
        root.setCenter(readyButton);

        FirebaseWriter.setGameGlobalState(roomId, "preparing");
    }

    public void updateGameField() {
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

    public void roomInfoLabelUpdate() {
        StringBuilder text = new StringBuilder();
        text.append("Room: ").append(currentRoom.getRoomInfo().get("name")).append("\n");
        text.append("Players: ");
        for (HashMap<String, String> playerInfo : playersInfo.values()) {
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
        FirebaseWriter.addMessageToRoom(roomId, text);
    }

    public void startGame() {
        FirebaseWriter.setRandomTeams(roomId, playersInfo);
        FirebaseWriter.setGameGlobalState(roomId, "playing");
        FirebaseWriter.setCurrentTurn(roomId, "0");
        sendMessage("Game started", true);
    }
}
