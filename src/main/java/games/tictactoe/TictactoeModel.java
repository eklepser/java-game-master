package games.tictactoe;


import games.common.GameModel;
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
}
