package controller.games.tictactoe_classic;

import core.logic.Client;
import core.network.FirebaseWriter;
import controller.common.GameModel;
import javafx.scene.control.Button;
import java.util.HashMap;

public class TictactoeModel extends GameModel {
    public HashMap<String, HashMap<String, String>> playersInfo = new HashMap<>();
    public boolean isReadyForGame = false;
    public Button[] gameFieldButtons;

    public TictactoeModel() {
        isReadyForGame = false;
        gameFieldButtons = new Button[9];
    }

    public void startGame() {
        FirebaseWriter.setRandomTeams(roomId, playersInfo);
        FirebaseWriter.setGameGlobalState(roomId, "playing");
        FirebaseWriter.setCurrentTurn(roomId, "0");
        sendMessage("Game started", true);
    }

    public void sendMessage(String message, boolean isServiceMessage) {
        String text;
        if (!isServiceMessage) {
            text = Client.getClientName() + ": " + message;
        }
        else text = "!!!" + message;
        FirebaseWriter.addMessageToRoom(roomId, text);
    }
}
