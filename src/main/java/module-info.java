module ituvtu.server {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.java_websocket;
    requires jakarta.xml.bind;
    requires java.sql;
    requires javafx.graphics;

    opens ituvtu.server.view to javafx.fxml;
    opens ituvtu.server.controller to javafx.fxml, jakarta.xml.bind;
    opens ituvtu.server.model to javafx.fxml, jakarta.xml.bind;
    exports ituvtu.server.view;
    exports ituvtu.server.model;
    exports ituvtu.server.controller;
    exports ituvtu.server.chat;
    exports ituvtu.server.database;
    exports ituvtu.server.xml;
    opens ituvtu.server.xml.message to jakarta.xml.bind;
    opens ituvtu.server.xml to jakarta.xml.bind;
    opens ituvtu.server.xml.auth to jakarta.xml.bind;
    opens ituvtu.server.xml.chat to jakarta.xml.bind;
    exports ituvtu.server.xml.message;
    exports ituvtu.server.xml.chat;
    exports ituvtu.server.xml.auth;
    exports ituvtu.server.xml.auxiliary;

}