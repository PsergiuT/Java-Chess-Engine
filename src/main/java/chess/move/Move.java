package chess.move;

public class Move {

    public static int encode(int from, int to, int piece, int capture, int isCapture, int isPromotion, int isCastling, int isEnPassant, int isCheckMate, int isCheck){
        return  isCheck         & 0x1 << 25 |
                isCheckMate     & 0x1 << 24 |
                isEnPassant     & 0x1 << 23 |
                isCastling      & 0x1 << 22 |
                isPromotion     & 0x1 << 21 |
                isCapture       & 0x1 << 20 |
                capture         & 0xF << 16 |
                piece           & 0xF << 12 |
                to              & 0x3F << 6 |
                from            & 0x3F;
    }

    public static int getPiece(int move){
        return move >> 12 & 0xF;
    }

    public static int getFrom(int move){
        return move & 0x3F;
    }

    public static int getTo(int move){
        return move >> 6 & 0x3F;
    }

    public static int getCapture(int move){
        return move >> 16 & 0xF;
    }

    public static boolean isCapture(int move){
        return (move >> 20 & 0x1) == 1;
    }

    public static boolean isPromotion(int move){
        return (move >> 21 & 0x1) == 1;
    }

    public static boolean isCastling(int move){
        return (move >> 22 & 0x1) == 1;
    }

    public static boolean isEnPassant(int move){
        return (move >> 23 & 0x1) == 1;
    }

    public static boolean isCheckMate(int move){
        return (move >> 24 & 0x1) == 1;
    }

    public static boolean isCheck(int move){
        return (move >> 25 & 0x1) == 1;
    }

//    public static boolean isStaleMate(int move){
//        return false;
//    }

//    public static boolean isDraw(int move){
//        return false;
//    }

//    public static int getPromotion(int move){
//        return -1;
//    }


}
