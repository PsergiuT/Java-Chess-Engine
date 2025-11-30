module chess {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.base;
    requires java.desktop;


    opens chess to javafx.fxml;
    exports chess;
//    exports chess.config;
//    opens chess.config to javafx.fxml;
}
