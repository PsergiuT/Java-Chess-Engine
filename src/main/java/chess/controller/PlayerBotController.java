package chess.controller;


import chess.bitboard.BitBoard;
import chess.bitboard.MoveGenerator;
import chess.move.Move;
import chess.move.MoveList;
import chess.search.Bot;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
    private MoveGenerator moveGenerator;
    private MoveList validMovesFromSelectedSquare = new MoveList();

    private int lastSelectedRow = -1;
    private int lastSelectedCol = -1;
    private int botLastSelectedRow = -1;
    private int botLastSelectedCol = -1;


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
                int col = 7 - (i % 8);

                ImageView pieceView = new ImageView(pieceImage);
                pieceView.setFitWidth(70);
                pieceView.setFitHeight(70);
                pieceView.setPreserveRatio(true);

                squares[row][col].getChildren().add(pieceView);
            }
        }
    }


    private void handleSquareClick(int row, int col) {
        //System.out.println("Selected row: " + row + " col: " + col);
        int indexInBoard = (7 - row) * 8 + (7 - col);


        if (selectedSquare == null) {
            // Select a piece
            if (!squares[row][col].getChildren().isEmpty()) {
                MoveList allValidMoves = moveGenerator.generateMoves(board, board.isWhiteTurn);
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
                showAvailablePositions();
                if(lastSelectedRow != -1 && lastSelectedCol != -1) {
                    resetSquareStyle(lastSelectedRow, lastSelectedCol);
                }
            }

        } else {

            int[] validMoves = validMovesFromSelectedSquare.getMoves();

            if(moveGenerator.generateMoves(board, board.isWhiteTurn).getSize() == 0){
                //checkmate
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Checkmate!", "Checkmate!");
            }

            for(int i = 0; i < validMovesFromSelectedSquare.getSize(); i++){
                if(indexInBoard == Move.getTo(validMoves[i])){
                    //printBoard();
                    board.makeMove(validMoves[i]);
                    movePiece(selectedRow, selectedCol, row, col);

                    // Switch turns
                    currentTurnLabel.setText(board.isWhiteTurn ? "White" : "Black");


                    // Deselect
                    setSquareStyleAfterMove(selectedRow, selectedCol, "#ffffff");
                    if(lastSelectedRow != -1 && lastSelectedCol != -1) {
                        resetSquareStyle(lastSelectedRow, lastSelectedCol);
                    }

                    //--------------make bot move---------------
                    int bestMove = Bot.bestMove(board, moveGenerator, board.isWhiteTurn);

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


                    // Deselect
                    setSquareStyleAfterMove(fromRow, fromCol, "#5f5f5f");
                    if(botLastSelectedCol != -1 && botLastSelectedRow != -1) {
                        resetSquareStyle(botLastSelectedRow, botLastSelectedCol);
                    }

                    botLastSelectedRow = fromRow;
                    botLastSelectedCol = fromCol;


                    break;
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
    private void initialize(){
        board = new BitBoard(10, 200);
        moveGenerator = new MoveGenerator();
        loadPieceImages();
        setupBoard();
        updateBoardDisplay();
    }



}
