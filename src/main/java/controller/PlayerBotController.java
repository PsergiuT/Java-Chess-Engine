package Controller;


import Board.BitBoard;
import Controller.Loader.BoardDisplay;
import Search.BotV2.BotV2;
import javafx.application.Platform;
import lombok.Setter;
import MoveGenerator.MoveGenerator;
import Move.Move;
import Move.MoveList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PlayerBotController {

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
    private StackPane selectedSquare = null;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private final MoveList validMovesFromSelectedSquare = new MoveList();

    private int lastSelectedRow = -1;
    private int lastSelectedCol = -1;
    private int botLastSelectedRow = -1;
    private int botLastSelectedCol = -1;
    //private int selectedMove = -1;

    private Long start = 0L;
    private Long end = 0L;
    private Double time = 0.0;

    private final ExecutorService clockExecutor = Executors.newSingleThreadExecutor();


    @FXML
    private void initialize(){
        board = new BitBoard(10);
        setupBoard();
        BoardDisplay.updateBoardDisplay(squares, board);
        BoardDisplay.updateBoardTimer(board, blackTimerLabel, whiteTimerLabel);

        start = System.nanoTime();              // start timer for player
    }


    private void setupBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane square = new StackPane();
                square.setPrefSize(80, 80);

                squares[row][col] = square;
                initSquareStyle(row, col);

                final int r = row;
                final int c = col;
                square.setOnMouseClicked(e -> clockExecutor.submit(() -> handleSquareClick(r, c)));

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


    private void handleSquareClick(int row, int col) {
        if (selectedSquare == null)
            selectSquare(row, col);
        else
            makePlayerAndBotMove(row, col);
    }





    private void selectSquare(int row, int col){
        int selectedBoardIndex = (7 - row) * 8 + (7 - col);
        selectedRow = row;
        selectedCol = col;

        if (!squares[selectedRow][selectedCol].getChildren().isEmpty()) {
            MoveList allValidMoves = MoveGenerator.generateMoves(board, board.isWhiteTurn);
            int[] Moves = allValidMoves.getMoves();

            if(Moves.length == 0){
                checkMate();
                return;
            }

            Platform.runLater(() -> {
                selectedSquare = squares[selectedRow][selectedCol];
                selectedSquare.getStyleClass().add("selected-square");
            });

            validMovesFromSelectedSquare.clear();
            for(int i = 0; i < allValidMoves.getSize(); i++){
                if(Move.getFrom(Moves[i]) == selectedBoardIndex){
                    validMovesFromSelectedSquare.addMove(Moves[i]);
                    // Highlight available moves
                    int indexTo = Move.getTo(Moves[i]);
                    int rowTo = 7 - (indexTo / 8);
                    int colTo = 7 - (indexTo % 8);
                    Platform.runLater(() -> setSquareStyleAfterMove(rowTo, colTo, "after-available-position"));
                }
            }
            if(lastSelectedRow != -1 && lastSelectedCol != -1) {
                Platform.runLater(() -> setSquareStyle(lastSelectedRow, lastSelectedCol));

            }
        }
    }


    private void makePlayerAndBotMove(int row, int col){
        int selectedBoardIndex = (7 - row) * 8 + (7 - col);

        if(MoveGenerator.generateMoves(board, board.isWhiteTurn).getSize() == 0){
            checkMate();
            return;
        }
        int[] validMoves = validMovesFromSelectedSquare.getMoves();

        resetAvailablePositions();
        for(int i = 0; i < validMovesFromSelectedSquare.getSize(); i++){
            if(selectedBoardIndex == Move.getTo(validMoves[i])){
                playerMoves(validMoves[i], row, col);
                botMoves();
            }
        }

        selectedSquare = null;
        lastSelectedRow = selectedRow;
        lastSelectedCol = selectedCol;
        selectedRow = -1;
        selectedCol = -1;
    }





    private void playerMoves(int move, int row, int col){
        board.makeMove(move);
        Platform.runLater(() -> movePiece(selectedRow, selectedCol, row, col));

        end = System.nanoTime();
        time = (end - start) / 1_000_000_000.0;

        board.decrementTimeForWhite(time);
        Platform.runLater(() -> {
            BoardDisplay.updateBoardDisplay(squares, board);
            BoardDisplay.updateBoardTimer(board, blackTimerLabel, whiteTimerLabel);

            // Deselect
            setSquareStyleAfterMove(selectedRow, selectedCol, "after-white-moved");
            if(lastSelectedRow != -1 && lastSelectedCol != -1) {
                setSquareStyle(lastSelectedRow, lastSelectedCol);
            }

            currentTurnLabel.setText(board.isWhiteTurn ? "White" : "Black");
        });

        //selectedMove = move;
        try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    private void botMoves(){

        start = System.nanoTime();
        BotV2.bestMove(board, 5, 5);
        end = System.nanoTime();
        time = (end - start) / 1_000_000_000.0;
        int bestMove = BotV2.getBestMove();

        board.decrementTimeForBlack(time);
        board.makeMove(bestMove);

        int fromRow = 7 - (Move.getFrom(bestMove) / 8);
        int fromCol = 7 - (Move.getFrom(bestMove) % 8);
        int toRow = 7 - (Move.getTo(bestMove) / 8);
        int toCol = 7 - (Move.getTo(bestMove) % 8);
        Platform.runLater(() -> {
            movePiece(fromRow, fromCol, toRow, toCol);
            BoardDisplay.updateBoardDisplay(squares, board);
            BoardDisplay.updateBoardTimer(board, blackTimerLabel, whiteTimerLabel);

            // Deselect
            setSquareStyleAfterMove(fromRow, fromCol, "after-black-moved");
            if (botLastSelectedCol != -1 && botLastSelectedRow != -1) {
                setSquareStyle(botLastSelectedRow, botLastSelectedCol);
            }

            botLastSelectedRow = fromRow;
            botLastSelectedCol = fromCol;

            currentTurnLabel.setText(board.isWhiteTurn ? "White" : "Black");
        });

        try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }

        start = System.nanoTime();                  // start timer for white
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





    private void resetAvailablePositions(){
        int[] validMoves = validMovesFromSelectedSquare.getMoves();
        for(int i = 0; i < validMovesFromSelectedSquare.getSize(); i++){
            int indexTo = Move.getTo(validMoves[i]);
            int row = 7 - (indexTo / 8);
            int col = 7 - (indexTo % 8);
            Platform.runLater(() -> setSquareStyle(row, col));

        }
    }


    private void setSquareStyleAfterMove(int row, int col, String styleClass){
        squares[row][col].getStyleClass().add(styleClass);
    }


    private void setSquareStyle(int row, int col) {
        squares[row][col].getStyleClass().removeAll("after-available-position", "after-white-moved", "after-black-moved", "selected-square");
    }


//    @FXML
//    private void onUndoClick(){
//        if(selectedMove != -1){
//            board.undoMove(selectedMove);
//            selectedMove = -1;
//            BoardDisplay.updateBoardDisplay(squares, board);
//            BoardDisplay.updateBoardTimer(board, blackTimerLabel, whiteTimerLabel);
//        }
//    }

}
