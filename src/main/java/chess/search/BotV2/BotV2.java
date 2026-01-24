package chess.search.BotV2;

import chess.bitboard.BitBoard;
import chess.bitboard.MoveGenerator;
import chess.move.MoveList;
import lombok.Getter;

public class BotV2 {
    @Getter
    private static int bestMove = 0;

    public static int bestMove(BitBoard board, int depth, int initialDepth){

        if(depth == 0){
            return Evaluation.evaluate(board);
        }

        boolean isMaximizing = board.isWhiteTurn;
        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        MoveList moves = MoveGenerator.generateMoves(board, board.isWhiteTurn);

        for(int i = 0; i < moves.getSize(); i++){
            board.makeMove(moves.getMoves()[i]);

            int evaluation = BotV2.bestMove(board, depth - 1, initialDepth);


            if(isMaximizing){
                if(evaluation > bestScore){
                    bestScore = evaluation;
                    if (depth == initialDepth) bestMove = moves.getMoves()[i];
                }
            }else{
                if(evaluation < bestScore) {
                    bestScore = evaluation;
                    if (depth == initialDepth) bestMove = moves.getMoves()[i];
                }
            }

            board.undoMove(moves.getMoves()[i]);
        }

        return bestScore;
    }
}
