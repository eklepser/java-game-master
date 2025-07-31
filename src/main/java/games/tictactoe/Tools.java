package games.tictactoe;

import core.logic.Client;

import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.util.HashMap;

public class Tools {

    static String getGameMap(Button[] gameFieldButtons) {
        StringBuilder gameMap = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            String btnText = gameFieldButtons[i].getText();
            if ((btnText.equals("x")) || (btnText.equals("o"))) gameMap.append(btnText);
            else gameMap.append("-");
        }
        return gameMap.toString();
    }

    static String getReadyMessage(String playersReady, String maxSize) {
        StringBuilder text = new StringBuilder();
        text.append(Client.getClientName()).append(" is ready ");
        text.append("(").append(playersReady).append("/").append(maxSize).append(")");
        return text.toString();
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
        char mark;
        if (Client.getClientTeam().equals("0")) mark = 'x';
        else mark = 'o';

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

    static HashMap<String, String> getPlayerInfoByTurn(HashMap<String, HashMap<String, String>> playersInfo, String turn) {
        String prevTurn = getOpponentTeam(turn);
        for (HashMap<String, String> playerInfo : playersInfo.values()) {
            if (playerInfo.get("team").equals(prevTurn)) return playerInfo;
        }
        return null;
    }

    static String getOpponentTeam(String team) {
        int opponentTeam = Math.abs(Integer.parseInt(team) - 1);
        return String.valueOf(opponentTeam);
    }
}
