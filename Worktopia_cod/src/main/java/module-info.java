module org.example.worktopia_cod {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;


    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires org.apache.poi.ooxml;
    requires com.fasterxml.jackson.databind;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j;
    requires mysql.connector.java;
    requires com.google.protobuf;
    requires jdk.httpserver;
    requires java.mail;
    requires java.sql;
    requires java.desktop;

    requires org.junit.jupiter.api;
    requires org.testng;

    opens Modelos to javafx.fxml, org.junit.jupiter.api;
    opens Aplicaciones to javafx.graphics, org.junit.jupiter.api;
    opens Clases to javafx.base, org.junit.jupiter.api;

    exports Aplicaciones;
    exports Modelos;
    exports Clases;
    exports Prueba_errores to org.junit.platform.commons;
}