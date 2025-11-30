package chess.bitboard;

import chess.move.MoveList;

public class MoveGenerator {

    private final long[] knightMoves = new long[64];

    private void precomputeKnightMoves(){
        long FileA = 0x8080808080808080L;
        long FileB = 0x4040404040404040L;
        long FileG = 0x0202020202020202L;
        long FileH = 0x0101010101010101L;

        for(int i = 0; i < 64; i++){
            long knight = 1L << i;

            //knight is not on file A
            if((knight & FileA) == 0) knightMoves[i] |= (knight << 17) | (knight >> 15);
            //knight is not on fileA and fileB
            if((knight & (FileA | FileB)) == 0) knightMoves[i] |= (knight << 10) | (knight >> 6);

            //knight in not on fileH
            if((knight & FileH) == 0) knightMoves[i] |= (knight << 15) | (knight >> 17);
            //knight in not on fileH and fileG
            if((knight & (FileG | FileH)) == 0) knightMoves[i] |= (knight << 6) | (knight >> 10);
        }


    }

    public MoveGenerator(){
        precomputeKnightMoves();
    }

    public static MoveList generateMoves(BitBoard board){
        MoveList moves = new MoveList();
        generatePawnMoves(board, moves);
        generateRookMoves(board, moves);
        generateKnightMoves(board, moves);
        generateBishopMoves(board, moves);
        generateQueenMoves(board, moves);
        generateKingMoves(board, moves);
        return moves;
    }

    private static void generatePawnMoves(BitBoard board, MoveList moves){

    }

    private static void generateRookMoves(BitBoard board, MoveList moves){

    }

    private static void generateKnightMoves(BitBoard board, MoveList moves){

    }

    private static void generateBishopMoves(BitBoard board, MoveList moves){

    }

    private static void generateQueenMoves(BitBoard board, MoveList moves){

    }

    private static void generateKingMoves(BitBoard board, MoveList moves){

    }



    private static void generateSlidingMoves(BitBoard board){

    }
}

