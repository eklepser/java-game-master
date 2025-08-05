package controllers.games.tictactoe_classic;

import core.logic.Player;
import core.scenes.SceneManager;
import core.logic.Client;
import core.network.FirebaseListener;
import core.network.FirebaseManager;
import core.network.FirebaseReader;
import core.network.FirebaseWriter;
import controllers.common.GameController;
import core.scenes.ScenePath;
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
import javafx.scene.text.Text;
import java.io.IOException;
import java.util.Objects;

public class TictactoeController extends GameController {
    private TictactoeModel model;
    TictactoeNetworkListener network;

    @FXML private BorderPane root;
    @FXML private ScrollPane chatScrollPane;
    @FXML private Label roomInfoLabel;
    @FXML private TextField messageField;
    @FXML private VBox messageBox;
    @FXML private VBox gameFieldBox;
    @FXML private GridPane btnGrid;
    @FXML private Region rg;
    @FXML private Button readyButton;
    @FXML private Button finishButton;
    @FXML private Label currentTurnLabel;

    @Override
    protected void extraInitialize() {
        model = new TictactoeModel();
        network = new TictactoeNetworkListener(model, this);
    }

    @Override
    protected void initializeUI() {
        readyButton = new Button();
        readyButton.setText("Ready");
        readyButton.setPrefWidth(60);
        readyButton.setOnAction(this::onReadyButton);
        finishButton = new Button("New Game");

        finishButton.setOnAction((_) -> onFinishButton(finishButton));

        gameFieldBox = new VBox();
        gameFieldBox.setAlignment(Pos.CENTER);
        currentTurnLabel = new Label();
        btnGrid = new GridPane();
        btnGrid.setAlignment(Pos.CENTER);
        gameFieldBox.getChildren().add(currentTurnLabel);
        rg = new Region();
        rg.setPrefHeight(20);
        messageBox.heightProperty().addListener((_, _, _) -> chatScrollPane.setVvalue(1.0));
        }

    public void onSendMessageButton() {
        model.sendMessage(messageField.getText(), false);
        messageField.clear();
    }

    public void onKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            model.sendMessage(messageField.getText(), false);
            messageField.clear();
        }
    }

    public void onReadyButton(ActionEvent actionEvent) {
        model.switchReadyStatus();
        if (model.isReadyStatus()) {
            FirebaseWriter.setPlayerIsReady(model.roomId, Client.getClientId(), true);
            FirebaseReader.getPlayersReadyAmount(model.roomId).thenAccept(playersReady -> {
                String maxSize = model.currentRoom.getSize();
                String message = TictactoeUtils.getReadyMessage(String.valueOf(playersReady), maxSize);
                model.sendMessage(message, true);
                boolean isAllPlayersReady = Objects.equals(String.valueOf(playersReady), maxSize);
                if (isAllPlayersReady) {
                    model.startGame();
                }
            });
            readyButton.setText("Cancel");
        }
        else {
            FirebaseWriter.setPlayerIsReady(model.roomId, Client.getClientId(), false);
            model.sendMessage(Client.getClientName() + " is not ready", true);
            readyButton.setText("Ready");
        }
    }

    public void setReadyButton(Boolean isReady) {
        model.setReadyStatus(isReady);
        if (isReady) readyButton.setText("Cancel");
        else readyButton.setText("Ready");
        root.setCenter(readyButton);
    }

    public void removeReadyButton() {
        root.getChildren().remove(readyButton);
    }

    public void onGameFieldButton(Button btn) {
        if ((btn.getText().isBlank()) && model.isCurrentTurnClient()) {
            if (Client.getClientTeam().equals("0")) btn.setText("x");
            else if (Client.getClientTeam().equals("1")) btn.setText("o");
            String opponentTeam = TictactoeUtils.getOpponentTeam(Client.getClientTeam());
            FirebaseWriter.setCurrentTurn(model.roomId, opponentTeam);
            FirebaseWriter.setGameMap(model.roomId, model.getGameMap());
        }
    }

    public void onFinishButton(Button btn) {
        gameFieldBox.getChildren().remove(btn);
        gameFieldBox.getChildren().remove(rg);
        root.getChildren().remove(gameFieldBox);

        model.setReadyStatus(false);
        readyButton.setText("Ready");
        root.setCenter(readyButton);

        FirebaseWriter.setGameGlobalState(model.roomId, "preparing");
    }

    public void setFinishButton() {
        gameFieldBox.getChildren().add(rg);
        gameFieldBox.getChildren().add(finishButton);
    }

    public void onExitButton() throws IOException {
        FirebaseListener.removeAllListeners();
        FirebaseWriter.removeClientFromRoom(Client.getClientId(), model.roomId);
        FirebaseManager.releaseClient();

        model.sendMessage(Client.getClientName() + " left the room", true);

        SceneManager.loadScene(ScenePath.ROOM_SELECTION);
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
                btn.setOnAction((_) -> onGameFieldButton(btn));
                model.setGameFieldButton(3 * i + j, btn);
                btnGrid.add(btn, i, j);
            }
        }
        gameFieldBox.getChildren().add(btnGrid);
    }

    public void setGameFieldBox() {
        root.setCenter(gameFieldBox);
    }

    public void updateRoomInfoLabel() {
        StringBuilder text = new StringBuilder();
        text.append("Room: ").append(model.currentRoom.getName()).append("\n");
        text.append("Players: ");
        for (Player player : model.getAllPlayers().values()) {
            text.append("\n").append(player.getName());
        }
        Platform.runLater(() -> roomInfoLabel.setText(text.toString()));
    }

    public void updateCurrentTurnLabel(String newMessage) {
        currentTurnLabel.setText(newMessage);
    }

    public void updateMessageBox(Text newMessageText) {
        messageBox.getChildren().add(newMessageText);
    }
}
