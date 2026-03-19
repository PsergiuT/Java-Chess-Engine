package controller;

import bitboard.BitBoard;
import controller.loader.BoardDisplay;
import controller.loader.ImageLoader;
import move.Move;
import move.MoveList;
import search.BotV2.BotV2;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import lombok.Setter;

import java.util.Objects;

public class BotVsBotController {


    @FXML
    private Label blackTimerLabel;
    @FXML
    private Label whiteTimerLabel;
    @FXML
    private Label currentTurnLabel;
    @FXML
    private Label moveCountLabel;
    @FXML
    private Label lastMoveLabel;

    @FXML
    private GridPane chessBoard;

    @Setter
    private BitBoard board;
    private StackPane[][] squares = new StackPane[8][8];
    private StackPane selectedSquare = null;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private MoveList validMovesFromSelectedSquare = new MoveList();

    private int lastSelectedRow = -1;
    private int lastSelectedCol = -1;
    private int botLastSelectedRow = -1;
    private int botLastSelectedCol = -1;
    private int selectedMove = -1;

    @FXML
    private Button undoBtn;

    private void setupBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane square = new StackPane();
                square.setPrefSize(80, 80);

                // Checkerboard pattern
                String color = (row + col) % 2 == 0 ? "#f0d9b5" : "#b58863";
                square.setStyle("-fx-background-color: " + color + "; -fx-border-color: #000000; -fx-border-width: 1;");

                // Store reference
                squares[row][col] = square;
                chessBoard.add(square, col, row);
            }
        }
    }


    @FXML
    private void onStartClick(){
        start();
    }



    private void start() {
        // Run the game loop in a separate background thread
        new Thread(() -> {

            while (true) {
                if(Integer.parseInt(moveCountLabel.getText()) == 86){
                    return;
                }
                // --- CHECK FOR DRAW ---
                if (board.getHalfMoveClock() >= 100) {
                    Platform.runLater(() -> {
                        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Draw!", "Draw!");
                        BoardDisplay.updateBoardDisplay(squares, board);
                    });
                    break;
                }

                // ==========================================
                // BOT 1 (WHITE) TURN
                // ==========================================
                if (board.isCheckMate) {
                    Platform.runLater(() -> MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Checkmate!", "Checkmate!"));
                    break;
                }

                long strt = System.nanoTime();
                BotV2.bestMove(board, 2, 2);
                long end = System.nanoTime();
                double time = (end - strt) / 1_000_000.0;

                int bestMove = BotV2.getBestMove();

                board.decrementTimeForWhite(time);
                board.makeMove(bestMove);

                Platform.runLater(() -> {
                    int fR = 7 - (Move.getFrom(bestMove) / 8);
                    int fC = 7 - (Move.getFrom(bestMove) % 8);
                    int tR = 7 - (Move.getTo(bestMove) / 8);
                    int tC = 7 - (Move.getTo(bestMove) % 8);

                    movePiece(fR, fC, tR, tC);
                    currentTurnLabel.setText(board.isWhiteTurn ? "White" : "Black");
                    BoardDisplay.updateBoardDisplay(squares, board);
                });

                try { Thread.sleep(30); } catch (InterruptedException e) { break; }


                // ==========================================
                // BOT 2 (BLACK) TURN
                // ==========================================
                if (board.isCheckMate) {
                    Platform.runLater(() -> MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Checkmate!", "Checkmate!"));
                    break;
                }
                strt = System.nanoTime();
                BotV2.bestMove(board, 5, 5);
                end = System.nanoTime();
                time = (end - strt) / 1_000_000.0;

                board.decrementTimeForBlack(time);
                int bestMove2 = BotV2.getBestMove();

                board.makeMove(bestMove2);
                // UPDATE STATE & UI
                Platform.runLater(() -> {
                    int fR = 7 - (Move.getFrom(bestMove2) / 8);
                    int fC = 7 - (Move.getFrom(bestMove2) % 8);
                    int tR = 7 - (Move.getTo(bestMove2) / 8);
                    int tC = 7 - (Move.getTo(bestMove2) % 8);

                    movePiece(fR, fC, tR, tC);
                    currentTurnLabel.setText(board.isWhiteTurn ? "White" : "Black");
                    BoardDisplay.updateBoardDisplay(squares, board);
                });

                try { Thread.sleep(30); } catch (InterruptedException e) { break; }
            }

        }).start();
    }


    private void printBoard(){
        System.out.println("--------------------------------------------");

        long enemyBoard = board.isWhiteTurn ? board.getBlackPieces() : board.getWhitePieces();

        for(int i = 0; i < 8; i++){
            System.out.print("R" + (i + 1) + ": ");
            for(int j = 0; j < 8; j++){
                long piece = enemyBoard & 0x8000000000000000L;
                System.out.print( piece != 0 ? "X " : "_ ");
                enemyBoard = enemyBoard << 1;
            }
            System.out.println("");
        }

        System.out.println("--------------------------------------------");
    }


    private void movePiece(int fromRow, int fromCol, int toRow, int toCol) {

        if (!squares[fromRow][fromCol].getChildren().isEmpty()) {
            ImageView piece = (ImageView) squares[fromRow][fromCol].getChildren().get(0);
            squares[fromRow][fromCol].getChildren().clear();
            squares[toRow][toCol].getChildren().clear();
            squares[toRow][toCol].getChildren().add(piece);

            // Update move info
            String move = getSquareName(fromRow, fromCol) + " -> " + getSquareName(toRow, toCol);
            //lastMoveLabel.setText(move);

            int moves = Integer.parseInt(moveCountLabel.getText());
            moveCountLabel.setText(String.valueOf(moves + 1));
        }
    }

    private void showAvailablePositions(){
        int[] validMoves = validMovesFromSelectedSquare.getMoves();
        for(int i = 0; i < validMovesFromSelectedSquare.getSize(); i++){
            int indexTo = Move.getTo(validMoves[i]);
            int row = 7 - (indexTo / 8);
            int col = 7 - (indexTo % 8);
            setSquareStyleAfterMove(row, col, "#FFD700");
        }
    }


    private void resetAvailablePositions(){
        int[] validMoves = validMovesFromSelectedSquare.getMoves();
        for(int i = 0; i < validMovesFromSelectedSquare.getSize(); i++){
            int indexTo = Move.getTo(validMoves[i]);
            int row = 7 - (indexTo / 8);
            int col = 7 - (indexTo % 8);
            resetSquareStyle(row, col);
        }
    }

    private void setSquareStyleAfterMove(int row, int col, String color){
        squares[row][col].setStyle("-fx-background-color: " + color + "; -fx-border-color: #000000; -fx-border-width: 1;");
    }


    private void resetSquareStyle(int row, int col) {
        String color = (row + col) % 2 == 0 ? "#f0d9b5" : "#b58863";
        squares[row][col].setStyle("-fx-background-color: " + color + "; -fx-border-color: #000000; -fx-border-width: 1;");
    }

    private String getSquareName(int row, int col) {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }


    @FXML
    private void initialize(){
        board = new BitBoard(10);
        setupBoard();
        BoardDisplay.updateBoardDisplay(squares, board);
    }



}
