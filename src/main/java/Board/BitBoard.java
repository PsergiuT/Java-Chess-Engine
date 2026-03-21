package Board;

import Move.Move;
import lombok.Getter;
import lombok.Setter;


public class BitBoard implements Board {
    @Getter
    private final long[] board = new long[14];

    private final int[] savedBoardState = new int[2048];


    @Getter @Setter
    private int enPassantSquare = -1;
    @Getter @Setter
    private int halfMoveClock;

    private int index = 0;
    public boolean isWhiteTurn;
    public boolean isCheckMate;
    private Double timeLeftForWhite;        // in seconds
    private Double timeLeftForBlack;        // in seconds
    int castlingRights = 0xF;

    // castling
    @Override
    public void setWhiteKingCastle(boolean whiteKingCastle) {
        if(whiteKingCastle) this.castlingRights |= (1 << 3) ;
        else this.castlingRights &= ~(1 << 3);
    }
    @Override
    public boolean isWhiteKingCastle() {
        return (castlingRights & (1 << 3)) != 0;
    }
    @Override
    public void setWhiteQueenCastle(boolean whiteQueenCastle) {
        if(whiteQueenCastle) this.castlingRights |= (1 << 2) ;
        else this.castlingRights &= ~(1 << 2);
    }
    @Override
    public boolean isWhiteQueenCastle() {
        return (castlingRights & (1 << 2)) != 0;
    }


    @Override
    public void setBlackKingCastle(boolean blackKingCastle) {
        if(blackKingCastle) this.castlingRights |= (1 << 1) ;
        else this.castlingRights &= ~(1 << 1);
    }
    @Override
    public boolean isBlackKingCastle() {
        return (castlingRights & (1 << 1)) != 0;
    }
    @Override
    public void setBlackQueenCastle(boolean blackQueenCastle) {
        if(blackQueenCastle) this.castlingRights |= 1 ;
        else this.castlingRights &= ~1;
    }
    @Override
    public boolean isBlackQueenCastle() {
        return (castlingRights & 1) != 0;
    }


    // turns and time
    @Override
    public void setIsWhiteTurn(boolean isWhiteTurn) {
        this.isWhiteTurn = isWhiteTurn;
    }
    @Override
    public void decrementTimeForWhite(double time) {
        timeLeftForWhite -= time;
    }
    @Override
    public void decrementTimeForBlack(double time) {
        timeLeftForBlack -= time;
    }
    @Override
    public Double getTimeForWhite() {
        return timeLeftForWhite;
    }
    @Override
    public Double getTimeForBlack() {
        return timeLeftForBlack;
    }


    // board getters
    @Override
    public long getWhitePawnBoard() {
        return board[0];
    }
    @Override
    public long getWhiteKnightBoard() {
        return board[1];
    }
    @Override
    public long getWhiteRookBoard() {
        return board[2];
    }
    @Override
    public long getWhiteBishopBoard() {
        return board[3];
    }
    @Override
    public long getWhiteQueenBoard() {
        return board[4];
    }
    @Override
    public long getWhiteKingBoard() {
        return board[5];
    }

    @Override
    public long getBlackPawnBoard() {
        return board[8];
    }
    @Override
    public long getBlackKnightBoard() {
        return board[9];
    }
    @Override
    public long getBlackRookBoard() {
        return board[10];
    }
    @Override
    public long getBlackBishopBoard() {
        return board[11];
    }
    @Override
    public long getBlackQueenBoard() {
        return board[12];
    }
    @Override
    public long getBlackKingBoard() {
        return board[13];
    }

    // board setters
    public void setWhitePawnBoard(long board) {
        this.board[0] = board;
    }
    public void setWhiteKnightBoard(long board) {
        this.board[1] = board;
    }
    public void setWhiteRookBoard(long board) {
        this.board[2] = board;
    }
    public void setWhiteBishopBoard(long board) {
        this.board[3] = board;
    }
    public void setWhiteQueenBoard(long board) {
        this.board[4] = board;
    }
    public void setWhiteKingBoard(long board) {
        this.board[5] = board;
    }


    public void setBlackPawnBoard(long board) {
        this.board[8] = board;
    }
    public void setBlackKnightBoard(long board) {
        this.board[9] = board;
    }
    public void setBlackRookBoard(long board) {
        this.board[10] = board;
    }
    public void setBlackBishopBoard(long board) {
        this.board[11] = board;
    }
    public void setBlackQueenBoard(long board) {
        this.board[12] = board;
    }
    public void setBlackKingBoard(long board) {
        this.board[13] = board;
    }


