package games.common;

import core.logic.Client;
import core.logic.GameState;
import core.logic.Room;

import java.util.HashMap;

public abstract class GameController {
    public void initialize() {
        extraInitialize();
        initializeUI();
    }

    protected abstract void extraInitialize();
    protected abstract void initializeUI();
}
