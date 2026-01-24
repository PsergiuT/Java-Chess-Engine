package chess;

import chess.controller.BotVsBotController;
import chess.controller.PlayerBotController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BotVsBotApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/bot-vs-bot.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Bot VS Bot");
        stage.setScene(scene);

        stage.show();
    }


    public static void main(String[] args) {
    launch();
}
}
