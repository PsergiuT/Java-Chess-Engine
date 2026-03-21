package MoveGenerator;

import Board.BitBoard;

public class PrecomputeMasks {
    public final static long[] knightMoves = PrecomputeMoves.knightMoves;
    public final static long[] kingMoves = PrecomputeMoves.kingMoves;
    public final static long[][] pawnMoves = PrecomputeMoves.pawnMoves;
    public final static long[] bishopMoves = PrecomputeMoves.bishopMoves;
    public final static long[] rookMoves = PrecomputeMoves.rookMoves;


    public final static long[] pinnedPieces = new long[64];
    public final static long[][] rayMovement = PrecomputeMoves.rayMovement;
    public static long[] pinnedMaskBoard = new long[1];
    public static long[] checkMask = { 0xFFFFFFFFFFFFFFFFL };



    public static void calculatePinnedMask(BitBoard board, boolean isWhite){
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

        pinnedMaskBoard[0] = 0;
        while(orthogonalSlidingAttacks != 0){
            int enemy = Long.numberOfTrailingZeros(orthogonalSlidingAttacks);
            long ray = rayMovement[kingIndex][enemy] & ~(1L << kingIndex);
            orthogonalSlidingAttacks &= (orthogonalSlidingAttacks - 1);

            long allyHits = ray & allyPieces;                                           //all the ally pieces that are in the ray
            long enemyHits = ray & enemyPieces;                                         //all the enemy pieces that are in the ray

            if(Long.bitCount(allyHits) == 1 && enemyHits == 0){
                int allyPinnedPiece = Long.numberOfTrailingZeros(allyHits);
                pinnedPieces[allyPinnedPiece] = (ray | (1L << enemy));                  //store the bitmap of the places where the pinned piece can go plus enemy square
                pinnedMaskBoard[0] |= allyHits;                                         //store a map of all the ally pieces that are pinned
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
                    pinnedMaskBoard[0] |= allyHits;                                        //store a map of all the ally pieces that are pinned
                }
            }
        }

        while(diagonalSlidingAttacks != 0){
            int enemy = Long.numberOfTrailingZeros(diagonalSlidingAttacks);
            long ray = rayMovement[kingIndex][enemy] & ~(1L << kingIndex);
            diagonalSlidingAttacks &= (diagonalSlidingAttacks - 1);

            long allyHits = ray & allyPieces;                                           //all the ally pieces that are in the ray
            long enemyHits = ray & enemyPieces;                                         //all the enemy pieces that are in the ray
            if(Long.bitCount(allyHits) == 1 && enemyHits == 0){
                int allyPinnedPiece = Long.numberOfTrailingZeros(allyHits);
                pinnedPieces[allyPinnedPiece] = (ray | (1L << enemy));                  //store the bitmap of the places where the pinned piece can go
                pinnedMaskBoard[0] |= allyHits;                                         //store a map of all the ally pieces that are pinned
            }
        }
    }





    public static boolean calculateCheckMask(BitBoard board, boolean isWhite){
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
            long ray = rayMovement[kingIndex][enemy] & ~(1L << kingIndex);
            orthogonalSlidingAttacks &= (orthogonalSlidingAttacks - 1);

            //we don't want anything between the king and the sliding piece

            if((ray & allPieces) == 0){
                checkers ++;
                attackingMap |= (ray | (1L << enemy));
            }
        }

        while(diagonalSlidingAttacks != 0){
            int enemy = Long.numberOfTrailingZeros(diagonalSlidingAttacks);
            long ray = rayMovement[kingIndex][enemy] & ~(1L << kingIndex);
            diagonalSlidingAttacks &= (diagonalSlidingAttacks - 1);

            //we don't want anything between the king and the sliding piece
            if((ray & allPieces) == 0){
                checkers ++;
                attackingMap |= (ray | (1L << enemy));
            }
        }

        if(checkers == 0){
            checkMask[0] = 0XFFFFFFFFFFFFFFFFL;
            return true;
        }
        else if(checkers == 1){
            checkMask[0] = attackingMap;
            return true;
        }
        return false;
    }



    public static boolean isSquareAttacked(BitBoard board, int square, boolean isWhite){
        //THIS ONLY WORKS FOR KING AND CASTLING CHECK
        long enemyPawn, enemyKnight, enemyRook, enemyBishop, enemyQueen, enemyKing;
        long pawnBoard;
        long allPieces = (board.getBlackPieces() | board.getWhitePieces());
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
            allPieces &= ~(board.getWhiteKingBoard());      //we remove the king so it won't interfere with ray movement
                                                            //if we want to check for other pieces, we need to remove them from all pieces
        }
        else {
            pawnBoard = pawnMoves[3][square];
            enemyPawn = board.getWhitePawnBoard();
            enemyKnight = board.getWhiteKnightBoard();
            enemyRook = board.getWhiteRookBoard();
            enemyBishop = board.getWhiteBishopBoard();
            enemyQueen = board.getWhiteQueenBoard();
            enemyKing = board.getWhiteKingBoard();
            allPieces &= ~(board.getBlackKingBoard());
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
            if(((rayMovement[square][enemy] & ~(1L << square)) & allPieces) == 0)          //if hits == 0
                return true;
            orthogonalSlidingAttacks &= (orthogonalSlidingAttacks - 1);
        }


        while(diagonalSlidingAttacks != 0){
            int enemy = Long.numberOfTrailingZeros(diagonalSlidingAttacks);
            if(((rayMovement[square][enemy] & ~(1L << square)) & allPieces) == 0){
                return true;
            }
            diagonalSlidingAttacks &= (diagonalSlidingAttacks - 1);
        }
        return false;
    }
}
