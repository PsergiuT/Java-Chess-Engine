package controller;


import bitboard.BitBoard;
import controller.loader.BoardDisplay;
import controller.loader.ImageLoader;
import lombok.Setter;
import moveGenerator.MoveGenerator;
import move.Move;
import move.MoveList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import search.BotV1.Bot;

import java.util.Objects;

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
    private int selectedMove = -1;


    @FXML
    private void initialize(){
        board = new BitBoard(10);
        setupBoard();
        BoardDisplay.updateBoardDisplay(squares, board);
    }


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

                // Add click handler
                final int r = row;
                final int c = col;
                square.setOnMouseClicked(e -> handleSquareClick(r, c));

                chessBoard.add(square, col, row);
            }
        }
    }



    private void handleSquareClick(int row, int col) {
        //System.out.println("Selected row: " + row + " col: " + col);
        int indexInBoard = (7 - row) * 8 + (7 - col);


        if (selectedSquare == null) {
            // Select a piece
            if (!squares[row][col].getChildren().isEmpty()) {
                MoveList allValidMoves = MoveGenerator.generateMoves(board, board.isWhiteTurn);
                int[] Moves = allValidMoves.getMoves();

                selectedSquare = squares[row][col];
                selectedRow = row;
                selectedCol = col;

                // Highlight selected square
                selectedSquare.setStyle(selectedSquare.getStyle() + "; -fx-border-color: #FFD700; -fx-border-width: 4;");

                validMovesFromSelectedSquare.clear();
                for(int i = 0; i < allValidMoves.getSize(); i++){
                    if(Move.getFrom(Moves[i]) == indexInBoard){
                        validMovesFromSelectedSquare.addMove(Moves[i]);
                    }
                }
                if(lastSelectedRow != -1 && lastSelectedCol != -1) {
                    resetSquareStyle(lastSelectedRow, lastSelectedCol);
                }
                showAvailablePositions();
            }

        } else {

            int[] validMoves = validMovesFromSelectedSquare.getMoves();

            if(MoveGenerator.generateMoves(board, board.isWhiteTurn).getSize() == 0){
                //checkmate
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Checkmate!", "Checkmate!");
            }

            for(int i = 0; i < validMovesFromSelectedSquare.getSize(); i++){
                if(indexInBoard == Move.getTo(validMoves[i])){
                    //printBoard();
                    board.makeMove(validMoves[i]);
                    selectedMove = validMoves[i];
                    movePiece(selectedRow, selectedCol, row, col);

                    // Switch turns
                    currentTurnLabel.setText(board.isWhiteTurn ? "White" : "Black");

                    BoardDisplay.updateBoardDisplay(squares, board);

                    // Deselect
                    setSquareStyleAfterMove(selectedRow, selectedCol, "#ffffff");
                    if(lastSelectedRow != -1 && lastSelectedCol != -1) {
                        resetSquareStyle(lastSelectedRow, lastSelectedCol);
                    }

                    //--------------make bot move---------------
                    int bestMove = Bot.bestMove(board);

                    if(bestMove == -1){
                        //checkmate
                        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Checkmate!", "Checkmate!");
                    }

                    board.makeMove(bestMove);
                    int fromRow = 7 - (Move.getFrom(bestMove) / 8);
                    int fromCol = 7 - (Move.getFrom(bestMove) % 8);
                    int toRow = 7 - (Move.getTo(bestMove) / 8);
                    int toCol = 7 - (Move.getTo(bestMove) % 8);
                    movePiece(fromRow, fromCol, toRow, toCol);

                    // Switch turns
                    currentTurnLabel.setText(board.isWhiteTurn ? "White" : "Black");

                    BoardDisplay.updateBoardDisplay(squares, board);


                    // Deselect
                    setSquareStyleAfterMove(fromRow, fromCol, "#5f5f5f");
                    if(botLastSelectedCol != -1 && botLastSelectedRow != -1) {
                        resetSquareStyle(botLastSelectedRow, botLastSelectedCol);
                    }

                    botLastSelectedRow = fromRow;
                    botLastSelectedCol = fromCol;
                }
            }

            resetAvailablePositions();
            selectedSquare = null;
            lastSelectedRow = selectedRow;
            lastSelectedCol = selectedCol;
            selectedRow = -1;
            selectedCol = -1;
        }
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
    private void onUndoClick(){
        if(selectedMove != -1){
            board.undoMove(selectedMove);
            selectedMove = -1;
            BoardDisplay.updateBoardDisplay(squares, board);
        }
    }

}
