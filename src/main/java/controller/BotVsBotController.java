package Controller;

import Board.BitBoard;
import Controller.Loader.BoardDisplay;
import Move.Move;
import MoveGenerator.MoveGenerator;
import Search.BotV2.BotV2;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import lombok.Setter;


public class BotVsBotController {


    public Button startBtn;
    @FXML
    private Label blackTimerLabel;
    @FXML
    private Label whiteTimerLabel;
    @FXML
    private Label currentTurnLabel;
    @FXML
    private Label moveCountLabel;

    @FXML
    private GridPane chessBoard;

    @Setter
    private BitBoard board;
    private final StackPane[][] squares = new StackPane[8][8];


    @FXML
    private void initialize(){
        board = new BitBoard(10);
        setupBoard();
        BoardDisplay.updateBoardDisplay(squares, board);
        BoardDisplay.updateBoardTimer(board, blackTimerLabel, whiteTimerLabel);
    }

    private void setupBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane square = new StackPane();
                square.setPrefSize(80, 80);

                // Checkerboard pattern
                squares[row][col] = square;
                initSquareStyle(row, col);

                chessBoard.add(square, col, row);
            }
        }
    }


    private void initSquareStyle(int row, int col){
        squares[row][col].getStyleClass().add("chess-square");
        if((row + col) % 2 == 0)
            squares[row][col].getStyleClass().add("light-square");
        else
            squares[row][col].getStyleClass().add("dark-square");
    }



    private void start() {
        new Thread(() -> {
            while (true) {
                // --- CHECK FOR DRAW --- //
                if (board.getHalfMoveClock() >= 100) {
                    Platform.runLater(() -> {
                        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Draw!", "Draw!");
                        BoardDisplay.updateBoardDisplay(squares, board);
                        BoardDisplay.updateBoardTimer(board, blackTimerLabel, whiteTimerLabel);
                    });
                    break;
                }

                botV1Move();
                botV2Move();
            }

        }).start();
    }


    private void botV1Move(){
        if(MoveGenerator.generateMoves(board, board.isWhiteTurn).getSize() == 0){
            checkMate();
            return;
        }

        long start = System.nanoTime();
        BotV2.bestMove(board, 2, 2);
        long end = System.nanoTime();
        double time = (end - start) / 1_000_000_000.0;

        int bestMove = BotV2.getBestMove();

        board.decrementTimeForWhite(time);
        makeMove(bestMove);

        try { Thread.sleep(30); } catch (InterruptedException e) {
            System.err.println("Thread err");
        }
    }


    private void botV2Move(){
        if(MoveGenerator.generateMoves(board, board.isWhiteTurn).getSize() == 0){
            checkMate();
            return;
        }
        long start = System.nanoTime();
        BotV2.bestMove(board, 5, 5);
        long end = System.nanoTime();
        double time = (end - start) / 1_000_000_000.0;

        board.decrementTimeForBlack(time);
        int bestMove = BotV2.getBestMove();
        makeMove(bestMove);

        try { Thread.sleep(30); } catch (InterruptedException e) { System.err.println("Thread err"); }
    }


    private void makeMove(int bestMove){
        board.makeMove(bestMove);
        Platform.runLater(() -> {
            int fR = 7 - (Move.getFrom(bestMove) / 8);
            int fC = 7 - (Move.getFrom(bestMove) % 8);
            int tR = 7 - (Move.getTo(bestMove) / 8);
            int tC = 7 - (Move.getTo(bestMove) % 8);

            movePiece(fR, fC, tR, tC);
            currentTurnLabel.setText(board.isWhiteTurn ? "White" : "Black");
            BoardDisplay.updateBoardDisplay(squares, board);
            BoardDisplay.updateBoardTimer(board, blackTimerLabel, whiteTimerLabel);
        });
    }



    private void movePiece(int fromRow, int fromCol, int toRow, int toCol) {

        if (!squares[fromRow][fromCol].getChildren().isEmpty()) {
            ImageView piece = (ImageView) squares[fromRow][fromCol].getChildren().get(0);
            squares[fromRow][fromCol].getChildren().clear();
            squares[toRow][toCol].getChildren().clear();
            squares[toRow][toCol].getChildren().add(piece);

            int moves = Integer.parseInt(moveCountLabel.getText());
            moveCountLabel.setText(String.valueOf(moves + 1));
        }
    }


    private void checkMate() {
        Platform.runLater(() -> {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Checkmate!", board.isWhiteTurn ? "Black" : "White" + " wins!");
            try{
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.exit(0);
        });
    }

    @FXML
    private void onStartClick(){
        start();
    }



}
