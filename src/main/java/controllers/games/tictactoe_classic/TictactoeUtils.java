package controllers.games.tictactoe_classic;

import core.logic.Client;
import javafx.scene.text.Text;

public class TictactoeUtils {

    static String getReadyMessage(String playersReady, int maxSize) {
        return Client.getClientName() + " is ready " +
                "(" + playersReady + "/" + maxSize + ")";
    }

    static Text getMessageText(String chatMessage) {
        Text messageText = new Text();
        if (chatMessage.startsWith("!!!")) {
            messageText.setText(chatMessage.substring(3));
            messageText.setStyle("-fx-font-style: italic;");
        } else messageText.setText(chatMessage);
        return messageText;
    }

    static String getCurrentTurnText(String currentTurn) {
        String currentTurnText = "Current turn: ";
        if (currentTurn.equals("0")) currentTurnText += "x ";
        else currentTurnText += "o ";
        if (Client.getClientTeam().equals(currentTurn)) currentTurnText += "(you)";
        else currentTurnText += "(opponent)";
        return currentTurnText;
    }

    static boolean isWinner(String gameMap) {
        char mark = (Client.getClientTeam().equals("0")) ? 'x' : 'o';

        for (int i = 0; i < 9; i += 3) {
            if (gameMap.charAt(i) == mark &&
                    gameMap.charAt(i + 1) == mark &&
                    gameMap.charAt(i + 2) == mark) {
                return true;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (gameMap.charAt(i) == mark &&
                    gameMap.charAt(i + 3) == mark &&
                    gameMap.charAt(i + 6) == mark) {
                return true;
            }
        }

        if (gameMap.charAt(0) == mark && gameMap.charAt(4) == mark && gameMap.charAt(8) == mark) {
            return true;
        }
        if (gameMap.charAt(2) == mark && gameMap.charAt(4) == mark && gameMap.charAt(6) == mark) {
            return true;
        }

        return false;
    }

    static boolean isDraw(String gameMap) {
        return !gameMap.contains("-");
    }

    static String getOpponentTeam(String team) {
        int opponentTeam = Math.abs(Integer.parseInt(team) - 1);
        return String.valueOf(opponentTeam);
    }
}
