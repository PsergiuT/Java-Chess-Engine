package Search.BotV1;

import Board.BitBoard;
import MoveGenerator.MoveGenerator;
import Move.MoveList;
import java.util.Random;

public class Bot {
    public static int bestMove(BitBoard board) {
        Random random = new Random();
        MoveList generated = MoveGenerator.generateMoves(board, board.isWhiteTurn);
        int length = generated.getSize();
        if(length == 0) return -1; //checkmate
        return generated.getMoves()[random.nextInt(length)];
    }
}
