package controllers.common;

import core.logic.Client;
import core.logic.GameState;
import core.logic.Room;

public class GameModel {
    public Room currentRoom;
    public String roomId;
    public GameState currentGameState;

    public GameModel() {
        currentRoom = Client.CurrentRoom;
        roomId = currentRoom.getId();
        System.out.println("new GameModel: " + roomId);
        currentGameState = Client.CurrentGameState;
    }
}
