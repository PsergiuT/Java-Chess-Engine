package chess;

import chess.bitboard.BitBoard;
import chess.controller.PlayerBotController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PlayerBotApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/player-bot-board.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Player Bot Page");
        stage.setScene(scene);

        BitBoard board = new BitBoard();
        PlayerBotController controller = fxmlLoader.getController();
        //controller.setBoard(board);

        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}
