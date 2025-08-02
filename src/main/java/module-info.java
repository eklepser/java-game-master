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
    exports controllers.games.tictactoe_classic;
    opens controllers.games.tictactoe_classic to javafx.fxml;
    opens core to javafx.fxml;
    exports core;
    exports controllers.common.callbacks;
    opens controllers.common.callbacks to javafx.fxml;
    exports core.logic;
    opens core.logic to javafx.fxml;
    opens controllers.common to javafx.fxml;
    exports controllers.common;
    exports controllers.menus.main_menu;
    opens controllers.menus.main_menu to javafx.fxml;
    exports controllers.menus.room_creation;
    opens controllers.menus.room_creation to javafx.fxml;
    exports controllers.menus.room_selection;
    opens controllers.menus.room_selection to javafx.fxml;
}