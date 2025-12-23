package chess.controller;

import chess.bitboard.BitBoard;
import chess.bitboard.MoveGenerator;
import chess.search.FenTranslator;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class TestBitBoardController {

    private BitBoard board;

    @FXML
    private Label boardTitle;
    @FXML
    private GridPane boardGrid;

    @FXML
    private GridPane boardImagesGrid;

    @FXML
    private StackPane[][] squares = new StackPane[8][8];

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

    private Image whitePawnImg;
    private Image whiteRookImg;
    private Image whiteKnightImg;
    private Image whiteBishopImg;
    private Image whiteQueenImg;
    private Image whiteKingImg;
    private Image blackPawnImg;
    private Image blackRookImg;
    private Image blackKnightImg;
    private Image blackBishopImg;
    private Image blackQueenImg;
    private Image blackKingImg;






    public void setBoard(BitBoard board){
        this.board = board;
    }


    @FXML
    private void initialize(){
        setupBoard();
        setupComboBoxes();
        loadPieceImages();
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


    private void loadPieceImages() {
        // Load images from resources folder
        // Adjust paths according to your project structure
        try {
            whitePawnImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/white_pawn.png")));
            whiteRookImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/white_rook.png")));
            whiteKnightImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/white_knight.png")));
            whiteBishopImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/white_bishop.png")));
            whiteQueenImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/white_queen.png")));
            whiteKingImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/white_king.png")));

            blackPawnImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/black_pawn.png")));
            blackRookImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/black_rook.png")));
            blackKnightImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/black_knight.png")));
            blackBishopImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/black_bishop.png")));
            blackQueenImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/black_queen.png")));
            blackKingImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/black_king.png")));
        } catch (Exception e) {
            System.out.println("Error loading images: " + e.getMessage());
        }
    }


    private void updateBoardDisplay() {
        // Clear all squares
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col].getChildren().clear();
            }
        }

        // Draw pieces based on bitboards
        drawPieces(board.getWhitePawnBoard(), whitePawnImg);
        drawPieces(board.getWhiteRookBoard(), whiteRookImg);
        drawPieces(board.getWhiteKnightBoard(), whiteKnightImg);
        drawPieces(board.getWhiteBishopBoard(), whiteBishopImg);
        drawPieces(board.getWhiteQueenBoard(), whiteQueenImg);
        drawPieces(board.getWhiteKingBoard(), whiteKingImg);

        drawPieces(board.getBlackPawnBoard(), blackPawnImg);
        drawPieces(board.getBlackRookBoard(), blackRookImg);
        drawPieces(board.getBlackKnightBoard(), blackKnightImg);
        drawPieces(board.getBlackBishopBoard(), blackBishopImg);
        drawPieces(board.getBlackQueenBoard(), blackQueenImg);
        drawPieces(board.getBlackKingBoard(), blackKingImg);
    }


    private void drawPieces(long bitboard, Image pieceImage) {
        for (int i = 0; i < 64; i++) {
            if (((bitboard >> i) & 1) == 1) {
                int row = 7 - (i / 8);
                int col = 7 - (i % 8);

                ImageView pieceView = new ImageView(pieceImage);
                pieceView.setFitWidth(70);
                pieceView.setFitHeight(70);
                pieceView.setPreserveRatio(true);

                squares[row][col].getChildren().add(pieceView);
            }
        }
    }


    private void setupComboBoxes() {
        // Populate all three ComboBoxes with values 1-64
        for (int i = 1; i <= 64; i++) {
            whitePawnAheadPositionCombo.getItems().add(i);
            blackPawnAheadPositionCombo.getItems().add(i);
            whitePawnCapturePositionCombo.getItems().add(i);
            blackPawnCapturePositionCombo.getItems().add(i);
            kingPositionCombo.getItems().add(i);
            knightPositionCombo.getItems().add(i);
            bishopPositionCombo.getItems().add(i);
            rookPositionCombo.getItems().add(i);
            queenPositionCombo.getItems().add(i);
            rayCombo1.getItems().add(i);
            rayCombo2.getItems().add(i);
        }

        String[] FEN = FenTranslator.FENS;
        for (String s : FEN) {
            fenCombo.getItems().add(s);
        }

        // Add listeners to update board when selection changes
        whitePawnAheadPositionCombo.setOnAction(e -> {
            Integer position = whitePawnAheadPositionCombo.getValue();
            if (position != null) {
                drawPieces(position, MoveGenerator.pawnMoves[0][position - 1]);
            }
        });

        blackPawnAheadPositionCombo.setOnAction(e -> {
            Integer position = blackPawnAheadPositionCombo.getValue();
            if (position != null) {
                drawPieces(position, MoveGenerator.pawnMoves[2][position - 1]);
            }
        });

        whitePawnCapturePositionCombo.setOnAction(e -> {
            Integer position = whitePawnCapturePositionCombo.getValue();
            if (position != null) {
                drawPieces(position, MoveGenerator.pawnMoves[1][position - 1]);
            }
        });

        blackPawnCapturePositionCombo.setOnAction(e -> {
            Integer position = blackPawnCapturePositionCombo.getValue();
            if (position != null) {
                drawPieces(position, MoveGenerator.pawnMoves[3][position - 1]);
            }
        });

        kingPositionCombo.setOnAction(e -> {
            Integer position = kingPositionCombo.getValue();
            if (position != null) {
                drawPieces(position, MoveGenerator.kingMoves[position - 1]);
            }
        });

        bishopPositionCombo.setOnAction(e -> {
            Integer position = bishopPositionCombo.getValue();
            if (position != null) {
                drawPieces(position, MoveGenerator.bishopMoves[position - 1]);
            }
        });

        rookPositionCombo.setOnAction(e -> {
            Integer position = rookPositionCombo.getValue();
            if (position != null) {
                drawPieces(position, MoveGenerator.rookMoves[position - 1]);
            }
        });


        queenPositionCombo.setOnAction(e -> {
            Integer position = queenPositionCombo.getValue();
            if (position != null) {
                drawPieces(position, MoveGenerator.queenMoves[position - 1]);
            }
        });


        knightPositionCombo.setOnAction(e -> {
            Integer position = knightPositionCombo.getValue();
            if (position != null) {
                drawPieces(position, MoveGenerator.knightMoves[position - 1]);
            }
        });

        rayCombo1.setOnAction(e -> {
            Integer position1 = rayCombo1.getValue();
            Integer position2 = rayCombo2.getValue();

            if (position1 != null && position2 != null) {
                drawPieces(position1, MoveGenerator.rayMovement[position1 - 1][position2 - 1]);
                System.out.println(MoveGenerator.rayMovement[position1 - 1][position2 - 1]);
            }
        });

        rayCombo2.setOnAction(e -> {
            Integer position1 = rayCombo1.getValue();
            Integer position2 = rayCombo2.getValue();

            if (position1 != null && position2 != null) {
                drawPieces(position2, MoveGenerator.rayMovement[position1 - 1][position2 - 1]);
                System.out.println(MoveGenerator.rayMovement[position1 - 1][position2 - 1]);
            }
        });

        fenCombo.setOnAction(e -> {
            String fen = fenCombo.getValue();
            FenTranslator.translate(fen, board);

            updateBoardDisplay();
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
