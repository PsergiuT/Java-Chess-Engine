module Chess.Bot{
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires javafx.graphics;

    exports Main;
    exports Applications;

    opens Controller to javafx.fxml;

}