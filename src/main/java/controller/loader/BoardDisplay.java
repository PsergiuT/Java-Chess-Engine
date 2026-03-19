package controller.loader;

import bitboard.Board;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class BoardDisplay {
    private static StackPane[][] squares = new StackPane[8][8];

    public static void updateBoardDisplay(StackPane[][] sq, Board board) {
        // Clear all squares
        squares = sq;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col].getChildren().clear();
            }
        }

        // Draw pieces based on bitboards
        drawPieces(board.getWhitePawnBoard(), ImageLoader.whitePawnImg);
        drawPieces(board.getWhiteRookBoard(), ImageLoader.whiteRookImg);
        drawPieces(board.getWhiteKnightBoard(), ImageLoader.whiteKnightImg);
        drawPieces(board.getWhiteBishopBoard(), ImageLoader.whiteBishopImg);
        drawPieces(board.getWhiteQueenBoard(), ImageLoader.whiteQueenImg);
        drawPieces(board.getWhiteKingBoard(), ImageLoader.whiteKingImg);

        drawPieces(board.getBlackPawnBoard(), ImageLoader.blackPawnImg);
        drawPieces(board.getBlackRookBoard(), ImageLoader.blackRookImg);
        drawPieces(board.getBlackKnightBoard(), ImageLoader.blackKnightImg);
        drawPieces(board.getBlackBishopBoard(), ImageLoader.blackBishopImg);
        drawPieces(board.getBlackQueenBoard(), ImageLoader.blackQueenImg);
        drawPieces(board.getBlackKingBoard(), ImageLoader.blackKingImg);

        //blackTimerLabel.setText(board.getTimeForBlack().toString());
        //whiteTimerLabel.setText(board.getTimeForWhite().toString());
    }


    private static void drawPieces(long bitboard, Image pieceImage) {
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
}
