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
    exports games.tictactoe;
    opens games.tictactoe to javafx.fxml;
    opens core to javafx.fxml;
    exports core;
    exports games.common.callbacks;
    opens games.common.callbacks to javafx.fxml;
    exports games.common.controllers;
    opens games.common.controllers to javafx.fxml;
    exports core.logic;
    opens core.logic to javafx.fxml;
    opens games.common to javafx.fxml;
    exports games.common;
}