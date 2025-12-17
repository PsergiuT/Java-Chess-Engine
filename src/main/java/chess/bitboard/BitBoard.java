package chess.bitboard;

import chess.move.Move;

public class BitBoard implements Board {
    private final long[] board = new long[14];

    private int numberOfMovesLeft;

    public boolean isWhiteTurn;
    public boolean isCheckMate;
    private Double timeLeftForWhite;
    private Double timeLeftForBlack;
    private int enPassantSquare = -1;

    public Double getTimeForWhite() {
        return timeLeftForWhite;
    }

    public Double getTimeForBlack() {
        return timeLeftForBlack;
    }

    public int getMovesLeft() {
        return  numberOfMovesLeft;
    }

    public long getWhitePawnBoard() {
        return board[0];
    }

    public long getWhiteKnightBoard() {
        return board[1];
    }

    public long getWhiteRookBoard() {
        return board[2];
    }

    public long getWhiteBishopBoard() {
        return board[3];
    }

    public long getWhiteQueenBoard() {
        return board[4];
    }

    public long getWhiteKingBoard() {
        return board[5];
    }


    public long getBlackPawnBoard() {
        return board[8];
    }

    public long getBlackKnightBoard() {
        return board[9];
    }

    public long getBlackRookBoard() {
        return board[10];
    }

    public long getBlackBishopBoard() {
        return board[11];
    }

    public long getBlackQueenBoard() {
        return board[12];
    }

    public long getBlackKingBoard() {
        return board[13];
    }

    public long getWhitePieces() {
        return board[0] | board[1] | board[2] | board[3] | board[4] | board[5];
    }

    public long getBlackPieces() {
        return  board[8] | board[9] | board[10] | board[11] | board[12] | board[13];
    }

    public long getWhiteSlidingPieces() {
        return board[2] | board[3] | board[4];
    }

    public long getBlackSlidingPieces() {
        return board[10] | board[11] | board[12];
    }

    public long[] getBoard(){
        return board;
    }

    public int getEnPassantSquare(){
        return enPassantSquare;
    }


    public BitBoard(double time, int numberOfMoves) {
        isWhiteTurn = true;
        isCheckMate = false;
        timeLeftForWhite = time;
        timeLeftForBlack = time;

        board[0] |= 255 << 8;          //Pawn
        board[1] |= 0b01000010;        //Knight
        board[2] |= 0b10000001;        //Rook
        board[3] |= 0b00100100;        //Bishop
        board[4] |= 0b00001000;        //Queen
        board[5] |= 0b00010000;        //King
        board[6] = board[0] | board[1] | board[2] | board[3] | board[4] | board[5];


        board[8] |= board[0] << 8 * 5;      //Pawn
        board[9] |= board[1] << 8 * 7;      //Knight
        board[10] |= board[2] << 8 * 7;      //Rook
        board[11] |= board[3] << 8 * 7;      //Bishop
        board[12] |= board[4] << 8 * 7;      //Queen
        board[13] |= board[5] << 8 * 7;      //King
        board[7] = board[8] | board[9] | board[10] | board[11] | board[12] | board[13];

        this.numberOfMovesLeft = numberOfMoves;
    }


    public void makeMove(int move) {
        //no need to validate
        //just need to update the board
        move(move);

        isWhiteTurn = !isWhiteTurn;
        numberOfMovesLeft--;
    }


    private void captureEnPassant(int move) {
        if (isWhiteTurn) board[8] &= ~(1L << (Move.getTo(move) - 8));
        else board[0] &= ~(1L << (Move.getTo(move) + 8));

    }

    private void capturePiece(int move) {
        board[Move.getCapture(move)] &= ~(1L << (Move.getTo(move)));
    }

    private void movePiece(int move) {
        if(Move.getPiece(move) % 8 == 0 && Math.abs(Move.getFrom(move) - Move.getTo(move)) == 16){
            //pawn moved to double square
            this.enPassantSquare = (Move.getFrom(move) + Move.getTo(move)) / 2;
        }
        else{
            this.enPassantSquare = -1;
        }
        board[Move.getPiece(move)] &= ~(1L << Move.getFrom(move));
        board[Move.getPiece(move)] |= 1L << Move.getTo(move);
    }

    private void move(int move) {
        movePiece(move);

        if (Move.isCapture(move)) {
            if (Move.isEnPassant(move)) captureEnPassant(move);
            else capturePiece(move);
            return;
        }

        if (Move.isCastling(move)) {
            if((board[Move.getPiece(move)] & (0x8000000000000080L)) != 0){
                //if rook on the left
                //move king(+3) on the left
                board[Move.getPiece(move) + 3] = board[Move.getPiece(move)] << 2;
            }else{
                //if rook on the right
                //move king(+3) on the right
                board[Move.getPiece(move) + 3] = board[Move.getPiece(move)] >> 2;
            }
            return;
        }

        if (Move.isPromotion(move)) {
            //replace the pawn with the new promotion piece
            board[Move.getPromotion(move)] |= 1L << Move.getTo(move);
            board[Move.getPiece(move)] &= ~(1L << Move.getTo(move));
            return;
        }

        if (move == 0) {
            isCheckMate = true;
        }
    }
}
