package chess.bitboard;

import chess.move.MoveList;

public class MoveGenerator {

    public final long[] knightMoves = new long[64];
    public final long[] kingMoves = new long[64];
    public final long[] whitePawnMoves = new long[64];
    public final long[] blackPawnMoves = new long[64];
    public final long[] queenMoves = new long[64];
    public final long[] bishopMoves = new long[64];
    public final long[] rookMoves = new long[64];
    private final long[][] precomputedDirections = new long[8][64]; //0, 1, 2, 3 are for Orthogonal directions, 4, 5, 6, 7 are for Diagonal directions

    private boolean isWhite;

    private void precomputeKnightMoves(){
        long FileA = 0x8080808080808080L;
        long FileB = 0x4040404040404040L;
        long FileG = 0x0202020202020202L;
        long FileH = 0x0101010101010101L;

        for(int i = 0; i < 64; i++){
            long knight = 1L << i;

            //knight is not on file A
            if((knight & FileA) == 0) knightMoves[i] |= (knight << 17) | (knight >>> 15);
            //knight is not on fileA and fileB
            if((knight & (FileA | FileB)) == 0) knightMoves[i] |= (knight << 10) | (knight >>> 6);

            //knight in not on fileH
            if((knight & FileH) == 0) knightMoves[i] |= (knight << 15) | (knight >>> 17);
            //knight in not on fileH and fileG
            if((knight & (FileG | FileH)) == 0) knightMoves[i] |= (knight << 6) | (knight >>> 10);
        }
    }

    private void precomputeKingMoves(){
        long FileA = 0x8080808080808080L;
        long FileH = 0x0101010101010101L;

        for(int i = 0; i < 64; i++){
            long king = 1L << i;

            //always can go up or down
            kingMoves[i] |= (king << 8) | (king >>> 8);

            //king is not on file A
            if((king & FileA) == 0) kingMoves[i] |= (king << 9) | (king << 1) | (king >>> 7);

            //king in not on fileH
            if((king & FileH) == 0) kingMoves[i] |= (king << 7) | (king >>> 1) | (king >>> 9);

        }
    }

    private void precomputeWhitePawnMoves(){
        long FileA = 0x8080808080808080L;
        long FileH = 0x0101010101010101L;

        for(int i = 8; i < 16; i++){
            long pawn = 1L << i;

            //add possibility to move 1 or 2 up
            whitePawnMoves[i] |= (pawn << 8) | (pawn << 16);

            //pawn is not on file A
            if((pawn & FileA) == 0) whitePawnMoves[i] |= (pawn << 9);

            //pawn in not on fileH
            if((pawn & FileH) == 0) whitePawnMoves[i] |= (pawn << 7);

        }

        for(int i = 16; i < 55; i++){
            long pawn = 1L << i;

            //move one up
            whitePawnMoves[i] |= (pawn << 8);

            //pawn is not on file A
            if((pawn & FileA) == 0) whitePawnMoves[i] |= (pawn << 9);

            //pawn in not on fileH
            if((pawn & FileH) == 0) whitePawnMoves[i] |= (pawn << 7);

        }

        //last row is promoted
    }

    private void precomputeBlackPawnMoves(){
        long FileA = 0x8080808080808080L;
        long FileH = 0x0101010101010101L;

        for(int i = 48; i < 55; i++){
            long pawn = 1L << i;

            //add possibility to move 1 or 2 down
            blackPawnMoves[i] |= (pawn >>> 8) | (pawn >>> 16);

            //pawn is not on file A
            if((pawn & FileA) == 0) blackPawnMoves[i] |= (pawn >>> 7);

            //pawn in not on fileH
            if((pawn & FileH) == 0) blackPawnMoves[i] |= (pawn >>> 9);

        }

        for(int i = 8; i < 47; i++){
            long pawn = 1L << i;

            //move one up
            blackPawnMoves[i] |= (pawn >>> 8);

            //pawn is not on file A
            if((pawn & FileA) == 0) blackPawnMoves[i] |= (pawn >>> 7);

            //pawn in not on fileH
            if((pawn & FileH) == 0) blackPawnMoves[i] |= (pawn >>> 9);

        }

        //last row is promoted
    }

