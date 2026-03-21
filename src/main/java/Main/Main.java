package Main;

import Applications.BotVsBotApplication;
import Applications.PlayerBotApplication;
import Applications.TestBoardApplication;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button btnPly = new Button("Launch Player VS Bot");
        Button btnBot = new Button("Launch Bot VS Bot");
        Button btnTst = new Button("Launch Test Interface");

        btnPly.setPrefWidth(200);
        btnBot.setPrefWidth(200);
        btnTst.setPrefWidth(200);

        btnPly.setOnAction(e -> openApp(new PlayerBotApplication(), primaryStage));
        btnBot.setOnAction(e -> openApp(new BotVsBotApplication(), primaryStage));
        btnTst.setOnAction(e -> openApp(new TestBoardApplication(), primaryStage));

        VBox layout = new VBox(20, btnPly, btnBot, btnTst);
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("portal-background");

        Scene scene = new Scene(layout, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chess Portal");
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private void openApp(Application app, Stage portalStage) {
        try {
            Stage newStage = new Stage();
            app.start(newStage);

            //portalStage.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}