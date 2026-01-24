package chess.search.BotV2;

import chess.bitboard.BitBoard;

public class Evaluation {
    private final static int pawn = 100;
    private final static int knight = 320;
    private final static int bishop = 330;
    private final static int rook = 500;
    private final static int queen = 900;
    private final static int king = 20000;

    public static int evaluate(BitBoard board){
        int sumWhite = Long.bitCount(board.getWhitePawnBoard()) * pawn +
                Long.bitCount(board.getWhiteKnightBoard()) * knight +
                Long.bitCount(board.getWhiteBishopBoard()) * bishop +
                Long.bitCount(board.getWhiteRookBoard()) * rook +
                Long.bitCount(board.getWhiteQueenBoard()) * queen +
                Long.bitCount(board.getWhiteKingBoard()) * king;

        int sumBlack = Long.bitCount(board.getBlackPawnBoard()) * pawn +
                Long.bitCount(board.getBlackKnightBoard()) * knight +
                Long.bitCount(board.getBlackBishopBoard()) * bishop +
                Long.bitCount(board.getBlackRookBoard()) * rook +
                Long.bitCount(board.getBlackQueenBoard()) * queen +
                Long.bitCount(board.getBlackKingBoard()) * king;

        return sumWhite - sumBlack;
    }
}
