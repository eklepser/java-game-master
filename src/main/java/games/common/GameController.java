package games.common;

public abstract class GameController {
    public void initialize() {
        extraInitialize();
        initializeUI();
    }

    protected abstract void extraInitialize();
    protected abstract void initializeUI();
}
