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

    public long getWhitePieces(){
        return whiteKingBoard | whiteQueenBoard | whiteBishopBoard | whitePawnBoard | whiteRookBoard | whiteKnightBoard;
    }

    public long getBlackPieces(){
        return blackKingBoard | blackPawnBoard | blackRookBoard |  blackQueenBoard | blackBishopBoard | blackKnightBoard;
    }


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


    public void makeMove(int move){
        //no need to validate


        //TODO implement castling

        int piece = Move.getPiece(move);

        switch(piece){
            case 1:
                //pawn
                movePawn(move);
                break;

            case 2:
                //knight
                moveKnight(move);
                break;

            case 3:
                //rook
                moveRook(move);
                break;

            case 4:
                //bishop
                moveBishop(move);
                break;

            case 5:
                //queen
                moveQueen(move);
                break;

            case 6:
                //king
                moveKing(move);
                break;

            default:
                break;
        }


        isWhiteTurn = !isWhiteTurn;
    }


    private void movePawn(int move){
        if(isWhiteTurn){
            whitePawnBoard &= ~(1L << Move.getFrom(move));
            whitePawnBoard |= 1L << Move.getTo(move);
            if(Move.isCapture(move)){
                int capturedPiece = Move.getCapture(move);
                if(Move.isEnPassant(move)){
                    switch(capturedPiece){
                        case 1:
                            blackPawnBoard &= ~(1L << (Move.getTo(move) - 8));
                            break;

                        case 2:
                            blackKnightBoard &= ~(1L << (Move.getTo(move) - 8));
                            break;

                        case 3:
                            blackRookBoard &= ~(1L << (Move.getTo(move) - 8));
                            break;

                        case 4:
                            blackBishopBoard &= ~(1L << (Move.getTo(move) - 8));
                            break;

                        case 5:
                            blackQueenBoard &= ~(1L << (Move.getTo(move) - 8));
                            break;

                    }
                }else{
                    switch(capturedPiece){
                        case 1:
                            blackPawnBoard &= ~(1L << Move.getTo(move));
                            break;

                        case 2:
                            blackKnightBoard &= ~(1L << Move.getTo(move));
                            break;

                        case 3:
                            blackRookBoard &= ~(1L << Move.getTo(move));
                            break;

                        case 4:
                            blackBishopBoard &= ~(1L << Move.getTo(move));
                            break;

                        case 5:
                            blackQueenBoard &= ~(1L << Move.getTo(move));
                            break;

                    }
                }

            }
        }
        else{
            blackPawnBoard &= ~(1L << Move.getFrom(move));
            blackPawnBoard |= 1L << Move.getTo(move);
            if(Move.isCapture(move)){
                int capturedPiece = Move.getCapture(move);
                if(Move.isEnPassant(move)){
                    switch(capturedPiece){
                        case 1:
                            whitePawnBoard &= ~(1L << (Move.getTo(move) + 8));
                            break;

                        case 2:
                            whiteKnightBoard &= ~(1L << (Move.getTo(move) + 8));
                            break;

                        case 3:
                            whiteRookBoard &= ~(1L << (Move.getTo(move) + 8));
                            break;

                        case 4:
                            whiteBishopBoard &= ~(1L << (Move.getTo(move) + 8));
                            break;

                        case 5:
                            whiteQueenBoard &= ~(1L << (Move.getTo(move) + 8));
                            break;

                    }
                }else{
                    switch(capturedPiece){
                        case 1:
                            whitePawnBoard &= ~(1L << Move.getTo(move));
                            break;

                        case 2:
                            whiteKnightBoard &= ~(1L << Move.getTo(move));
                            break;

                        case 3:
                            whiteRookBoard &= ~(1L << Move.getTo(move));
                            break;

                        case 4:
                            whiteBishopBoard &= ~(1L << Move.getTo(move));
                            break;

                        case 5:
                            whiteQueenBoard &= ~(1L << Move.getTo(move));
                            break;

                    }
                }
            }

        }
    }



    private void moveKnight(int move){
        if(isWhiteTurn){
            whiteKnightBoard &= ~(1L << Move.getFrom(move));
            whiteKnightBoard |= 1L << Move.getTo(move);
            if(Move.isCapture(move)){
                int capturedPiece = Move.getCapture(move);
                switch(capturedPiece){
                    case 1:
                        blackPawnBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 2:
                        blackKnightBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 3:
                        blackRookBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 4:
                        blackBishopBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 5:
                        blackQueenBoard &= ~(1L << Move.getTo(move));
                        break;

                }
            }
        }
        else{
            blackKnightBoard &= ~(1L << Move.getFrom(move));
            blackKnightBoard |= 1L << Move.getTo(move);
            if(Move.isCapture(move)){
                int capturedPiece = Move.getCapture(move);
                switch(capturedPiece){
                    case 1:
                        whitePawnBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 2:
                        whiteKnightBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 3:
                        whiteRookBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 4:
                        whiteBishopBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 5:
                        whiteQueenBoard &= ~(1L << Move.getTo(move));
                        break;

                }
            }
        }
    }
    private void moveRook(int move){

        if(isWhiteTurn){
            whiteRookBoard &= ~(1L << Move.getFrom(move));
            whiteRookBoard |= 1L << Move.getTo(move);
            if(Move.isCapture(move)){
                int capturedPiece = Move.getCapture(move);
                switch(capturedPiece){
                    case 1:
                        blackPawnBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 2:
                        blackKnightBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 3:
                        blackRookBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 4:
                        blackBishopBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 5:
                        blackQueenBoard &= ~(1L << Move.getTo(move));
                        break;

                }
            }
        }
        else{
            blackRookBoard &= ~(1L << Move.getFrom(move));
            blackRookBoard |= 1L << Move.getTo(move);
            if(Move.isCapture(move)){
                int capturedPiece = Move.getCapture(move);
                switch(capturedPiece){
                    case 1:
                        whitePawnBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 2:
                        whiteKnightBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 3:
                        whiteRookBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 4:
                        whiteBishopBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 5:
                        whiteQueenBoard &= ~(1L << Move.getTo(move));
                        break;

                }
            }
        }
    }
    private void moveBishop(int move){
        if(isWhiteTurn){
            whiteBishopBoard &= ~(1L << Move.getFrom(move));
            whiteBishopBoard |= 1L << Move.getTo(move);
            if(Move.isCapture(move)){
                int capturedPiece = Move.getCapture(move);
                switch(capturedPiece){
                    case 1:
                        blackPawnBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 2:
                        blackKnightBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 3:
                        blackRookBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 4:
                        blackBishopBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 5:
                        blackQueenBoard &= ~(1L << Move.getTo(move));
                        break;

                }
            }
        }
        else{
            blackBishopBoard &= ~(1L << Move.getFrom(move));
            blackBishopBoard |= 1L << Move.getTo(move);
            if(Move.isCapture(move)){
                int capturedPiece = Move.getCapture(move);
                switch(capturedPiece){
                    case 1:
                        whitePawnBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 2:
                        whiteKnightBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 3:
                        whiteRookBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 4:
                        whiteBishopBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 5:
                        whiteQueenBoard &= ~(1L << Move.getTo(move));
                        break;

                }
            }
        }
    }

    private void moveQueen(int move){
        if(isWhiteTurn){
            whiteQueenBoard &= ~(1L << Move.getFrom(move));
            whiteQueenBoard |= 1L << Move.getTo(move);
            if(Move.isCapture(move)){
                int capturedPiece = Move.getCapture(move);
                switch(capturedPiece){
                    case 1:
                        blackPawnBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 2:
                        blackKnightBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 3:
                        blackRookBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 4:
                        blackBishopBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 5:
                        blackQueenBoard &= ~(1L << Move.getTo(move));
                        break;

                }
            }
        }
        else{
            blackQueenBoard &= ~(1L << Move.getFrom(move));
            blackQueenBoard |= 1L << Move.getTo(move);
            if(Move.isCapture(move)){
                int capturedPiece = Move.getCapture(move);
                switch(capturedPiece){
                    case 1:
                        whitePawnBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 2:
                        whiteKnightBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 3:
                        whiteRookBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 4:
                        whiteBishopBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 5:
                        whiteQueenBoard &= ~(1L << Move.getTo(move));
                        break;

                }
            }
        }
    }


    private void moveKing(int move){
        if(isWhiteTurn){
            whiteKingBoard &= ~(1L << Move.getFrom(move));
            whiteKingBoard |= 1L << Move.getTo(move);
            if(Move.isCapture(move)){
                int capturedPiece = Move.getCapture(move);
                switch(capturedPiece){
                    case 1:
                        blackPawnBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 2:
                        blackKnightBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 3:
                        blackRookBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 4:
                        blackBishopBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 5:
                        blackQueenBoard &= ~(1L << Move.getTo(move));
                        break;

                }
            }
        }
        else{
            blackKingBoard &= ~(1L << Move.getFrom(move));
            blackKingBoard |= 1L << Move.getTo(move);
            if(Move.isCapture(move)){
                int capturedPiece = Move.getCapture(move);
                switch(capturedPiece){
                    case 1:
                        whitePawnBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 2:
                        whiteKnightBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 3:
                        whiteRookBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 4:
                        whiteBishopBoard &= ~(1L << Move.getTo(move));
                        break;

                    case 5:
                        whiteQueenBoard &= ~(1L << Move.getTo(move));
                        break;

                }
            }
        }
    }

}
