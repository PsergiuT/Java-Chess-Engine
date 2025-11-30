package chess.move;

public class MoveList {
    private int[] moves = new int[256];
    private int size = 0;

    public void addMove(int move){
        moves[size++] = move;
    }

    public void clear(){
        size = 0;
    }

    public int[] getMoves(){
        return moves;
    }
}
