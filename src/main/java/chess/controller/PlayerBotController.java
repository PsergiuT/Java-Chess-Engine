package chess.controller;


import chess.bitboard.BitBoard;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

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
    private Label lastMoveLabel;

    @FXML
    private GridPane chessBoard;

    private BitBoard board;
    private StackPane[][] squares = new StackPane[8][8];
    private StackPane selectedSquare = null;
    private int selectedRow = -1;
    private int selectedCol = -1;


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
                int col = i % 8;

                ImageView pieceView = new ImageView(pieceImage);
                pieceView.setFitWidth(70);
                pieceView.setFitHeight(70);
                pieceView.setPreserveRatio(true);

                squares[row][col].getChildren().add(pieceView);
            }
        }
    }


    private void handleSquareClick(int row, int col) {
        if (selectedSquare == null) {
            // Select a piece
            if (!squares[row][col].getChildren().isEmpty()) {
                selectedSquare = squares[row][col];
                selectedRow = row;
                selectedCol = col;

                // Highlight selected square
                selectedSquare.setStyle(selectedSquare.getStyle() + "; -fx-border-color: #FFD700; -fx-border-width: 4;");
            }
        } else {
            // Move piece (you'll need to implement move validation)
            //if (isValidMove(selectedRow, selectedCol, row, col)) {

            movePiece(selectedRow, selectedCol, row, col);

            // Switch turns
            board.isWhiteTurn = !board.isWhiteTurn;
            currentTurnLabel.setText(board.isWhiteTurn ? "White" : "Black");

            // Bot move (if it's black's turn)
//            if (!board.isWhiteTurn) {
//                // TODO: Implement bot move logic
//            }


            // Deselect
            resetSquareStyle(selectedRow, selectedCol);
            selectedSquare = null;
            selectedRow = -1;
            selectedCol = -1;
        }
    }


    private void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        // TODO: Update bitboards in your BitBoard class
        // For now, just visually move the piece

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
        board = new BitBoard();
        loadPieceImages();
        setupBoard();
        updateBoardDisplay();
    }



}
