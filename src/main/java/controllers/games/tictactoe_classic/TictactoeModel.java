package controllers.games.tictactoe_classic;

import core.logic.Client;
import core.logic.Player;
import core.network.FirebaseWriter;
import controllers.common.GameModel;
import javafx.scene.control.Button;
import java.util.HashMap;

public class TictactoeModel extends GameModel {
    private final HashMap<String, Player> allPlayers = new HashMap<>();
    private boolean isReadyForGame;
    private final Button[] gameFieldButtons;

    public TictactoeModel() {
        isReadyForGame = false;
        gameFieldButtons = new Button[9];
    }

    public HashMap<String, Player> getAllPlayers() { return allPlayers; }

    public void putPlayer(Player player) { allPlayers.put(player.getId(), player); }
    public void removePlayerInfo(String id) { allPlayers.remove(id); }

    public Player getCurrentPlayer() {
        String currentTurn = currentGameState.getTurn();
        String prevTurn = TictactoeUtils.getOpponentTeam(currentTurn);
        for (Player player : allPlayers.values()) {
            if (player.getTeam().equals(prevTurn)) return player;
        }
        return null;
    }

    public void setReadyStatus(Boolean isReady) { isReadyForGame = isReady; }
    public boolean isReadyStatus() { return isReadyForGame; }

    public void switchReadyStatus() { isReadyForGame = !isReadyForGame; }

    public void setGameFieldButton(int pos, Button btn) { gameFieldButtons[pos] = btn; }

    public void disableGameFieldButtons() {
        for (Button btn : gameFieldButtons) { btn.setDisable(true); }
    }

    public String getCurrentTurn() {
        return currentGameState.getTurn();
    }

    public boolean isCurrentTurnClient() {
        return Client.getClientTeam().equals(getCurrentTurn());
    }

    public String getPreviousTurn() {
        return TictactoeUtils.getOpponentTeam(currentGameState.getTurn());
    }

    public boolean isPreviousTurnClient() {
        return Client.getClientTeam().equals(getPreviousTurn());
    }

    public void startGame() {
        FirebaseWriter.setRandomTeams(roomId, allPlayers);
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

    public String getGameMap() {
        StringBuilder gameMap = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            String btnText = gameFieldButtons[i].getText();
            if ((btnText.equals("x")) || (btnText.equals("o"))) gameMap.append(btnText);
            else gameMap.append("-");
        }
        return gameMap.toString();
    }
}
