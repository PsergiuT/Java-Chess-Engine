package MoveGenerator;

public class PrecomputeMoves {
    public final static long[] knightMoves = new long[64];
    public final static long[] kingMoves = new long[64];
    public final static long[][] pawnMoves = new long[4][64]; //0 is white, 2 is black, 1 is white captures, 3 is black captures
    public final static long[] queenMoves = new long[64];
    public final static long[] bishopMoves = new long[64];
    public final static long[] rookMoves = new long[64];

    private final static long FileA = 0x8080808080808080L;
    private final static long FileB = 0x4040404040404040L;
    private final static long FileG = 0x0202020202020202L;
    private final static long FileH = 0x0101010101010101L;
    private final static long Rank1 = 0x00000000000000FFL;
    private final static long Rank8 = 0xFF00000000000000L;


    public final static long[][] rayMovement = new long[64][64];
    public final static long[][] precomputedDirections = new long[8][64]; //0, 1, 2, 3 are for Orthogonal directions, 4, 5, 6, 7 are for Diagonal directions


    private static void precomputeKnightMoves(){
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

    private static void precomputeKingMoves(){
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

    // also calculates pawn attacks
    private static void precomputePawnMoves(){
        //__________WHITE____PAWNS__________//

        //add captures for the first row for king check mask
        for(int i = 0; i < 8; i++){
            long pawn = 1L << i;

            //pawn is not on file A
            if((pawn & FileA) == 0) pawnMoves[1][i] |= (pawn << 9);
            //pawn in not on fileH
            if((pawn & FileH) == 0) pawnMoves[1][i] |= (pawn << 7);

        }
        //add the possibility to move 1 or 2 up
        for(int i = 8; i < 16; i++){
            long pawn = 1L << i;

            pawnMoves[0][i] |= (pawn << 8) | (pawn << 16);
            //pawn is not on file A
            if((pawn & FileA) == 0) pawnMoves[1][i] |= (pawn << 9);
            //pawn in not on fileH
            if((pawn & FileH) == 0) pawnMoves[1][i] |= (pawn << 7);
        }
        //move one up
        for(int i = 16; i < 56; i++){
            long pawn = 1L << i;

            pawnMoves[0][i] |= (pawn << 8);
            //pawn is not on file A
            if((pawn & FileA) == 0) pawnMoves[1][i] |= (pawn << 9);
            //pawn in not on fileH
            if((pawn & FileH) == 0) pawnMoves[1][i] |= (pawn << 7);

        }

        //__________BLACK____PAWNS__________//

        //add captures for the first row for king check mask
        for(int i = 56; i < 64; i++){
            long pawn = 1L << i;

            //pawn is not on file A
            if((pawn & FileA) == 0) pawnMoves[3][i] |= (pawn >>> 7);
            //pawn in not on fileH
            if((pawn & FileH) == 0) pawnMoves[3][i] |= (pawn >>> 9);
        }
        //add the possibility to move 1 or 2 down
        for(int i = 48; i < 56; i++){
            long pawn = 1L << i;

            pawnMoves[2][i] |= (pawn >>> 8) | (pawn >>> 16);
            //pawn is not on file A
            if((pawn & FileA) == 0) pawnMoves[3][i] |= (pawn >>> 7);
            //pawn in not on fileH
            if((pawn & FileH) == 0) pawnMoves[3][i] |= (pawn >>> 9);
        }
        //move one up
        for(int i = 8; i < 48; i++){
            long pawn = 1L << i;

            pawnMoves[2][i] |= (pawn >>> 8);
            //pawn is not on file A
            if((pawn & FileA) == 0) pawnMoves[3][i] |= (pawn >>> 7);
            //pawn in not on fileH
            if((pawn & FileH) == 0) pawnMoves[3][i] |= (pawn >>> 9);

        }
    }


    private static void precomputeSlidingMoves(){
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

            //go north-west (4)
            if((piece & (FileA | Rank8)) == 0) {
                while (((piece << index * 9) & (FileA | Rank8)) == 0) {
                    precomputedDirections[4][i] |= piece << index * 9;
                    index++;
                }
                precomputedDirections[4][i] |= piece << index * 9;
                index = 1;
            }
            //go north-east (5)
            if((piece & (FileH | Rank8)) == 0){
                while (((piece << index * 7) & (FileH | Rank8)) == 0){
                    precomputedDirections[5][i] |= piece << index * 7;
                    index ++;
                }
                precomputedDirections[5][i] |= piece << index * 7;
                index = 1;
            }
            //go south-east (6)
            if((piece & (FileH | Rank1)) == 0){
                while (((piece >>> index * 9) & (FileH | Rank1)) == 0){
                    precomputedDirections[6][i] |= piece >>> index * 9;
                    index ++;
                }
                precomputedDirections[6][i] |= piece >>> index * 9;
                index = 1;
            }
            //go south-west (7)
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


    private static void precomputeRayMovement(){
        int index = 1;

        for(int i = 0; i < 64; i++){
            long piece = 1L << i;
            //generate Orthogonal directions ----------------------------------------------------------

            //go west (0)
            if((piece & FileA) == 0){
                while (((piece << index) & FileA) == 0){
                    if(index - 2 < 0){
                        index ++;
                        continue;
                    }
                    rayMovement[i][Long.numberOfTrailingZeros(piece << index)] |= (rayMovement[i][Long.numberOfTrailingZeros(piece << index - 1)]) | (piece << index - 1);
                    index ++;
                }
                rayMovement[i][Long.numberOfTrailingZeros(piece << index)] |= (rayMovement[i][Long.numberOfTrailingZeros(piece << index - 1)]) | (piece << index - 1);
                index = 1;
            }
            //go north (1)
            if((piece & Rank8) == 0){
                while (((piece << index * 8) & Rank8) == 0){
                    if(index - 2 < 0){
                        index ++;
                        continue;
                    }
                    rayMovement[i][Long.numberOfTrailingZeros(piece << index * 8)] |= (rayMovement[i][Long.numberOfTrailingZeros(piece << (index - 1) * 8)]) | (piece << (index - 1) * 8);
                    index ++;
                }
                rayMovement[i][Long.numberOfTrailingZeros(piece << index * 8)] |= (rayMovement[i][Long.numberOfTrailingZeros(piece << (index - 1) * 8)]) | (piece << (index - 1) * 8);
                index = 1;
            }
            //go east (2)
            if((piece & FileH) == 0){
                while (((piece >>> index) & FileH) == 0){
                    if(index - 2 < 0){
                        index ++;
                        continue;
                    }
                    rayMovement[i][Long.numberOfTrailingZeros(piece >>> index)] |= (rayMovement[i][Long.numberOfTrailingZeros(piece >>> index - 1)]) | (piece >>> (index - 1));
                    index ++;
                }
                rayMovement[i][Long.numberOfTrailingZeros(piece >>> index)] |= (rayMovement[i][Long.numberOfTrailingZeros(piece >>> index - 1)]) | (piece >>> (index - 1));
                index = 1;
            }
            //go south (3)
            if((piece & Rank1) == 0){
                while (((piece >>> index * 8) & Rank1) == 0){
                    if(index - 2 < 0){
                        index ++;
                        continue;
                    }
                    rayMovement[i][Long.numberOfTrailingZeros(piece >>> index * 8)] |= (rayMovement[i][Long.numberOfTrailingZeros(piece >>> (index - 1) * 8)]) | (piece >>> (index - 1) * 8);
                    index ++;
                }
                rayMovement[i][Long.numberOfTrailingZeros(piece >>> index * 8)] |= (rayMovement[i][Long.numberOfTrailingZeros(piece >>> (index - 1) * 8)]) | (piece >>> (index - 1) * 8);
                index = 1;
            }



            //generate Diagonal directions  -----------------------------------------------------------------

            //go north-west (4)
            if((piece & (FileA | Rank8)) == 0){
                while (((piece << index * 9) & (FileA | Rank8)) == 0){
                    if(index - 2 < 0){
                        index ++;
                        continue;
                    }
                    rayMovement[i][Long.numberOfTrailingZeros(piece << index * 9)] |= (rayMovement[i][Long.numberOfTrailingZeros(piece << (index - 1) * 9)]) | (piece << (index - 1) * 9);
                    index ++;
                }
                rayMovement[i][Long.numberOfTrailingZeros(piece << index * 9)] |= (rayMovement[i][Long.numberOfTrailingZeros(piece << (index - 1) * 9)]) | (piece << (index - 1) * 9);
                index = 1;
            }
            //go north-east (5)
            if((piece & (FileH | Rank8)) == 0){
                while (((piece << index * 7) & (FileH | Rank8)) == 0){
                    if(index - 2 < 0){
                        index ++;
                        continue;
                    }
                    rayMovement[i][Long.numberOfTrailingZeros(piece << index * 7)] |= (rayMovement[i][Long.numberOfTrailingZeros(piece << (index - 1) * 7)]) | (piece << (index - 1) * 7);
                    index ++;
                }
                rayMovement[i][Long.numberOfTrailingZeros(piece << index * 7)] |= (rayMovement[i][Long.numberOfTrailingZeros(piece << (index - 1) * 7)]) | (piece << (index - 1) * 7);
                index = 1;
            }
            //go south-east (6)
            if((piece & (FileH | Rank1)) == 0){
                while (((piece >>> index * 9) & (FileH | Rank1)) == 0){
                    if(index - 2 < 0){
                        index ++;
                        continue;
                    }
                    rayMovement[i][Long.numberOfTrailingZeros(piece >>> index * 9)] |= (rayMovement[i][Long.numberOfTrailingZeros(piece >>> (index - 1) * 9)]) | (piece >>> (index - 1) * 9);
                    index ++;
                }
                rayMovement[i][Long.numberOfTrailingZeros(piece >>> index * 9)] |= (rayMovement[i][Long.numberOfTrailingZeros(piece >>> (index - 1) * 9)]) | (piece >>> (index - 1) * 9);
                index = 1;
            }
            //go south-west (7)
            if((piece & (FileA | Rank1)) == 0){
                while (((piece >>> index * 7) & (FileA | Rank1)) == 0){
                    if(index - 2 < 0){
                        index ++;
                        continue;
                    }
                    rayMovement[i][Long.numberOfTrailingZeros(piece >>> index * 7)] |= (rayMovement[i][Long.numberOfTrailingZeros(piece >>> (index - 1) * 7)]) | (piece >>> (index - 1) * 7);
                    index ++;
                }
                rayMovement[i][Long.numberOfTrailingZeros(piece >>> index * 7)] |= (rayMovement[i][Long.numberOfTrailingZeros(piece >>> (index - 1) * 7)]) | (piece >>> (index - 1) * 7);
                index = 1;
            }
        }
    }

    // the static initializer enables the precomputation of all moves
    // this block runs exactly once, when the class if first loaded in memory
    static{
        precomputeKnightMoves();
        precomputeKingMoves();
        precomputePawnMoves();
        precomputeSlidingMoves();
        precomputeRayMovement();

        for(int i = 0; i < 64; i++)
        {
            rookMoves[i] = precomputedDirections[0][i] | precomputedDirections[1][i] | precomputedDirections[2][i] | precomputedDirections[3][i];
            bishopMoves[i] = precomputedDirections[4][i] | precomputedDirections[5][i] | precomputedDirections[6][i] | precomputedDirections[7][i];
            queenMoves[i] = rookMoves[i] | bishopMoves[i];
        }
    }
}
