package controller;

import bitboard.BitBoard;
import controller.loader.BoardDisplay;
import lombok.Setter;
import moveGenerator.MoveGenerator;
import search.FenTranslator;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.stream.IntStream;

public class TestBitBoardController {

    @Setter
    private BitBoard board;

    @FXML
    private GridPane boardGrid;
    @FXML
    private GridPane boardImagesGrid;

    @FXML
    private final StackPane[][] squares = new StackPane[8][8];

    @FXML
    private ComboBox<Integer> whitePawnAheadPositionCombo;
    @FXML
    private ComboBox<Integer> blackPawnAheadPositionCombo;
    @FXML
    private ComboBox<Integer> whitePawnCapturePositionCombo;
     @FXML
    private ComboBox<Integer> blackPawnCapturePositionCombo;

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
    @FXML
    private ComboBox<String> fenCombo;


    @FXML
    private void initialize(){
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

                StackPane square = new StackPane();
                square.setPrefSize(80, 80);

                // Checkerboard pattern
                String color2 = (row + col) % 2 == 0 ? "#f0d9b5" : "#b58863";
                square.setStyle("-fx-background-color: " + color2 + "; -fx-border-color: #000000; -fx-border-width: 1;");

                // Store reference
                squares[row][col] = square;
                boardImagesGrid.add(square, col, row);
            }
        }
    }


    private void setupComboBoxes() {
        // Populate all three ComboBoxes with values 1-64
        List<Integer> comboBoxValues = IntStream.rangeClosed(1, 64).boxed().toList();
        populate(comboBoxValues,
                whitePawnAheadPositionCombo, blackPawnAheadPositionCombo, whitePawnCapturePositionCombo, blackPawnCapturePositionCombo,
                kingPositionCombo, knightPositionCombo, bishopPositionCombo, rookPositionCombo, queenPositionCombo, rayCombo1, rayCombo2);

        fenCombo.getItems().setAll(FenTranslator.FENS);

        // Add listeners to update board when selection changes

        bindComboBoxes(whitePawnAheadPositionCombo, MoveGenerator.pawnMoves[0]);
        bindComboBoxes(blackPawnAheadPositionCombo, MoveGenerator.pawnMoves[2]);
        bindComboBoxes(whitePawnCapturePositionCombo, MoveGenerator.pawnMoves[1]);
        bindComboBoxes(blackPawnCapturePositionCombo, MoveGenerator.pawnMoves[3]);
        bindComboBoxes(kingPositionCombo, MoveGenerator.kingMoves);
        bindComboBoxes(knightPositionCombo, MoveGenerator.knightMoves);
        bindComboBoxes(bishopPositionCombo, MoveGenerator.bishopMoves);
        bindComboBoxes(rookPositionCombo, MoveGenerator.rookMoves);
        bindComboBoxes(queenPositionCombo, MoveGenerator.queenMoves);

        Runnable rayUpdater = () -> {
            Integer position1 = rayCombo1.getValue();
            Integer position2 = rayCombo2.getValue();
            if (position1 != null && position2 != null) {
                drawPieces(position1, MoveGenerator.rayMovement[position1 - 1][position2 - 1]);
                drawPieces(position2, MoveGenerator.rayMovement[position1 - 1][position2 - 1]);
                System.out.println(MoveGenerator.rayMovement[position1 - 1][position2 - 1]);
            }
        };

        rayCombo1.setOnAction(e -> rayUpdater.run());
        rayCombo2.setOnAction(e -> rayUpdater.run());


        fenCombo.setOnAction(e -> {
            String fen = fenCombo.getValue();
            FenTranslator.translate(fen, board);

            BoardDisplay.updateBoardDisplay(squares, board);
        });
    }

    @SafeVarargs
    private void populate(List<Integer> comboBoxValues,ComboBox<Integer>... comboBoxes ){
        for(var comboBox : comboBoxes){
            comboBox.getItems().addAll(comboBoxValues);
        }
    }


    private void bindComboBoxes(ComboBox<Integer> combo, long[] movesList){
        combo.setOnAction(e -> {
            Integer position = combo.getValue();
            if (position != null) {
                drawPieces(position, movesList[position - 1]);
            }
        });
    }



    private void drawPieces(long boardPieces){
        for(int i = 0; i < 64; i++){
            int piece = (int) (boardPieces & 1);

            //draw piece on grid
            // i goes 0-63, where 0 is bottom-left (a1), 63 is top-right (h8)
            int row = 7 - (i / 8);      // Reverse row: bottom to top becomes top to bottom for GridPane
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
                cell.setStyle(cell.getStyle() + "; -fx-background-color: white;");
            } else if(piece == 1) {
                cell.setStyle(cell.getStyle() + "; -fx-background-color: red;");
            } else {
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
