package chess.move;

public class Move {

    public static int encode(int from, int to, int piece, int promotion, int capture, int flags){
        return -1;
    }

    public static int getPiece(int move){
        return -1;
    }

    public static int getFrom(int move){
        return -1;
    }

    public static int getTo(int move){
        return -1;
    }

    public static int getPromotion(int move){
        return -1;
    }

    public static int getFlags(int move){
        return -1;
    }

    public static int getCapture(int move){
        return -1;
    }

    public static boolean isCapture(int move){
        return false;
    }

    public static boolean isPromotion(int move){
        return false;
    }

    public static boolean isCastling(int move){
        return false;
    }

    public static boolean isEnPassant(int move){
        return false;
    }

    public static boolean isCheck(int move){
        return false;
    }

    public static boolean isCheckMate(int move){
        return false;
    }

//    public static boolean isStaleMate(int move){
//        return false;
//    }

    public static boolean isDraw(int move){
        return false;
    }


}
