//package chess.bitboard;
//
//import chess.move.Move;
//import lombok.Getter;
//import lombok.Setter;
//
//public class BitBoard implements Board {
//    @Getter
//    private final long[] board = new long[14];
//
//    @Getter
//    @Setter
//    private int numberOfMovesLeft;
//
//    public boolean isWhiteTurn;
//    public boolean isCheckMate;
//    private final Double timeLeftForWhite;
//    private final Double timeLeftForBlack;
//    @Getter
//    @Setter
//    private int enPassantSquare = -1;
//    private int lastEnPassantSquare = -1;
//
//    @Getter
//    @Setter
//    private boolean whiteQueenCastle = true;
//    @Getter
//    @Setter
//    private boolean blackQueenCastle = true;
//    @Getter
//    @Setter
//    private boolean whiteKingCastle = true;
//    @Getter
//    @Setter
//    private boolean blackKingCastle = true;
//
//    private boolean lastWhiteQueenCastle = true;
//    private boolean lastBlackQueenCastle = true;
//    private boolean lastWhiteKingCastle = true;
//    private boolean lastBlackKingCastle = true;
//
//    public void setIsWhiteTurn(boolean isWhiteTurn) {
//        this.isWhiteTurn = isWhiteTurn;
//    }
//
//    public Double getTimeForWhite() {
//        return timeLeftForWhite;
//    }
//
//    public Double getTimeForBlack() {
//        return timeLeftForBlack;
//    }
//
//    public int getMovesLeft() {
//        return  numberOfMovesLeft;
//    }
//
//    public long getWhitePawnBoard() {
//        return board[0];
//    }
//
//    public long getWhiteKnightBoard() {
//        return board[1];
//    }
//
//    public long getWhiteRookBoard() {
//        return board[2];
//    }
//
//    public long getWhiteBishopBoard() {
//        return board[3];
//    }
//
//    public long getWhiteQueenBoard() {
//        return board[4];
//    }
//
//    public long getWhiteKingBoard() {
//        return board[5];
//    }
//
//
//    public long getBlackPawnBoard() {
//        return board[8];
//    }
//
//    public long getBlackKnightBoard() {
//        return board[9];
//    }
//
//    public long getBlackRookBoard() {
//        return board[10];
//    }
//
//    public long getBlackBishopBoard() {
//        return board[11];
//    }
//
//    public long getBlackQueenBoard() {
//        return board[12];
//    }
//
//    public long getBlackKingBoard() {
//        return board[13];
//    }
//
//    public void setWhitePawnBoard(long board) {
//        this.board[0] = board;
//    }
//
//    public void setWhiteKnightBoard(long board) {
//        this.board[1] = board;
//    }
//
//    public void setWhiteRookBoard(long board) {
//        this.board[2] = board;
//    }
//
//    public void setWhiteBishopBoard(long board) {
//        this.board[3] = board;
//    }
//
//    public void setWhiteQueenBoard(long board) {
//        this.board[4] = board;
//    }
//
//    public void setWhiteKingBoard(long board) {
//        this.board[5] = board;
//    }
//
//
//    public void setBlackPawnBoard(long board) {
//        this.board[8] = board;
//    }
//
//    public void setBlackKnightBoard(long board) {
//        this.board[9] = board;
//    }
//
//    public void setBlackRookBoard(long board) {
//        this.board[10] = board;
//    }
//
//    public void setBlackBishopBoard(long board) {
//        this.board[11] = board;
//    }
//
//    public void setBlackQueenBoard(long board) {
//        this.board[12] = board;
//    }
//
//    public void setBlackKingBoard(long board) {
//        this.board[13] = board;
//    }
//
//
//
//
//    public long getWhitePieces() {
//        return board[0] | board[1] | board[2] | board[3] | board[4] | board[5];
//    }
//
//    public long getBlackPieces() {
//        return  board[8] | board[9] | board[10] | board[11] | board[12] | board[13];
//    }
//
//
//    public BitBoard(double time, int numberOfMoves) {
//        isWhiteTurn = true;
//        isCheckMate = false;
//        timeLeftForWhite = time;
//        timeLeftForBlack = time;
//
//        board[0] |= 255 << 8;          //Pawn
//        board[1] |= 0b01000010;        //Knight
//        board[2] |= 0b10000001;        //Rook
//        board[3] |= 0b00100100;        //Bishop
//        board[4] |= 0b00010000;        //Queen
//        board[5] |= 0b00001000;        //King
//        board[6] = board[0] | board[1] | board[2] | board[3] | board[4] | board[5];
//
//
//        board[8] |= board[0] << 8 * 5;      //Pawn
//        board[9] |= board[1] << 8 * 7;      //Knight
//        board[10] |= board[2] << 8 * 7;      //Rook
//        board[11] |= board[3] << 8 * 7;      //Bishop
//        board[12] |= board[4] << 8 * 7;      //Queen
//        board[13] |= board[5] << 8 * 7;      //King
//        board[7] = board[8] | board[9] | board[10] | board[11] | board[12] | board[13];
//
//        this.numberOfMovesLeft = numberOfMoves;
//    }
//
//
//
//
//
//    public void makeMove(int move) {
//        //no need to validate
//        //just need to update the board
//        move(move);
//
//        isWhiteTurn = !isWhiteTurn;
//        numberOfMovesLeft--;
//    }
//
//
//    private void captureEnPassant(int move) {
//        if (isWhiteTurn) board[8] &= ~(1L << (Move.getTo(move) - 8));
//        else board[0] &= ~(1L << (Move.getTo(move) + 8));
//
//    }
//
//    private void capturePiece(int move) {
//        board[Move.getCapture(move)] &= ~(1L << (Move.getTo(move)));
//
//        if(Move.getTo(move) == 0){
//            lastWhiteKingCastle = whiteKingCastle;
//            whiteKingCastle = false;
//        }
//        if(Move.getTo(move) == 7){
//            lastWhiteQueenCastle = whiteQueenCastle;
//            whiteQueenCastle = false;
//        }
//        if(Move.getTo(move) == 56){
//            lastBlackKingCastle = blackKingCastle;
//            blackKingCastle = false;
//        }
//        if(Move.getTo(move) == 63){
//            lastBlackQueenCastle = blackQueenCastle;
//            blackQueenCastle = false;
//        }
//    }
//
//    private void movePiece(int move) {
//        this.lastEnPassantSquare = enPassantSquare;
//        if(Move.getPiece(move) % 8 == 0 && Math.abs(Move.getFrom(move) - Move.getTo(move)) == 16){
//            //pawn moved to double square
//            this.enPassantSquare = (Move.getFrom(move) + Move.getTo(move)) / 2;
//        }
//        else{
//            this.enPassantSquare = -1;
//        }
//
//        board[Move.getPiece(move)] &= ~(1L << Move.getFrom(move));
//        board[Move.getPiece(move)] |= 1L << Move.getTo(move);
//
//        if(Move.getPiece(move) == 5){
//            lastWhiteKingCastle = whiteKingCastle;
//            lastWhiteQueenCastle = whiteQueenCastle;
//            whiteKingCastle = false;
//            whiteQueenCastle = false;
//        }
//
//        if(Move.getPiece(move) == 13){
//            lastBlackKingCastle = blackKingCastle;
//            lastBlackQueenCastle = blackQueenCastle;
//            blackKingCastle = false;
//            blackQueenCastle = false;
//        }
//
//        if(Move.getPiece(move) == 2 && Move.getFrom(move) == 0){
//            lastWhiteKingCastle = whiteKingCastle;
//            whiteKingCastle = false;
//        }
//        if(Move.getPiece(move) == 2 && Move.getFrom(move) == 7){
//            lastWhiteQueenCastle = whiteQueenCastle;
//            whiteQueenCastle = false;
//        }
//        if(Move.getPiece(move) == 10 && Move.getFrom(move) == 56){
//            lastBlackKingCastle = blackKingCastle;
//            blackKingCastle = false;
//        }
//        if(Move.getPiece(move) == 10 && Move.getFrom(move) == 63){
//            lastBlackQueenCastle = blackQueenCastle;
//            blackQueenCastle = false;
//        }
//    }
//
//    private void move(int move) {
//        movePiece(move);
//
//        if (Move.isCapture(move)) {
//            if (Move.isEnPassant(move)) captureEnPassant(move);
//            else capturePiece(move);
//        }
//
//        if (Move.isCastling(move)) {
//            if(isWhiteTurn) {
//
//                if(Move.getTo(move) == 5){
//                    //if rook on the left
//                    board[2] ^= 0x0000000000000080L;      //delete the rook on the left
//                    board[2] |= 0x0000000000000010L;
//                }else{
//                    //if rook on the right
//                    board[2] ^= 0x0000000000000001L;      //delete the rook on the right
//                    board[2] |= 0x0000000000000004L;
//                }
//            }
//            else {
//
//                if(Move.getTo(move) == 61){
//                    //if rook on the left
//                    board[10] ^= 0x8000000000000000L;      //delete the rook on the left
//                    board[10] |= 0x1000000000000000L;
//                }else{
//                    //if rook on the right
//                    board[10] ^= 0x0100000000000000L;      //delete the rook on the left
//                    board[10] |= 0x0400000000000000L;
//                }
//            }
//        }
//
//        if (Move.isPromotion(move)) {
//            //replace the pawn with the new promotion piece
//            board[Move.getPromotion(move)] |= 1L << Move.getTo(move);
//            board[Move.getPiece(move)] &= ~(1L << Move.getTo(move));
//        }
//
//        if (move == 0) {
//            isCheckMate = true;
//        }
//    }
//
//
//
//
//
//
//
//
//
//    public void undoMove(int move){
//        isWhiteTurn = !isWhiteTurn;
//        numberOfMovesLeft++;
//        undoMovePiece(move);
//
//        if (Move.isCapture(move)) {
//            if (Move.isEnPassant(move)) undoCaptureEnPassant(move);
//            else undoCapturePiece(move);
//        }
//
//        if (Move.isCastling(move)) {
//            if(isWhiteTurn) {
//                if(Move.getTo(move) == 5){
//                    //if rook on the left
//                    whiteQueenCastle = true;
//                    board[2] ^= 0x0000000000000010L;      //delete the rook on the left
//                    board[2] |= 0x0000000000000080L;
//                }else{
//                    //if rook on the right
//                    whiteKingCastle = true;
//                    board[2] ^= 0x0000000000000004L;      //delete the rook on the right
//                    board[2] |= 0x0000000000000001L;
//                }
//            }
//            else {
//                if(Move.getTo(move) == 61){
//                    //if rook on the left
//                    blackQueenCastle = true;
//                    board[10] ^= 0x1000000000000000L;      //delete the rook on the left
//                    board[10] |= 0x8000000000000000L;
//                }else{
//                    //if rook on the right
//                    blackKingCastle = true;
//                    board[10] ^= 0x0400000000000000L;      //delete the rook on the left
//                    board[10] |= 0x0100000000000000L;
//                }
//            }
//
//        }
//
//        if (Move.isPromotion(move)) {
//            //replace the pawn with the new promotion piece
//            board[Move.getPromotion(move)] &= ~(1L << Move.getTo(move));
//            board[Move.getPiece(move)] |= 1L << Move.getFrom(move);
//        }
//
//        if (move == 0) {
//            isCheckMate = true;
//        }
//    }
//
//
//
//
//    private void undoMovePiece(int move){
//        this.enPassantSquare = lastEnPassantSquare;
//
//        board[Move.getPiece(move)] |= 1L << Move.getFrom(move);
//        board[Move.getPiece(move)] &= ~(1L << Move.getTo(move));
//
//
//        if(Move.getPiece(move) == 5){
//            whiteKingCastle = lastWhiteKingCastle;
//            whiteQueenCastle = lastWhiteQueenCastle;
//        }
//
//        if(Move.getPiece(move) == 13){
//            blackKingCastle = lastBlackKingCastle;
//            blackQueenCastle = lastBlackQueenCastle;
//        }
//
//        if(Move.getPiece(move) == 2 && Move.getFrom(move) == 0){
//            whiteKingCastle = lastWhiteKingCastle;
//        }
//        if(Move.getPiece(move) == 2 && Move.getFrom(move) == 7){
//            whiteQueenCastle = lastWhiteQueenCastle;
//        }
//        if(Move.getPiece(move) == 10 && Move.getFrom(move) == 56){
//            blackKingCastle = lastBlackKingCastle;
//        }
//        if(Move.getPiece(move) == 10 && Move.getFrom(move) == 63){
//            blackQueenCastle = lastBlackQueenCastle;
//
//        }
//    }
//
//
//
//
//    private void undoCaptureEnPassant(int move) {
//        if (isWhiteTurn) board[8] |= 1L << (Move.getTo(move) - 8);
//        else board[0] |= 1L << (Move.getTo(move) + 8);
//
//    }
//
//
//
//    private void undoCapturePiece(int move) {
//        board[Move.getCapture(move)] |= 1L << (Move.getTo(move));
//
//        if(Move.getTo(move) == 0){
//            whiteKingCastle = lastWhiteKingCastle;
//        }
//        if(Move.getTo(move) == 7){
//            whiteQueenCastle = lastWhiteQueenCastle;
//        }
//        if(Move.getTo(move) == 56){
//            blackKingCastle = lastBlackKingCastle;
//        }
//        if(Move.getTo(move) == 63){
//            blackQueenCastle = lastWhiteQueenCastle;
//        }
//    }
//
//
//}