    // boards
    @Override
    public long getWhitePieces() {
        return board[0] | board[1] | board[2] | board[3] | board[4] | board[5];
    }
    @Override
    public long getBlackPieces() {
        return  board[8] | board[9] | board[10] | board[11] | board[12] | board[13];
    }


    public BitBoard(double time) {
        isWhiteTurn = true;
        isCheckMate = false;
        timeLeftForWhite = time * 60;           //minutes to seconds
        timeLeftForBlack = time * 60;

        board[0] |= 255 << 8;          //Pawn
        board[1] |= 0b01000010;        //Knight
        board[2] |= 0b10000001;        //Rook
        board[3] |= 0b00100100;        //Bishop
        board[4] |= 0b00010000;        //Queen
        board[5] |= 0b00001000;        //King
        board[6] = board[0] | board[1] | board[2] | board[3] | board[4] | board[5];


        board[8] |= board[0] << 8 * 5;      //Pawn
        board[9] |= board[1] << 8 * 7;      //Knight
        board[10] |= board[2] << 8 * 7;      //Rook
        board[11] |= board[3] << 8 * 7;      //Bishop
        board[12] |= board[4] << 8 * 7;      //Queen
        board[13] |= board[5] << 8 * 7;      //King
        board[7] = board[8] | board[9] | board[10] | board[11] | board[12] | board[13];

    }




    @Override
    public void makeMove(int move) {
        saveGameState();
        halfMoveClock++;
        move(move);
        isWhiteTurn = !isWhiteTurn;
    }


    // saving game state for undo move
    private void saveGameState() {
        savedBoardState[index] = castlingRights & 0xF |
                (enPassantSquare & 0xFF) << 4 |
                (halfMoveClock & 0xFF) << 12;
        index++;
    }

    // implements the mechanics behind moving/capturing/castling/promotion/en passant a piece on the board
    private void move(int move) {
        // physically moves the piece on the board
        movePiece(move);

        // capture logic
        if (Move.isCapture(move)) {
            if (Move.isEnPassant(move)) captureEnPassant(move);
            else capturePiece(move);
        }

        //castling logic
        if (Move.isCastling(move)) {
            makeCastleMove(move);
        }

        //pawn promotion logic
        if (Move.isPromotion(move)) {
            //replace the pawn with the new promotion piece
            board[Move.getPromotion(move)] |= 1L << Move.getTo(move);
            board[Move.getPiece(move)] &= ~(1L << Move.getTo(move));
        }

        //checkmate logic
        if (isWhiteTurn) {
            if(getWhiteKingBoard() == 0){
                isCheckMate = true;
            }
        }
        else{
            if(getBlackKingBoard() == 0){
                isCheckMate = true;
            }
        }

    }

    //physically moves the piece on the board
    private void movePiece(int move) {
        //pawn enPassan square checking
        if(Move.getPiece(move) % 8 == 0 && Math.abs(Move.getFrom(move) - Move.getTo(move)) == 16){
            //pawn moved to double square
            this.enPassantSquare = (Move.getFrom(move) + Move.getTo(move)) / 2;
        }
        else{
            this.enPassantSquare = -1;
        }

        //actually moves the piece on the board
        board[Move.getPiece(move)] &= ~(1L << Move.getFrom(move));
        board[Move.getPiece(move)] |= 1L << Move.getTo(move);

        //reset halfMoveClock
        if(Move.getPiece(move) == 0 || Move.getPiece(move) == 8){
            halfMoveClock = 0;
        }

        //reset castling rights
        checkCastlingRights(move);
    }

    private void checkCastlingRights(int move){
        if(Move.getPiece(move) == 5){
            setWhiteKingCastle(false);
            setWhiteQueenCastle(false);
        }
        if(Move.getPiece(move) == 13){
            setBlackKingCastle(false);
            setBlackQueenCastle(false);
        }
        if(Move.getPiece(move) == 2 && Move.getFrom(move) == 0){
            setWhiteKingCastle(false);
        }
        if(Move.getPiece(move) == 2 && Move.getFrom(move) == 7){
            setWhiteQueenCastle(false);
        }
        if(Move.getPiece(move) == 10 && Move.getFrom(move) == 56){
            setBlackKingCastle(false);
        }
        if(Move.getPiece(move) == 10 && Move.getFrom(move) == 63){
            setBlackQueenCastle(false);
        }
    }

