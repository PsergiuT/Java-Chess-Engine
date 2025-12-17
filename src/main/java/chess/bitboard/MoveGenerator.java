package chess.bitboard;

import chess.move.Move;
import chess.move.MoveList;

public class MoveGenerator {

    public final long[] knightMoves = new long[64];
    public final long[] kingMoves = new long[64];
    public final long[][] pawnMoves = new long[4][64];
    public final long[] queenMoves = new long[64];
    public final long[] bishopMoves = new long[64];
    public final long[] rookMoves = new long[64];

    public final long[] pinnedPieces = new long[64];
    public final long[][] rayMovement = new long[64][64];
    public long pinnedMaskBoard;
    public long checkMask = 0xFFFFFFFFFFFFFFFFL;
    private final long[][] precomputedDirections = new long[8][64]; //0, 1, 2, 3 are for Orthogonal directions, 4, 5, 6, 7 are for Diagonal directions

    private boolean isWhite;



    //-----------------------------------------------------------------------------------------------------------------------
    //                                  Precomputed boards ( ALL TESTED )
    //-----------------------------------------------------------------------------------------------------------------------


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

    private void precomputePawnMoves(){
        long FileA = 0x8080808080808080L;
        long FileH = 0x0101010101010101L;

        //__________WHITE____PAWNS__________//

        //add captures for first row for king check mask
        for(int i = 0; i < 8; i++){
            long pawn = 1L << i;

            //pawn is not on file A
            if((pawn & FileA) == 0) pawnMoves[1][i] |= (pawn << 9);

            //pawn in not on fileH
            if((pawn & FileH) == 0) pawnMoves[1][i] |= (pawn << 7);

        }


        for(int i = 8; i < 16; i++){
            long pawn = 1L << i;

            //add possibility to move 1 or 2 up
            pawnMoves[0][i] |= (pawn << 8) | (pawn << 16);

            //pawn is not on file A
            if((pawn & FileA) == 0) pawnMoves[1][i] |= (pawn << 9);

            //pawn in not on fileH
            if((pawn & FileH) == 0) pawnMoves[1][i] |= (pawn << 7);

        }

        for(int i = 16; i < 56; i++){
            long pawn = 1L << i;

            //move one up
            pawnMoves[0][i] |= (pawn << 8);

            //pawn is not on file A
            if((pawn & FileA) == 0) pawnMoves[1][i] |= (pawn << 9);

            //pawn in not on fileH
            if((pawn & FileH) == 0) pawnMoves[1][i] |= (pawn << 7);

        }

        //last row is promoted

        //__________BLACK____PAWNS__________//


        for(int i = 56; i < 64; i++){
            long pawn = 1L << i;

            //pawn is not on file A
            if((pawn & FileA) == 0) pawnMoves[3][i] |= (pawn >>> 7);

            //pawn in not on fileH
            if((pawn & FileH) == 0) pawnMoves[3][i] |= (pawn >>> 9);

        }

        for(int i = 48; i < 56; i++){
            long pawn = 1L << i;

            //add possibility to move 1 or 2 down
            pawnMoves[2][i] |= (pawn >>> 8) | (pawn >>> 16);

            //pawn is not on file A
            if((pawn & FileA) == 0) pawnMoves[3][i] |= (pawn >>> 7);

            //pawn in not on fileH
            if((pawn & FileH) == 0) pawnMoves[3][i] |= (pawn >>> 9);

        }

        for(int i = 8; i < 48; i++){
            long pawn = 1L << i;

            //move one up
            pawnMoves[2][i] |= (pawn >>> 8);

            //pawn is not on file A
            if((pawn & FileA) == 0) pawnMoves[3][i] |= (pawn >>> 7);

            //pawn in not on fileH
            if((pawn & FileH) == 0) pawnMoves[3][i] |= (pawn >>> 9);

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


    private void precomputeRayMovement(){

        //TODO NEEDS REFACTORING ASAP :))
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

            //go north west (4)
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


            //go north east (5)
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


            //go south east (6)
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


            //go south west (7)
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



    public MoveGenerator(){
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

        //bishop
        //queen                 //all need magic bitboards or sliding moves generation
        //rook
    }






    //-----------------------------------------------------------------------------------------------------------------------
    //                                  Pinned and Check Mask calculation + isCheck()
    //-----------------------------------------------------------------------------------------------------------------------





    private void calculatePinnedMask(BitBoard board){
        long orthogonalSlidingAttacks;
        long diagonalSlidingAttacks;
        long enemyPawn, enemyRook, enemyBishop, enemyQueen, allyPawn;
        int kingIndex;
        long allyPieces;
        long enemyPieces;
        if(isWhite){
            kingIndex = Long.numberOfTrailingZeros(board.getWhiteKingBoard());
            enemyRook = board.getBlackRookBoard();
            enemyBishop = board.getBlackBishopBoard();
            enemyQueen = board.getBlackQueenBoard();
            allyPieces = board.getWhitePieces();
            enemyPieces = board.getBlackPieces();
            allyPawn = board.getWhitePawnBoard();
            enemyPawn = board.getBlackPawnBoard();
        }
        else {
            kingIndex = Long.numberOfTrailingZeros(board.getBlackKingBoard());
            enemyRook = board.getWhiteRookBoard();
            enemyBishop = board.getWhiteBishopBoard();
            enemyQueen = board.getWhiteQueenBoard();
            allyPieces = board.getBlackPieces();
            enemyPieces = board.getWhitePieces();
            allyPawn = board.getBlackPawnBoard();
            enemyPawn = board.getWhitePawnBoard();
        }

        orthogonalSlidingAttacks = (enemyRook | enemyQueen) & rookMoves[kingIndex];
        diagonalSlidingAttacks = (enemyBishop | enemyQueen) & bishopMoves[kingIndex];

        // enemy pawn, knight and king can't pin

        this.pinnedMaskBoard = 0;
        while(orthogonalSlidingAttacks != 0){
            int enemy = Long.numberOfTrailingZeros(orthogonalSlidingAttacks);
            long ray = rayMovement[kingIndex][enemy];
            orthogonalSlidingAttacks &= (orthogonalSlidingAttacks - 1);

            long allyHits = ray & allyPieces;                                           //all the ally pieces that are in the ray
            long enemyHits = ray & enemyPieces;                                         //all the enemy pieces that are in the ray

            if(Long.bitCount(allyHits) == 1 && enemyHits == 0){
                int allyPinnedPiece = Long.numberOfTrailingZeros(allyHits);
                pinnedPieces[allyPinnedPiece] = (ray | (1L << enemy));                                    //store the bitmap of the places where the pinned piece can go + enemy square
                pinnedMaskBoard |= allyHits;                                                              //store a map of all the ally pieces that are pinned
            }

            //check for the en passant case
            if(Long.bitCount(allyHits) == 1 && Long.bitCount(enemyHits) == 1){
                if((allyPawn & allyHits) != 0 && (enemyHits & enemyPawn) != 0){
                    int allyPinnedPiece = Long.numberOfTrailingZeros(allyHits);
                    //the only place where the pawn can't go is in the en passant square
                    long mask = 0xFFFFFFFEFFFFFFFFL;
                    long destination;
                    if(isWhite){
                        destination = ((1L << Long.numberOfTrailingZeros(enemyHits)) << 8);
                    }
                    else{
                        destination = ((1L << Long.numberOfTrailingZeros(enemyHits)) >> 8);
                    }

                    if((destination & enemyPieces) != 0){
                        //enemies to attack -> false en passant
                        continue;
                    }
                    mask ^= destination;
                    pinnedPieces[allyPinnedPiece] = mask;
                    pinnedMaskBoard |= allyHits;                                                              //store a map of all the ally pieces that are pinned
                }
            }
        }


        while(diagonalSlidingAttacks != 0){
            int enemy = Long.numberOfTrailingZeros(diagonalSlidingAttacks);
            long ray = rayMovement[kingIndex][enemy];
            diagonalSlidingAttacks &= (diagonalSlidingAttacks - 1);

            long allyHits = ray & allyPieces;                                           //all the ally pieces that are in the ray
            long enemyHits = ray & enemyPieces;                                         //all the enemy pieces that are in the ray
            if(Long.bitCount(allyHits) == 1 && enemyHits == 0){
                int allyPinnedPiece = Long.numberOfTrailingZeros(allyHits);
                pinnedPieces[allyPinnedPiece] = (ray | (1L << enemy));                                    //store the bitmap of the places where the pinned piece can go
                pinnedMaskBoard |= allyHits;                                                              //store a map of all the ally pieces that are pinned
            }
        }
    }





    private boolean calculateCheckMask(BitBoard board){
        //update the checkMask
        int kingIndex;
        long enemyPawn, enemyKnight, enemyRook, enemyBishop, enemyQueen;
        long orthogonalSlidingAttacks;
        long diagonalSlidingAttacks;
        long pawnBoard;
        long allPieces = board.getBlackPieces() | board.getWhitePieces();
        if(isWhite){
            kingIndex = Long.numberOfTrailingZeros(board.getWhiteKingBoard());
            pawnBoard = pawnMoves[1][kingIndex];
            System.out.println(pawnBoard);
            enemyPawn = board.getBlackPawnBoard();
            enemyKnight = board.getBlackKnightBoard();
            enemyRook = board.getBlackRookBoard();
            enemyBishop = board.getBlackBishopBoard();
            enemyQueen = board.getBlackQueenBoard();
        }
        else {
            kingIndex = Long.numberOfTrailingZeros(board.getBlackKingBoard());
            pawnBoard = pawnMoves[3][kingIndex];
            enemyPawn = board.getWhitePawnBoard();
            enemyKnight = board.getWhiteKnightBoard();
            enemyRook = board.getWhiteRookBoard();
            enemyBishop = board.getWhiteBishopBoard();
            enemyQueen = board.getWhiteQueenBoard();
        }

        orthogonalSlidingAttacks = (enemyRook | enemyQueen) & rookMoves[kingIndex];
        diagonalSlidingAttacks = (enemyBishop | enemyQueen) & bishopMoves[kingIndex];

        int checkers = 0;


        //----------calculate knight + pawn attacks----------
        long attackingMap = knightMoves[kingIndex] & enemyKnight;
        if(attackingMap != 0){
            checkers += Long.bitCount(attackingMap);
        }

        long attacking = pawnBoard & enemyPawn;
        if(attacking != 0){
            checkers ++;
            attackingMap |= attacking;
        }


        //----------calculate sliding moves----------

        while(orthogonalSlidingAttacks != 0){
            int enemy = Long.numberOfTrailingZeros(orthogonalSlidingAttacks);
            long ray = rayMovement[kingIndex][enemy];
            orthogonalSlidingAttacks &= (orthogonalSlidingAttacks - 1);

            //we don't want anything between the king and the sliding piece
            if((ray & allPieces) == 0){
                checkers ++;
                attackingMap |= (ray | (1L << enemy));
            }
        }


        while(diagonalSlidingAttacks != 0){
            int enemy = Long.numberOfTrailingZeros(diagonalSlidingAttacks);
            long ray = rayMovement[kingIndex][enemy];
            diagonalSlidingAttacks &= (diagonalSlidingAttacks - 1);

            //we don't want anything between the king and the sliding piece
            if((ray & allPieces) == 0){
                checkers ++;
                attackingMap |= (ray | (1L << enemy));
            }
        }


        if(checkers == 0){
            checkMask = 0XFFFFFFFFFFFFFFFFL;
            return true;
        }
        else if(checkers == 1){
            checkMask = attackingMap;
            return true;
        }

        //double check
        return false;
    }



    private boolean isSquareAttacked(BitBoard board, int square){
        long enemyPawn, enemyKnight, enemyRook, enemyBishop, enemyQueen, enemyKing;
        long pawnBoard;
        long allPieces = board.getBlackPieces() | board.getWhitePieces();
        long orthogonalSlidingAttacks;
        long diagonalSlidingAttacks;
        if(isWhite){
            pawnBoard = pawnMoves[1][square];
            enemyPawn = board.getBlackPawnBoard();
            enemyKnight = board.getBlackKnightBoard();
            enemyRook = board.getBlackRookBoard();
            enemyBishop = board.getBlackBishopBoard();
            enemyQueen = board.getBlackQueenBoard();
            enemyKing = board.getBlackKingBoard();
        }
        else {
            pawnBoard = pawnMoves[3][square];
            enemyPawn = board.getWhitePawnBoard();
            enemyKnight = board.getWhiteKnightBoard();
            enemyRook = board.getWhiteRookBoard();
            enemyBishop = board.getWhiteBishopBoard();
            enemyQueen = board.getWhiteQueenBoard();
            enemyKing = board.getWhiteKingBoard();
        }

        orthogonalSlidingAttacks = (enemyRook | enemyQueen) & rookMoves[square];
        diagonalSlidingAttacks = (enemyBishop | enemyQueen) & bishopMoves[square];


        if((knightMoves[square] & enemyKnight) != 0)
            return true;

        if((pawnBoard & enemyPawn) != 0)
            return true;

        if((kingMoves[square] & enemyKing) != 0)
            return true;



        while(orthogonalSlidingAttacks != 0){
            int enemy = Long.numberOfTrailingZeros(orthogonalSlidingAttacks);
            if((rayMovement[square][enemy] & allPieces) == 0)          //if hits == 0
                return true;

            orthogonalSlidingAttacks &= (orthogonalSlidingAttacks - 1);
        }


        while(diagonalSlidingAttacks != 0){
            int enemy = Long.numberOfTrailingZeros(diagonalSlidingAttacks);
            if((rayMovement[square][enemy] & allPieces) == 0){
                System.out.println("ok");
                return true;
            }

            diagonalSlidingAttacks &= (diagonalSlidingAttacks - 1);
        }

        return false;
    }





    //-----------------------------------------------------------------------------------------------------------------------
    //                                               Move generation
    //-----------------------------------------------------------------------------------------------------------------------


    private void printMask(long mask){
        System.out.println("--------------------------------------------");

        for(int i = 0; i < 8; i++){
            System.out.print("R" + (i + 1) + ": ");
            for(int j = 0; j < 8; j++){
                long piece = mask & 0x8000000000000000L;
                System.out.print( piece != 0 ? "X " : "_ ");
                mask = mask << 1;
            }
            System.out.println("");
        }

        System.out.println("--------------------------------------------");
    }

    private void printWithMessage(String message, long mask){
        System.out.println("-----------------------------");
        System.out.println(message);
        System.out.println("-----------------------------");
        printMask(mask);
    }




    public MoveList generateMoves(BitBoard board, boolean isWhite){
        this.isWhite = isWhite;
        MoveList moves = new MoveList();

        calculatePinnedMask(board);
        System.out.println("CHECK MASK: ");
        printMask(checkMask);
//        System.out.println("PINNED MASK: ");
//        printMask(pinnedMaskBoard);
        if(!calculateCheckMask(board)){
            generateKingMoves(board, moves);
            return moves;
        }

        generatePawnMoves(board, moves);
        generateRookMoves(board, moves);
        generateKnightMoves(board, moves);
        generateBishopMoves(board, moves);
        generateQueenMoves(board, moves);
        generateKingMoves(board, moves);
        return moves;
    }




    private void generatePawnMoves(BitBoard board, MoveList moves){
        long pawnBoard;
        int piece;
        long enemyPieces;
        long allPieces = board.getWhitePieces() | board.getBlackPieces();
        long Rank1 = 0x00000000000000FFL;
        long Rank8 = 0xFF00000000000000L;
        if(isWhite){
            piece = 0;
            pawnBoard = board.getWhitePawnBoard();
            enemyPieces = board.getBlackPieces();
        }
        else{
            piece = 8;
            pawnBoard = board.getBlackPawnBoard();
            enemyPieces = board.getWhitePieces();
        }



        while(pawnBoard != 0){
            int pawnIndex = Long.numberOfTrailingZeros(pawnBoard);
            pawnBoard &= (pawnBoard - 1);
            long pawnMap = (1L << pawnIndex);

            long pMapMoveAhead;
            long pMapCapture;

            if(isWhite) {
                pMapMoveAhead = pawnMoves[0][pawnIndex];
                pMapCapture = pawnMoves[1][pawnIndex];
            }
            else{
                pMapMoveAhead = pawnMoves[2][pawnIndex];
                pMapCapture = pawnMoves[3][pawnIndex];
            }

            if((pawnMap & pinnedMaskBoard) != 0){
                // if pawn pinned
                pMapMoveAhead &= pinnedPieces[pawnIndex];
                pMapCapture &= pinnedPieces[pawnIndex];
            }

            pMapMoveAhead &= checkMask;
            pMapCapture &= checkMask;

            long pMapEnPassant = pMapCapture;

            pMapMoveAhead &= ~(allPieces);             //move ahead pawns are blocked by all pieces
            pMapCapture &= enemyPieces;                 //capture pawns can only move if enemy pieces are in their way


            //only one en passant move can exist per move
            long enPassantMask = (1L << board.getEnPassantSquare());
            if(board.getEnPassantSquare() != -1 && (pMapEnPassant & enPassantMask) != 0) {
                // add the en passant move to the list
                moves.addMove(Move.encode(
                        pawnIndex,
                        board.getEnPassantSquare(),
                        piece,
                        isWhite ? 8 : 0,
                        1,
                        0,
                        0,
                        1,
                        0
                ));
            }


            ///_________CHECK__FOR__MOVE__AHEAD_________///


            while(pMapMoveAhead != 0){

                int to = Long.numberOfTrailingZeros(pMapMoveAhead);
                long pieceBoard = (1L << to);
                pMapMoveAhead &= (pMapMoveAhead - 1);


                //check for double move bug
                if(Math.abs(pawnIndex - to) == 16){
                    int indexInFrontOfPawn = isWhite ? pawnIndex + 8 : pawnIndex - 8;
                    if((allPieces & (1L << indexInFrontOfPawn)) != 0){
                        continue;
                    }
                }


                //check for promotion
                if((pieceBoard & Rank1) != 0 || (pieceBoard & Rank8) != 0){
                    int startIndex = isWhite ? 1 : 9; //starting with the knight
                    for(int i = startIndex; i < startIndex + 4; i++){
                        moves.addMove(Move.encode(
                                pawnIndex,
                                to,
                                piece,
                                0,
                                0,
                                1,
                                0,
                                0,
                                i
                        ));
                    }
                    continue;
                }

                moves.addMove(Move.encode(
                        pawnIndex,
                        to,
                        piece,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0
                ));
            }


            ///_________CHECK__FOR__CAPTURE_________///


            while(pMapCapture != 0){
                int to = Long.numberOfTrailingZeros(pMapCapture);
                long pieceBoardMask = (1L << to);
                int captured_piece = getPieceAt(board, pieceBoardMask);
                pMapCapture &= (pMapCapture - 1);

                //check for promotion
                if((pieceBoardMask & Rank1) != 0 || (pieceBoardMask & Rank8) != 0){
                    int startIndex = isWhite ? 1 : 9; //starting with the knight
                    for(int i = startIndex; i < startIndex + 4; i++){
                        moves.addMove(Move.encode(
                                pawnIndex,
                                to,
                                piece,
                                captured_piece,
                                1,
                                1,
                                0,
                                0,
                                i
                        ));
                    }
                    continue;
                }

                moves.addMove(Move.encode(
                        pawnIndex,
                        to,
                        piece,
                        captured_piece,
                        1,
                        0,
                        0,
                        0,
                        0
                ));

            }
        }

    }



    private void generateRookMoves(BitBoard board, MoveList moves){
        //TODO -> refactor + castle
        long enemyPieces, allyPieces;
        long rookBoard;
        if(isWhite){
            rookBoard = board.getWhiteRookBoard();
            enemyPieces = board.getBlackPieces();
            allyPieces = board.getWhitePieces();
        }else{
            rookBoard = board.getBlackRookBoard();
            enemyPieces = board.getWhitePieces();
            allyPieces = board.getBlackPieces();
        }

        while(rookBoard != 0){
            int rookIndex = Long.numberOfTrailingZeros(rookBoard);
            long rookPieceBoard = (1L << rookIndex);
            rookBoard &= (rookBoard - 1);

            long rMap = rookMoves[rookIndex];
            long conflictingPieces = rMap & (allyPieces | enemyPieces);
            long conflictingEnemyPieces = rMap & enemyPieces;


            //check for pin
            if((rMap & pinnedMaskBoard) != 0){
                rMap &= pinnedPieces[rookIndex];
            }
            //check for check
            rMap &= checkMask;


            //south
            long southRay = precomputedDirections[3][rookIndex];
            southRay &= rMap;

            if(southRay != 0){
                //there are moves left in the south direction
                if((southRay & conflictingPieces) != 0){
                    int southIndex = 63 - Long.numberOfLeadingZeros(southRay & conflictingPieces);
                    southRay = southRay ^ (precomputedDirections[3][southIndex] | (1L << southIndex));

                    if(((1L << southIndex) & conflictingEnemyPieces) != 0){
                        //generate capture
                        int capture = getPieceAt(board, (1L << southIndex));
                        moves.addMove(Move.encode(
                            rookIndex,
                            southIndex,
                            isWhite ? 2 : 10,
                            capture,
                            1,
                            0,
                            0,
                            0,
                            0
                        ));
                    }

                }
            }



            //east
            long eastRay = precomputedDirections[2][rookIndex];
            eastRay &= rMap;
            if(eastRay != 0){
                if((eastRay & conflictingPieces) != 0){
                    int eastIndex = 63 - Long.numberOfLeadingZeros(eastRay & conflictingPieces);
                    if(((1L << eastIndex) & conflictingEnemyPieces) != 0){
                        //generate capture
                        int capture = getPieceAt(board, (1L << eastIndex));
                        moves.addMove(Move.encode(
                                rookIndex,
                                eastIndex,
                                isWhite ? 2 : 10,
                                capture,
                                1,
                                0,
                                0,
                                0,
                                0
                        ));
                    }
                    eastRay = eastRay ^ (precomputedDirections[2][eastIndex] | (1L << eastIndex));
                }
            }



            //west
            long westRay = precomputedDirections[0][rookIndex];
            westRay &= rMap;
            if(westRay != 0){
                if((westRay & conflictingPieces) != 0){
                    int westIndex = Long.numberOfTrailingZeros(westRay & conflictingPieces);
                    if(((1L << westIndex) & conflictingEnemyPieces) != 0){
                        //generate capture
                        int capture = getPieceAt(board, (1L << westIndex));
                        moves.addMove(Move.encode(
                                rookIndex,
                                westIndex,
                                isWhite ? 2 : 10,
                                capture,
                                1,
                                0,
                                0,
                                0,
                                0
                        ));
                    }
                    westRay = westRay ^ (precomputedDirections[0][westIndex] | (1L << westIndex));
                }
            }


            //north
            long northRay = precomputedDirections[1][rookIndex];
            northRay &= rMap;
            if(northRay != 0){
                if((northRay & conflictingPieces) != 0){
                    int northIndex = Long.numberOfTrailingZeros(northRay & conflictingPieces);
                    if(((1L << northIndex) & conflictingEnemyPieces) != 0){
                        //generate capture
                        int capture = getPieceAt(board, (1L << northIndex));
                        moves.addMove(Move.encode(
                                rookIndex,
                                northIndex,
                                isWhite ? 2 : 10,
                                capture,
                                1,
                                0,
                                0,
                                0,
                                0
                        ));
                    }
                    northRay = northRay ^ ( precomputedDirections[1][northIndex] | (1L << northIndex));
                }
            }


            rMap &= (southRay | eastRay | westRay | northRay);


            while(rMap != 0){
                int to = Long.numberOfTrailingZeros(rMap);
                rMap &= (rMap - 1);
                moves.addMove(Move.encode(
                        rookIndex,
                        to,
                        isWhite ? 2 : 10,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0
                ));
            }
        }
    }



    private void generateKnightMoves(BitBoard board, MoveList moves){
        //find all knights on the board for the given color
        long knightBoard;
        int piece;
        long enemyPieces;
        long allyPieces;
        if(isWhite){
            piece = 1;
            knightBoard = board.getWhiteKnightBoard();
            enemyPieces = board.getBlackPieces();
            allyPieces = board.getWhitePieces();
        }
        else{
            piece = 9;
            knightBoard = board.getBlackKnightBoard();
            enemyPieces = board.getWhitePieces();
            allyPieces = board.getBlackPieces();
        }


        while(knightBoard != 0){
            int knightIndex = Long.numberOfTrailingZeros(knightBoard);
            knightBoard &= (knightBoard - 1);

            long kMap = knightMoves[knightIndex];
            kMap &= ~(allyPieces);                                      // remove all the moves conflicting with ally pieces
            if(((1L << knightIndex) & pinnedMaskBoard) != 0){
                //knight is pinned
                //might be 0 because knight can't move orthogonal or diagonal
                continue;
            }

            kMap &= checkMask;
            while(kMap != 0){
                int to = Long.numberOfTrailingZeros(kMap);
                int captured_piece = -1;
                long captureMask = (1L << to);
                kMap &= (kMap - 1);


                if((captureMask & enemyPieces) != 0){
                    captured_piece = getPieceAt(board, captureMask);
                }


                moves.addMove(Move.encode(
                        knightIndex,
                        to,
                        piece,
                        captured_piece,
                        (captured_piece != -1) ? 1 : 0,
                        0,
                        0,
                        0,
                        0
                        ));
            }
        }

    }



    private void generateBishopMoves(BitBoard board, MoveList moves){
        long enemyPieces, allyPieces;
        long bishopBoard;
        if(isWhite){
            bishopBoard = board.getWhiteBishopBoard();
            enemyPieces = board.getBlackPieces();
            allyPieces = board.getWhitePieces();
        }else{
            bishopBoard = board.getBlackBishopBoard();
            enemyPieces = board.getWhitePieces();
            allyPieces = board.getBlackPieces();
        }

        while(bishopBoard != 0){
            int bishopIndex = Long.numberOfTrailingZeros(bishopBoard);
            long bishopPieceBoard = (1L << bishopIndex);
            bishopBoard &= (bishopBoard - 1);

            long bMap = bishopMoves[bishopIndex];
            long conflictingPieces = bMap & (allyPieces | enemyPieces);
            long conflictingEnemyPieces = bMap & enemyPieces;

            //check for pin
            if((bMap & pinnedMaskBoard) != 0){
                bMap &= pinnedPieces[bishopIndex];
            }
            //check for check
            bMap &= checkMask;


            //south west
            long SWRay = precomputedDirections[7][bishopIndex];
            SWRay &= bMap;

            if(SWRay != 0){
                //there are moves left in the south direction
                if((SWRay & conflictingPieces) != 0){
                    int SWIndex = 63 - Long.numberOfLeadingZeros(SWRay & conflictingPieces);
                    SWRay = SWRay ^ (precomputedDirections[7][SWIndex] | (1L << SWIndex));

                    if(((1L << SWIndex) & conflictingEnemyPieces) != 0){
                        //generate capture
                        int capture = getPieceAt(board, (1L << SWIndex));
                        moves.addMove(Move.encode(
                                bishopIndex,
                                SWIndex,
                                isWhite ? 3 : 11,
                                capture,
                                1,
                                0,
                                0,
                                0,
                                0
                        ));
                    }

                }
            }



            //south east
            long SERay = precomputedDirections[6][bishopIndex];
            SERay &= bMap;
            if(SERay != 0){
                if((SERay & conflictingPieces) != 0){
                    int SEIndex = 63 - Long.numberOfLeadingZeros(SERay & conflictingPieces);
                    SERay = SERay ^ (precomputedDirections[6][SEIndex] | (1L << SEIndex));

                    if(((1L << SEIndex) & conflictingEnemyPieces) != 0){
                        //generate capture
                        int capture = getPieceAt(board, (1L << SEIndex));
                        moves.addMove(Move.encode(
                                bishopIndex,
                                SEIndex,
                                isWhite ? 3 : 11,
                                capture,
                                1,
                                0,
                                0,
                                0,
                                0
                        ));
                    }
                }
            }



            //north west
            long NWRay = precomputedDirections[4][bishopIndex];
            NWRay &= bMap;
            if(NWRay != 0){
                if((NWRay & conflictingPieces) != 0){
                    int NWIndex = Long.numberOfTrailingZeros(NWRay & conflictingPieces);
                    NWRay = NWRay ^ (precomputedDirections[4][NWIndex] | (1L << NWIndex));

                    if(((1L << NWIndex) & conflictingEnemyPieces) != 0){
                        //generate capture
                        int capture = getPieceAt(board, (1L << NWIndex));
                        moves.addMove(Move.encode(
                                bishopIndex,
                                NWIndex,
                                isWhite ? 3 : 11,
                                capture,
                                1,
                                0,
                                0,
                                0,
                                0
                        ));
                    }
                }
            }


            //north east
            long NERay = precomputedDirections[5][bishopIndex];
            NERay &= bMap;
            if(NERay != 0){
                if((NERay & conflictingPieces) != 0){
                    int NEIndex = Long.numberOfTrailingZeros(NERay & conflictingPieces);
                    NERay = NERay ^ ( precomputedDirections[5][NEIndex] | (1L << NEIndex));

                    if(((1L << NEIndex) & conflictingEnemyPieces) != 0){
                        //generate capture
                        int capture = getPieceAt(board, (1L << NEIndex));
                        moves.addMove(Move.encode(
                                bishopIndex,
                                NEIndex,
                                isWhite ? 3 : 11,
                                capture,
                                1,
                                0,
                                0,
                                0,
                                0
                        ));
                    }
                }
            }

            bMap &= (SWRay | SERay | NWRay | NERay);

            while(bMap != 0){
                int to = Long.numberOfTrailingZeros(bMap);
                bMap &= (bMap - 1);

                moves.addMove(Move.encode(
                        bishopIndex,
                        to,
                        isWhite ? 3 : 11,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0
                ));
            }
        }
    }






    private void generateQueenMoves(BitBoard board, MoveList moves){
        long enemyPieces, allyPieces;
        long queenBoard;
        if(isWhite){
            queenBoard = board.getWhiteQueenBoard();
            enemyPieces = board.getBlackPieces();
            allyPieces = board.getWhitePieces();
        }else{
            queenBoard = board.getBlackQueenBoard();
            enemyPieces = board.getWhitePieces();
            allyPieces = board.getBlackPieces();
        }


        while(queenBoard != 0){
            int queenIndex = Long.numberOfTrailingZeros(queenBoard);
            long queenPieceBoard = (1L << queenIndex);
            queenBoard &= (queenBoard - 1);

            long qMap = queenMoves[queenIndex];
            long conflictingPieces = qMap & (allyPieces | enemyPieces);
            long conflictingEnemyPieces = qMap & enemyPieces;

            //south
            long southRay = precomputedDirections[3][queenIndex];
            southRay &= qMap;

            if(southRay != 0){
                //there are moves left in the south direction
                if((southRay & conflictingPieces) != 0){
                    int southIndex = 63 - Long.numberOfLeadingZeros(southRay & conflictingPieces);
                    southRay = southRay ^ (precomputedDirections[3][southIndex] | (1L << southIndex));

                    if(((1L << southIndex) & conflictingEnemyPieces) != 0){
                        //generate capture
                        int capture = getPieceAt(board, (1L << southIndex));
                        moves.addMove(Move.encode(
                                queenIndex,
                                southIndex,
                                isWhite ? 4 : 12,
                                capture,
                                1,
                                0,
                                0,
                                0,
                                0
                        ));
                    }

                }
            }



            //east
            long eastRay = precomputedDirections[2][queenIndex];
            eastRay &= qMap;
            if(eastRay != 0){
                if((eastRay & conflictingPieces) != 0){
                    int eastIndex = 63 - Long.numberOfLeadingZeros(eastRay & conflictingPieces);
                    if(((1L << eastIndex) & conflictingEnemyPieces) != 0){
                        //generate capture
                        int capture = getPieceAt(board, (1L << eastIndex));
                        moves.addMove(Move.encode(
                                queenIndex,
                                eastIndex,
                                isWhite ? 4 : 12,
                                capture,
                                1,
                                0,
                                0,
                                0,
                                0
                        ));
                    }
                    eastRay = eastRay ^ (precomputedDirections[2][eastIndex] | (1L << eastIndex));
                }
            }



            //west
            long westRay = precomputedDirections[0][queenIndex];
            westRay &= qMap;
            if(westRay != 0){
                if((westRay & conflictingPieces) != 0){
                    int westIndex = Long.numberOfTrailingZeros(westRay & conflictingPieces);
                    if(((1L << westIndex) & conflictingEnemyPieces) != 0){
                        //generate capture
                        int capture = getPieceAt(board, (1L << westIndex));
                        moves.addMove(Move.encode(
                                queenIndex,
                                westIndex,
                                isWhite ? 4 : 12,
                                capture,
                                1,
                                0,
                                0,
                                0,
                                0
                        ));
                    }
                    westRay = westRay ^ (precomputedDirections[0][westIndex] | (1L << westIndex));
                }
            }


            //north
            long northRay = precomputedDirections[1][queenIndex];
            northRay &= qMap;
            if(northRay != 0){
                if((northRay & conflictingPieces) != 0){
                    int northIndex = Long.numberOfTrailingZeros(northRay & conflictingPieces);
                    if(((1L << northIndex) & conflictingEnemyPieces) != 0){
                        //generate capture
                        int capture = getPieceAt(board, (1L << northIndex));
                        moves.addMove(Move.encode(
                                queenIndex,
                                northIndex,
                                isWhite ? 4 : 12,
                                capture,
                                1,
                                0,
                                0,
                                0,
                                0
                        ));
                    }
                    northRay = northRay ^ ( precomputedDirections[1][northIndex] | (1L << northIndex));
                }
            }


            //south west
            long SWRay = precomputedDirections[7][queenIndex];
            SWRay &= qMap;

            if(SWRay != 0){
                //there are moves left in the south direction
                if((SWRay & conflictingPieces) != 0){
                    int SWIndex = 63 - Long.numberOfLeadingZeros(SWRay & conflictingPieces);
                    SWRay = SWRay ^ (precomputedDirections[7][SWIndex] | (1L << SWIndex));

                    if(((1L << SWIndex) & conflictingEnemyPieces) != 0){
                        //generate capture
                        int capture = getPieceAt(board, (1L << SWIndex));
                        moves.addMove(Move.encode(
                                queenIndex,
                                SWIndex,
                                isWhite ? 4 : 12,
                                capture,
                                1,
                                0,
                                0,
                                0,
                                0
                        ));
                    }

                }
            }



            //south east
            long SERay = precomputedDirections[6][queenIndex];
            SERay &= qMap;
            if(SERay != 0){
                if((SERay & conflictingPieces) != 0){
                    int SEIndex = 63 - Long.numberOfLeadingZeros(SERay & conflictingPieces);
                    SERay = SERay ^ (precomputedDirections[6][SEIndex] | (1L << SEIndex));

                    if(((1L << SEIndex) & conflictingEnemyPieces) != 0){
                        //generate capture
                        int capture = getPieceAt(board, (1L << SEIndex));
                        moves.addMove(Move.encode(
                                queenIndex,
                                SEIndex,
                                isWhite ? 4 : 12,
                                capture,
                                1,
                                0,
                                0,
                                0,
                                0
                        ));
                    }
                }
            }



            //north west
            long NWRay = precomputedDirections[4][queenIndex];
            NWRay &= qMap;
            if(NWRay != 0){
                if((NWRay & conflictingPieces) != 0){
                    int NWIndex = Long.numberOfTrailingZeros(NWRay & conflictingPieces);
                    NWRay = NWRay ^ (precomputedDirections[4][NWIndex] | (1L << NWIndex));

                    if(((1L << NWIndex) & conflictingEnemyPieces) != 0){
                        //generate capture
                        int capture = getPieceAt(board, (1L << NWIndex));
                        moves.addMove(Move.encode(
                                queenIndex,
                                NWIndex,
                                isWhite ? 4 : 12,
                                capture,
                                1,
                                0,
                                0,
                                0,
                                0
                        ));
                    }
                }
            }


            //north east
            long NERay = precomputedDirections[5][queenIndex];
            NERay &= qMap;
            if(NERay != 0){
                if((NERay & conflictingPieces) != 0){
                    int NEIndex = Long.numberOfTrailingZeros(NERay & conflictingPieces);
                    NERay = NERay ^ ( precomputedDirections[5][NEIndex] | (1L << NEIndex));

                    if(((1L << NEIndex) & conflictingEnemyPieces) != 0){
                        //generate capture
                        int capture = getPieceAt(board, (1L << NEIndex));
                        moves.addMove(Move.encode(
                                queenIndex,
                                NEIndex,
                                isWhite ? 4 : 12,
                                capture,
                                1,
                                0,
                                0,
                                0,
                                0
                        ));
                    }
                }
            }

            qMap &= (southRay | eastRay | northRay | westRay | SWRay | SERay | NWRay | NERay);

            //check for pin
            if((qMap & pinnedMaskBoard) != 0){
                qMap &= pinnedPieces[queenIndex];
            }
            //check for check
            qMap &= checkMask;


            while(qMap != 0){
                int to = Long.numberOfTrailingZeros(qMap);
                qMap &= (qMap - 1);

                moves.addMove(Move.encode(
                        queenIndex,
                        to,
                        isWhite ? 4 : 12,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0
                ));
            }
        }
    }

    private void generateKingMoves(BitBoard board, MoveList moves){
        //TODO problem with the check mask in here

        System.out.println("king moves");
        long enemyPieces, allyPieces;
        long kingBoard;
        if(isWhite){
            kingBoard = board.getWhiteKingBoard();
            enemyPieces = board.getBlackPieces();
            allyPieces = board.getWhitePieces();
        }else{
            kingBoard = board.getBlackKingBoard();
            enemyPieces = board.getWhitePieces();
            allyPieces = board.getBlackPieces();
        }


        int kingIndex = Long.numberOfTrailingZeros(kingBoard);

        long kMap = kingMoves[kingIndex];

        //king can't be pinned
        //check for check
        //kMap &= checkMask;
        kMap &= ~(allyPieces);

        while(kMap != 0){
            int to = Long.numberOfTrailingZeros(kMap);
            kMap &= (kMap - 1);

            if(isSquareAttacked(board, to)){
                continue;
            }

            if(((1L << to) & enemyPieces) != 0){
                //capture
                int capture = getPieceAt(board, (1L << to));
                moves.addMove(Move.encode(
                        kingIndex,
                        to,
                        isWhite ? 5 : 13,
                        capture,
                        1,
                        0,
                        0,
                        0,
                        0
                ));
                continue;
            }

            moves.addMove(Move.encode(
                    kingIndex,
                    to,
                    isWhite ? 5 : 13,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
            ));
        }

    }



    private int getPieceAt(BitBoard board, long targetMask) {
        if (isWhite) {
            if ((targetMask & board.getBlackPawnBoard()) != 0)   return 8; // Pawn
            if ((targetMask & board.getBlackKnightBoard()) != 0) return 9; // Knight
            if ((targetMask & board.getBlackRookBoard()) != 0)   return 10; // Rook
            if ((targetMask & board.getBlackBishopBoard()) != 0) return 11; // Bishop
            if ((targetMask & board.getBlackQueenBoard()) != 0)  return 12; // Queen
        } else {
            if ((targetMask & board.getWhitePawnBoard()) != 0)   return 0;
            if ((targetMask & board.getWhiteKnightBoard()) != 0) return 1;
            if ((targetMask & board.getWhiteRookBoard()) != 0)   return 2;
            if ((targetMask & board.getWhiteBishopBoard()) != 0) return 3;
            if ((targetMask & board.getWhiteQueenBoard()) != 0)  return 4;
        }
        //TODO if we ever get to capture the king is check mate(should not happen as the list of moves get's verified to be nonempty for check mate validity)
        return -1;
    }


}

