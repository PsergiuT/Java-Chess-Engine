package chess.controller;

import chess.bitboard.BitBoard;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class TestBitBoardController {

    private BitBoard board;

    @FXML
    private Label boardTitle;
    @FXML
    private GridPane boardGrid;

    @FXML
    private Button whitePawnBtn;
    @FXML
    private Button whiteRookBtn;
    @FXML
    private Button whiteKnightBtn;
    @FXML
    private Button whiteBishopBtn;
    @FXML
    private Button whiteQueenBtn;
    @FXML
    private Button whiteKingBtn;
    @FXML
    private Button blackPawnBtn;
    @FXML
    private Button blackRookBtn;
    @FXML
    private Button blackKnightBtn;
    @FXML
    private Button blackBishopBtn;
    @FXML
    private Button blackQueenBtn;
    @FXML
    private Button blackKingBtn;


    public void setBoard(BitBoard board){
        this.board = board;
    }


    @FXML
    private void initialize(){
        setupBoard();
    }

    private void setupBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Label cell = new Label("0");
                cell.setPrefSize(50, 50);
                cell.setAlignment(Pos.CENTER);

                // Checkerboard pattern
                String color = (row + col) % 2 == 0 ? "#f0d9b5" : "#b58863";
                cell.setStyle("-fx-background-color: " + color +
                        "; -fx-border-color: black; -fx-font-size: 20px;");

                boardGrid.add(cell, col, row);
            }
        }
    }


    private void drawPieces(long boardPieces){
        for(int i = 0; i < 64; i++){
            int piece = (int) (boardPieces & 1);

            //draw piece on grid
            // Calculate row and column
            // i goes 0-63, where 0 is bottom-left (a1), 63 is top-right (h8)
            int row = 7 - (i / 8);  // Reverse row: bottom to top becomes top to bottom for GridPane
            int col = i % 8;        // Left to right

            // Get the label from the grid
            Label cell = (Label) boardGrid.getChildren().get(row * 8 + col);
            cell.setText(piece == 1 ? "1" : "0");

            boardPieces >>= 1;
        }
    }

    @FXML
    private void onWhitePawnClick(){
        long boardPieces = board.getWhitePawnBoard();
        drawPieces(boardPieces);
    }

    @FXML
    private void onWhiteRookClick(){
        long boardPieces = board.getWhiteRookBoard();
        drawPieces(boardPieces);
    }

    @FXML
    private void onWhiteKnightClick(){
        long boardPieces = board.getWhiteKnightBoard();
        drawPieces(boardPieces);
    }

    @FXML
    private void onWhiteBishopClick(){
        long boardPieces = board.getWhiteBishopBoard();
        drawPieces(boardPieces);
    }

    @FXML
    private void onWhiteQueenClick(){
        long boardPieces = board.getWhiteQueenBoard();
        drawPieces(boardPieces);
    }

    @FXML
    private void onWhiteKingClick(){
        long boardPieces = board.getWhiteKingBoard();
        drawPieces(boardPieces);
    }

    @FXML
    private void onBlackPawnClick(){
        long boardPieces = board.getBlackPawnBoard();
        drawPieces(boardPieces);
    }

    @FXML
    private void onBlackRookClick(){
        long boardPieces = board.getBlackRookBoard();
        drawPieces(boardPieces);
    }

    @FXML
    private void onBlackKnightClick(){
        long boardPieces = board.getBlackKnightBoard();
        drawPieces(boardPieces);
    }

    @FXML
    private void onBlackBishopClick(){
        long boardPieces = board.getBlackBishopBoard();
        drawPieces(boardPieces);
    }

    @FXML
    private void onBlackQueenClick(){
        long boardPieces = board.getBlackQueenBoard();
        drawPieces(boardPieces);
    }

    @FXML
    private void onBlackKingClick(){
        long boardPieces = board.getBlackKingBoard();
        drawPieces(boardPieces);
    }

}
