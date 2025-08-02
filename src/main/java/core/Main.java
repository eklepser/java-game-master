package core;

import core.network.FirebaseManager;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {
        FirebaseManager.init();
        SceneManager.init(stage);
        SceneManager.loadScene("menus/main_menu.fxml");
    }

    public static void main(String[] args) {
        launch();
    }
}