    private void precomputeSlidingMoves(){
        long FileA = 0x8080808080808080L;
        long FileH = 0x0101010101010101L;
        long Rank1 = 0x00000000000000FFL;
        long Rank8 = 0xFF00000000000000L;

        int index = 1;

        for(int i = 0; i < 64; i++){
            long piece = 1L << i;
            //generate Orthogonal directions ----------------------------------------------------------

            //go west (0)
            if((piece & FileA) == 0){
                while (((piece << index) & FileA) == 0){
                    precomputedDirections[0][i] |= piece << index;
                    index ++;
                }
                precomputedDirections[0][i] |= piece << index;
                index = 1;
            }

            //go north (1)
            if((piece & Rank8) == 0){
                while (((piece << index * 8) & Rank8) == 0){
                    precomputedDirections[1][i] |= piece << index * 8;
                    index ++;
                }
                precomputedDirections[1][i] |= piece << index * 8;
                index = 1;
            }


            //go east (2)
            if((piece & FileH) == 0){
                while (((piece >>> index) & FileH) == 0){
                    precomputedDirections[2][i] |= piece >>> index;
                    index ++;
                }
                precomputedDirections[2][i] |= piece >>> index;
                index = 1;
            }


            //go south (3)
            if((piece & Rank1) == 0){
                while (((piece >>> index * 8) & Rank1) == 0){
                    precomputedDirections[3][i] |= piece >>> index * 8;
                    index ++;
                }
                precomputedDirections[3][i] |= piece >>> index * 8;
                index = 1;
            }



            //generate Diagonal directions  -----------------------------------------------------------------

            //go north west (4)
            if((piece & (FileA | Rank8)) == 0){
                while (((piece << index * 9) & (FileA | Rank8)) == 0){
                    precomputedDirections[4][i] |= piece << index * 9;
                    index ++;
                }
                precomputedDirections[4][i] |= piece << index * 9;
                index = 1;
            }


            //go north east (5)
            if((piece & (FileH | Rank8)) == 0){
                while (((piece << index * 7) & (FileH | Rank8)) == 0){
                    precomputedDirections[5][i] |= piece << index * 7;
                    index ++;
                }
                precomputedDirections[5][i] |= piece << index * 7;
                index = 1;
            }


            //go south east (6)
            if((piece & (FileH | Rank1)) == 0){
                while (((piece >>> index * 9) & (FileH | Rank1)) == 0){
                    precomputedDirections[6][i] |= piece >>> index * 9;
                    index ++;
                }
                precomputedDirections[6][i] |= piece >>> index * 9;
                index = 1;
            }


            //go south west (7)
            if((piece & (FileA | Rank1)) == 0){
                while (((piece >>> index * 7) & (FileA | Rank1)) == 0){
                    precomputedDirections[7][i] |= piece >>> index * 7;
                    index ++;
                }
                precomputedDirections[7][i] |= piece >>> index * 7;
                index = 1;
            }

        }


    }



    public MoveGenerator(){
        precomputeKnightMoves();
        precomputeKingMoves();
        precomputeWhitePawnMoves();
        precomputeBlackPawnMoves();
        precomputeSlidingMoves();
        for(int i = 0; i < 64; i++)
        {
            rookMoves[i] = precomputedDirections[0][i] | precomputedDirections[1][i] | precomputedDirections[2][i] | precomputedDirections[3][i];
            bishopMoves[i] = precomputedDirections[4][i] | precomputedDirections[5][i] | precomputedDirections[6][i] | precomputedDirections[7][i];
            queenMoves[i] = rookMoves[i] | bishopMoves[i];
        }

        //bishop
        //queen                 //all need magic bitboards or sliding moves generation
        //rook
    }

    public MoveList generateMoves(BitBoard board, boolean isWhite){
        this.isWhite = isWhite;
        MoveList moves = new MoveList();
        generatePawnMoves(board, moves);
        generateRookMoves(board, moves);
        generateKnightMoves(board, moves);
        generateBishopMoves(board, moves);
        generateQueenMoves(board, moves);
        generateKingMoves(board, moves);
        return moves;
    }

    private void generatePawnMoves(BitBoard board, MoveList moves){

    }

    private void generateRookMoves(BitBoard board, MoveList moves){

    }

    private void generateKnightMoves(BitBoard board, MoveList moves){
        //find all knights on the board for the given color
        //see if ally pieces block any of the pregenerated moves or if any of the enemy pieces result in a capture move
        //construct the moves (from, to ,
    }

    private void generateBishopMoves(BitBoard board, MoveList moves){

    }

    private void generateQueenMoves(BitBoard board, MoveList moves){

    }

    private void generateKingMoves(BitBoard board, MoveList moves){

    }



    private void generateSlidingMoves(BitBoard board){

    }
}