    //enPassant capturing logic
    private void captureEnPassant(int move) {
        halfMoveClock = 0;
        if (isWhiteTurn) board[8] &= ~(1L << (Move.getTo(move) - 8));
        else board[0] &= ~(1L << (Move.getTo(move) + 8));

    }

    //piece capturing logic
    private void capturePiece(int move) {
        halfMoveClock = 0;
        board[Move.getCapture(move)] &= ~(1L << (Move.getTo(move)));

        if(Move.getTo(move) == 0){
            setWhiteKingCastle(false);
        }
        if(Move.getTo(move) == 7){
            setWhiteQueenCastle(false);
        }
        if(Move.getTo(move) == 56){
            setBlackKingCastle(false);
        }
        if(Move.getTo(move) == 63){
            setBlackQueenCastle(false);
        }
    }

    //castling logic
    private void makeCastleMove(int move){
        if(isWhiteTurn) {
            if(Move.getTo(move) == 5){
                //if rook on the left
                board[2] ^= 0x0000000000000080L;      //delete the rook on the left
                board[2] |= 0x0000000000000010L;
            }else{
                //if rook on the right
                board[2] ^= 0x0000000000000001L;      //delete the rook on the right
                board[2] |= 0x0000000000000004L;
            }
        }
        else {
            if(Move.getTo(move) == 61){
                //if rook on the left
                board[10] ^= 0x8000000000000000L;      //delete the rook on the left
                board[10] |= 0x1000000000000000L;
            }else{
                //if rook on the right
                board[10] ^= 0x0100000000000000L;      //delete the rook on the left
                board[10] |= 0x0400000000000000L;
            }
        }
    }













    @Override
    public void undoMove(int move){
        isWhiteTurn = !isWhiteTurn;
        undoMovePiece(move);
        loadGameState();

        // undo capture
        if (Move.isCapture(move)) {
            if (Move.isEnPassant(move)) undoCaptureEnPassant(move);
            else undoCapturePiece(move);
        }

        // undo castling
        if (Move.isCastling(move)) {
            undoCastleMove(move);
        }

        // undo pawn promotion
        if (Move.isPromotion(move)) {
            //replace the pawn with the new promotion piece
            board[Move.getPromotion(move)] &= ~(1L << Move.getTo(move));
            board[Move.getPiece(move)] |= 1L << Move.getFrom(move);
        }

        // undo checkmate
        if (isWhiteTurn) {
            if(getWhiteKingBoard() != 0){
                isCheckMate = false;
            }
        }
        else{
            if(getBlackKingBoard() != 0){
                isCheckMate = false;
            }
        }
    }

    private void undoMovePiece(int move){
        board[Move.getPiece(move)] |= 1L << Move.getFrom(move);
        board[Move.getPiece(move)] &= ~(1L << Move.getTo(move));
    }

    private void loadGameState() {
        index--;
        int gameState = savedBoardState[index];
        castlingRights = gameState & 0xF;
        enPassantSquare = (gameState >> 4) & 0xFF;
        halfMoveClock = (gameState >> 12) & 0xFF;

    }

    private void undoCaptureEnPassant(int move) {
        if (isWhiteTurn) board[8] |= 1L << (Move.getTo(move) - 8);
        else board[0] |= 1L << (Move.getTo(move) + 8);
    }

    private void undoCapturePiece(int move) {
        board[Move.getCapture(move)] |= 1L << (Move.getTo(move));
    }

    private void undoCastleMove(int move){
        if(isWhiteTurn) {
            if(Move.getTo(move) == 5){
                //if rook on the left
                board[2] ^= 0x0000000000000010L;      //delete the rook on the left
                board[2] |= 0x0000000000000080L;
            }else{
                //if rook on the right
                board[2] ^= 0x0000000000000004L;      //delete the rook on the right
                board[2] |= 0x0000000000000001L;
            }
        }
        else {
            if(Move.getTo(move) == 61){
                //if rook on the left
                board[10] ^= 0x1000000000000000L;      //delete the rook on the left
                board[10] |= 0x8000000000000000L;
            }else{
                //if rook on the right
                board[10] ^= 0x0400000000000000L;      //delete the rook on the left
                board[10] |= 0x0100000000000000L;
            }
        }
    }
}
