package chess.controller;

import chess.bitboard.BitBoard;
import chess.bitboard.MoveGenerator;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class TestBitBoardController {

    private BitBoard board;
    private MoveGenerator moveGenerator;

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
    @FXML
    private ComboBox<Integer> whitePawnPositionCombo;
    @FXML
    private ComboBox<Integer> blackPawnPositionCombo;
    @FXML
    private ComboBox<Integer> kingPositionCombo;
    @FXML
    private ComboBox<Integer> knightPositionCombo;
    @FXML
    private ComboBox<Integer> bishopPositionCombo;
    @FXML
    private ComboBox<Integer> rookPositionCombo;
    @FXML
    private ComboBox<Integer> queenPositionCombo;
    @FXML
    private ComboBox<Integer> rayCombo1;
    @FXML
    private ComboBox<Integer> rayCombo2;



    public void setBoard(BitBoard board){
        this.board = board;
    }


    @FXML
    private void initialize(){
        this.moveGenerator = new MoveGenerator();
        setupBoard();
        setupComboBoxes();
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


    private void setupComboBoxes() {
        // Populate all three ComboBoxes with values 1-64
        for (int i = 1; i <= 64; i++) {
            whitePawnPositionCombo.getItems().add(i);
            blackPawnPositionCombo.getItems().add(i);
            kingPositionCombo.getItems().add(i);
            knightPositionCombo.getItems().add(i);
            bishopPositionCombo.getItems().add(i);
            rookPositionCombo.getItems().add(i);
            queenPositionCombo.getItems().add(i);
            rayCombo1.getItems().add(i);
            rayCombo2.getItems().add(i);
        }

        // Add listeners to update board when selection changes
        whitePawnPositionCombo.setOnAction(e -> {
            Integer position = whitePawnPositionCombo.getValue();
            if (position != null) {
                drawPieces(position, moveGenerator.whitePawnMoves[position - 1]);
            }
        });

        blackPawnPositionCombo.setOnAction(e -> {
            Integer position = blackPawnPositionCombo.getValue();
            if (position != null) {
                drawPieces(position, moveGenerator.blackPawnMoves[position - 1]);
            }
        });

        kingPositionCombo.setOnAction(e -> {
            Integer position = kingPositionCombo.getValue();
            if (position != null) {
                drawPieces(position, moveGenerator.kingMoves[position - 1]);
            }
        });

        bishopPositionCombo.setOnAction(e -> {
            Integer position = bishopPositionCombo.getValue();
            if (position != null) {
                drawPieces(position, moveGenerator.bishopMoves[position - 1]);
            }
        });

        rookPositionCombo.setOnAction(e -> {
            Integer position = rookPositionCombo.getValue();
            if (position != null) {
                drawPieces(position, moveGenerator.rookMoves[position - 1]);
            }
        });


        queenPositionCombo.setOnAction(e -> {
            Integer position = queenPositionCombo.getValue();
            if (position != null) {
                drawPieces(position, moveGenerator.queenMoves[position - 1]);
            }
        });


        knightPositionCombo.setOnAction(e -> {
            Integer position = knightPositionCombo.getValue();
            if (position != null) {
                drawPieces(position, moveGenerator.knightMoves[position - 1]);
            }
        });

        rayCombo1.setOnAction(e -> {
            Integer position1 = rayCombo1.getValue();
            Integer position2 = rayCombo2.getValue();

            if (position1 != null && position2 != null) {
                drawPieces(position1, moveGenerator.rayMovement[position1 - 1][position2 - 1]);
                System.out.println(moveGenerator.rayMovement[position1 - 1][position2 - 1]);
            }
        });

        rayCombo2.setOnAction(e -> {
            Integer position1 = rayCombo1.getValue();
            Integer position2 = rayCombo2.getValue();

            if (position1 != null && position2 != null) {
                drawPieces(position2, moveGenerator.rayMovement[position1 - 1][position2 - 1]);
                System.out.println(moveGenerator.rayMovement[position1 - 1][position2 - 1]);
            }
        });
    }



    private void drawPieces(long boardPieces){
        for(int i = 0; i < 64; i++){
            int piece = (int) (boardPieces & 1);

            //draw piece on grid
            // Calculate row and column
            // i goes 0-63, where 0 is bottom-left (a1), 63 is top-right (h8)
            int row = 7 - (i / 8);  // Reverse row: bottom to top becomes top to bottom for GridPane
            int col = 7 - i % 8;        // Left to right

            // Get the label from the grid
            Label cell = (Label) boardGrid.getChildren().get(row * 8 + col);
            cell.setText(piece == 1 ? "1" : "0");

            boardPieces >>= 1;
        }
    }

    private void drawPieces(Integer position, long boardPieces){
        for(int i = 0; i < 64; i++){
            int piece = (int) (boardPieces & 1);

            int row = 7 - (i / 8);
            int col = 7 - (i % 8);

            // Get the label from the grid
            Label cell = (Label) boardGrid.getChildren().get(row * 8 + col);

            if(i + 1 == position){
                // This is the selected position - make it white
                //cell.setStyle(cell.getStyle() + "; -fx-background-color: white;");
            } else if(piece == 1) {
                // This has a piece - make it red
                cell.setStyle(cell.getStyle() + "; -fx-background-color: red;");
            } else {
                // Reset to checkerboard pattern
                String color = (row + col) % 2 == 0 ? "#f0d9b5" : "#b58863";
                cell.setStyle("-fx-background-color: " + color + "; -fx-border-color: black; -fx-font-size: 20px;");
            }

            cell.setText("" + (i + 1));

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
