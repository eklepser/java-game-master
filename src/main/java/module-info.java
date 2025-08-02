module org.example.javava {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires com.google.auth;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.google.auth.oauth2;
    requires firebase.admin;
    requires org.checkerframework.checker.qual;
    requires java.desktop;
    requires google.cloud.firestore;

    exports core.network;
    opens core.network to javafx.fxml;
    exports controller.games.tictactoe_classic;
    opens controller.games.tictactoe_classic to javafx.fxml;
    opens core to javafx.fxml;
    exports core;
    exports controller.common.callbacks;
    opens controller.common.callbacks to javafx.fxml;
    exports core.logic;
    opens core.logic to javafx.fxml;
    opens controller.common to javafx.fxml;
    exports controller.common;
    exports controller.menus.main_menu;
    opens controller.menus.main_menu to javafx.fxml;
    exports controller.menus.room_creation;
    opens controller.menus.room_creation to javafx.fxml;
    exports controller.menus.room_selection;
    opens controller.menus.room_selection to javafx.fxml;
}