package chess.bitboard;

import chess.move.Move;

public class BitBoard implements Board{
    private long whitePawnBoard;
    private long whiteRookBoard;
    private long whiteKnightBoard;
    private long whiteBishopBoard;
    private long whiteQueenBoard;
    private long whiteKingBoard;

    private long blackPawnBoard;
    private long blackRookBoard;
    private long blackKnightBoard;
    private long blackBishopBoard;
    private long blackQueenBoard;
    private long blackKingBoard;

    private long numberOfMovesLeft;

    private long whitePieces;
    private long blackPieces;


    public boolean isWhiteTurn;
    private Double timeLeftForWhite;
    private Double timeLeftForBlack;

    public long getWhitePawnBoard() {
        return whitePawnBoard;
    }

    public long getWhiteRookBoard() {
        return whiteRookBoard;
    }

    public long getWhiteKnightBoard() {
        return whiteKnightBoard;
    }

    public long getWhiteBishopBoard() {
        return whiteBishopBoard;
    }

    public long getWhiteKingBoard() {
        return whiteKingBoard;
    }

    public long getWhiteQueenBoard() {
        return whiteQueenBoard;
    }

    public long getBlackPawnBoard() {
        return blackPawnBoard;
    }

    public long getBlackRookBoard() {
        return blackRookBoard;
    }

    public long getBlackKnightBoard() {
        return blackKnightBoard;
    }

    public long getBlackQueenBoard() {
        return blackQueenBoard;
    }

    public long getBlackBishopBoard() {
        return blackBishopBoard;
    }

    public long getBlackKingBoard() {
        return blackKingBoard;
    }



    public BitBoard(){
        isWhiteTurn = true;
        timeLeftForWhite = 10.0;
        timeLeftForBlack = 10.0;

        whitePawnBoard |= 255 << 8;
        whiteRookBoard |= 0b10000001;
        whiteKnightBoard |= 0b01000010;
        whiteBishopBoard |= 0b00100100;
        whiteQueenBoard |= 0b00001000;
        whiteKingBoard |= 0b00010000;

        blackPawnBoard |= whitePawnBoard << 8*5;
        blackRookBoard |= whiteRookBoard << 8*7;
        blackKnightBoard |= whiteKnightBoard << 8*7;
        blackBishopBoard |= whiteBishopBoard << 8*7;
        blackQueenBoard |= whiteQueenBoard << 8*7;
        blackKingBoard |= whiteKingBoard << 8*7;

        whitePieces = whitePawnBoard | whiteRookBoard | whiteKnightBoard | whiteBishopBoard | whiteQueenBoard | whiteKingBoard;
        blackPieces = blackPawnBoard | blackRookBoard | blackKnightBoard | blackBishopBoard | blackQueenBoard | blackKingBoard;

        numberOfMovesLeft = 200;
    }


    public void makeMove(Move move){

    }


}
