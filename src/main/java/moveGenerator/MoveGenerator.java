package MoveGenerator;

import Board.BitBoard;
import Move.Move;
import Move.MoveList;

public class MoveGenerator {

    public final static long[] knightMoves = PrecomputeMoves.knightMoves;
    public final static long[] kingMoves = PrecomputeMoves.kingMoves;
    public final static long[][] pawnMoves = PrecomputeMoves.pawnMoves;                 //0 is white, 2 is black, 1 is white captures, 3 is black captures
    public final static long[] queenMoves = PrecomputeMoves.queenMoves;
    public final static long[] bishopMoves = PrecomputeMoves.bishopMoves;
    public final static long[] rookMoves = PrecomputeMoves.rookMoves;

    private final static long Rank1 = 0x00000000000000FFL;
    private final static long Rank8 = 0xFF00000000000000L;

    public final static long[] pinnedPieces = PrecomputeMasks.pinnedPieces;
    public final static long[][] rayMovement = PrecomputeMoves.rayMovement;
    public static long[] pinnedMaskBoard = PrecomputeMasks.pinnedMaskBoard;
    public static long[] checkMask = PrecomputeMasks.checkMask;
    private final static long[][] precomputedDirections = PrecomputeMoves.precomputedDirections; //0, 1, 2, 3 are for Orthogonal directions
                                                                                                // 4, 5, 6, 7 are for Diagonal directions

    private static boolean isWhite;



    public static MoveList generateMoves(BitBoard board, boolean isW){
        isWhite = isW;
        MoveList moves = new MoveList();

        PrecomputeMasks.calculatePinnedMask(board, isWhite);
        if(!PrecomputeMasks.calculateCheckMask(board, isWhite)){
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




    private static void generatePawnMoves(BitBoard board, MoveList moves){
        long pawnBoard;
        int piece;
        long enemyPieces;
        long allPieces = board.getWhitePieces() | board.getBlackPieces();
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
            } else{
                pMapMoveAhead = pawnMoves[2][pawnIndex];
                pMapCapture = pawnMoves[3][pawnIndex];
            }

            if((pawnMap & pinnedMaskBoard[0]) != 0){
                // if pawn pinned
                pMapMoveAhead &= pinnedPieces[pawnIndex];
                pMapCapture &= pinnedPieces[pawnIndex];
            }

            pMapMoveAhead &= checkMask[0];
            pMapCapture &= checkMask[0];

            long pMapEnPassant = pMapCapture;

            pMapMoveAhead &= ~(allPieces);              //move ahead pawns that are blocked by all pieces
            pMapCapture &= enemyPieces;                 //capture pawns can only move if enemy pieces are in their way

            //only one en passant move can exist per move
            long enPassantMask = (1L << board.getEnPassantSquare());
            if(board.getEnPassantSquare() != -1 && (pMapEnPassant & enPassantMask) != 0) {
                // add the en passant move to the list
                moves.addMove(Move.encode(pawnIndex, board.getEnPassantSquare(), piece, isWhite ? 8 : 0, 1, 0, 0, 1, 0));
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
                        moves.addMove(Move.encode(pawnIndex, to, piece, 0, 0, 1, 0, 0, i));
                    }
                    continue;
                }
                moves.addMove(Move.encode(pawnIndex, to, piece, 0, 0, 0, 0, 0, 0));
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
                        moves.addMove(Move.encode(pawnIndex, to, piece, captured_piece, 1, 1, 0, 0, i));
                    }
                    continue;
                }
                moves.addMove(Move.encode(pawnIndex, to, piece, captured_piece, 1, 0, 0, 0, 0));
            }
        }
    }





    private static void generateRookMoves(BitBoard board, MoveList moves){
        long enemyPieces, allyPieces;
        long rookBoard;
        //castling
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
            rookBoard &= (rookBoard - 1);

            long rMap = rookMoves[rookIndex];
            long conflictingPieces = rMap & (allyPieces | enemyPieces);
            long conflictingEnemyPieces = rMap & enemyPieces;

            //check for pin
            if(((1L << rookIndex) & pinnedMaskBoard[0]) != 0){
                rMap &= pinnedPieces[rookIndex];
            }
            //check for check
            rMap &= checkMask[0];

            //south
            long southRay = precomputedDirections[3][rookIndex];
            if((southRay & rMap) != 0){
                //there are moves left in the south direction
                if((southRay & conflictingPieces) != 0){
                    int southIndex = 63 - Long.numberOfLeadingZeros(southRay & conflictingPieces);
                    southRay = rMap & (southRay ^ (precomputedDirections[3][southIndex] | (1L << southIndex)));

                    if(((1L << southIndex) & conflictingEnemyPieces) != 0){     //check if the last piece was an enemy one
                        if(((1L << southIndex) & rMap) != 0){           //check if we can get to the enemy piece
                            //generate capture
                            int capture = getPieceAt(board, (1L << southIndex));
                            moves.addMove(Move.encode(rookIndex, southIndex, isWhite ? 2 : 10, capture, 1, 0, 0, 0, 0));
                        }
                    }
                } else southRay &= rMap;
            } else southRay = 0;

            //east
            long eastRay = precomputedDirections[2][rookIndex];
            if((eastRay & rMap) != 0){
                if((eastRay & conflictingPieces) != 0){
                    int eastIndex = 63 - Long.numberOfLeadingZeros(eastRay & conflictingPieces);
                    eastRay = rMap & (eastRay ^ (precomputedDirections[2][eastIndex] | (1L << eastIndex)));

                    if(((1L << eastIndex) & conflictingEnemyPieces) != 0){
                        if(((1L << eastIndex) & rMap) != 0){
                            //generate capture
                            int capture = getPieceAt(board, (1L << eastIndex));
                            moves.addMove(Move.encode(rookIndex, eastIndex, isWhite ? 2 : 10, capture, 1, 0, 0, 0, 0));
                        }

                    }
                } else eastRay &= rMap;
            } else eastRay = 0;

            //west
            long westRay = precomputedDirections[0][rookIndex];
            if((rMap & westRay) != 0){
                if((westRay & conflictingPieces) != 0){
                    int westIndex = Long.numberOfTrailingZeros(westRay & conflictingPieces);
                    westRay = rMap & (westRay ^ (precomputedDirections[0][westIndex] | (1L << westIndex)));

                    if(((1L << westIndex) & conflictingEnemyPieces) != 0){
                        if(((1L << westIndex) & rMap) != 0){
                            //generate capture
                            int capture = getPieceAt(board, (1L << westIndex));
                            moves.addMove(Move.encode(rookIndex, westIndex, isWhite ? 2 : 10, capture, 1, 0, 0, 0, 0));
                        }

                    }
                } else westRay &= rMap;
            } else westRay = 0;

            //north
            long northRay = precomputedDirections[1][rookIndex];
            if((rMap & northRay) != 0){
                if((northRay & conflictingPieces) != 0){
                    int northIndex = Long.numberOfTrailingZeros(northRay & conflictingPieces);
                    northRay = rMap & (northRay ^ ( precomputedDirections[1][northIndex] | (1L << northIndex)));

                    if(((1L << northIndex) & conflictingEnemyPieces) != 0){
                        if(((1L << northIndex) & rMap) != 0){
                            //generate capture
                            int capture = getPieceAt(board, (1L << northIndex));
                            moves.addMove(Move.encode(rookIndex, northIndex, isWhite ? 2 : 10, capture, 1, 0, 0, 0, 0));
                        }
                    }
                } else northRay &= rMap;

            } else northRay = 0;

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




    private static void generateKnightMoves(BitBoard board, MoveList moves){
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
            if(((1L << knightIndex) & pinnedMaskBoard[0]) != 0){
                //knight is pinned
                //might be 0 because knight can't move orthogonal or diagonal
                continue;
            }

            kMap &= checkMask[0];
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



    private static void generateBishopMoves(BitBoard board, MoveList moves){
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
            bishopBoard &= (bishopBoard - 1);

            long bMap = bishopMoves[bishopIndex];
            long conflictingPieces = bMap & (allyPieces | enemyPieces);
            long conflictingEnemyPieces = bMap & enemyPieces;

            //check for pin
            if(((1L << bishopIndex) & pinnedMaskBoard[0]) != 0){
                bMap &= pinnedPieces[bishopIndex];
            }
            //check for check
            bMap &= checkMask[0];


            //south-west
            long SWRay = precomputedDirections[7][bishopIndex];
            if((SWRay & bMap) != 0){
                //there are moves left in the south direction
                if((SWRay & conflictingPieces) != 0){
                    int SWIndex = 63 - Long.numberOfLeadingZeros(SWRay & conflictingPieces);
                    SWRay = bMap & (SWRay ^ (precomputedDirections[7][SWIndex] | (1L << SWIndex)));

                    if(((1L << SWIndex) & conflictingEnemyPieces) != 0){
                        if(((1L << SWIndex) & bMap) != 0) {
                            int capture = getPieceAt(board, (1L << SWIndex));
                            moves.addMove(Move.encode(bishopIndex, SWIndex, isWhite ? 3 : 11, capture, 1, 0, 0, 0, 0));
                        }
                    }

                }else SWRay &= bMap;
            }else SWRay = 0;



            //south-east
            long SERay = precomputedDirections[6][bishopIndex];
            if((bMap & SERay) != 0){
                if((SERay & conflictingPieces) != 0){
                    int SEIndex = 63 - Long.numberOfLeadingZeros(SERay & conflictingPieces);
                    SERay = bMap & (SERay ^ (precomputedDirections[6][SEIndex] | (1L << SEIndex)));

                    if(((1L << SEIndex) & conflictingEnemyPieces) != 0){
                        if(((1L << SEIndex) & bMap) != 0){
                            int capture = getPieceAt(board, (1L << SEIndex));
                            moves.addMove(Move.encode(bishopIndex, SEIndex, isWhite ? 3 : 11, capture, 1, 0, 0, 0, 0));
                        }
                    }
                } else SERay &= bMap;
            } else SERay = 0;

            //north-west
            long NWRay = precomputedDirections[4][bishopIndex];
            if((bMap & NWRay) != 0){
                if((NWRay & conflictingPieces) != 0){
                    int NWIndex = Long.numberOfTrailingZeros(NWRay & conflictingPieces);
                    NWRay = bMap & (NWRay ^ (precomputedDirections[4][NWIndex] | (1L << NWIndex)));

                    if(((1L << NWIndex) & conflictingEnemyPieces) != 0){
                        if(((1L << NWIndex) & bMap) != 0){
                            int capture = getPieceAt(board, (1L << NWIndex));
                            moves.addMove(Move.encode(bishopIndex, NWIndex, isWhite ? 3 : 11, capture, 1, 0, 0, 0, 0));
                        }
                    }
                } else NWRay &= bMap;
            } else NWRay = 0;

            //north-east
            long NERay = precomputedDirections[5][bishopIndex];
            if((bMap & NERay) != 0){
                if((NERay & conflictingPieces) != 0){
                    int NEIndex = Long.numberOfTrailingZeros(NERay & conflictingPieces);
                    NERay = bMap & (NERay ^ ( precomputedDirections[5][NEIndex] | (1L << NEIndex)));

                    if(((1L << NEIndex) & conflictingEnemyPieces) != 0){
                        if(((1L << NEIndex) & bMap) != 0){
                            int capture = getPieceAt(board, (1L << NEIndex));
                            moves.addMove(Move.encode(bishopIndex, NEIndex, isWhite ? 3 : 11, capture, 1, 0, 0, 0, 0));
                        }
                    }
                } else NERay &= bMap;
            } else NERay = 0;

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




    private static void generateQueenMoves(BitBoard board, MoveList moves){
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
            queenBoard &= (queenBoard - 1);

            long qMap = queenMoves[queenIndex];
            long conflictingPieces = qMap & (allyPieces | enemyPieces);
            long conflictingEnemyPieces = qMap & enemyPieces;

            if(((1L << queenIndex) & pinnedMaskBoard[0]) != 0){
                qMap &= pinnedPieces[queenIndex];
            }

            qMap &= checkMask[0];

            //south
            long southRay = precomputedDirections[3][queenIndex];
            if((southRay & qMap) != 0){
                //there are moves left in the south direction
                if((southRay & conflictingPieces) != 0){
                    int southIndex = 63 - Long.numberOfLeadingZeros(southRay & conflictingPieces);
                    southRay = qMap & (southRay ^ (precomputedDirections[3][southIndex] | (1L << southIndex)));

                    if(((1L << southIndex) & conflictingEnemyPieces) != 0){     //check if the last piece was an enemy one
                        if(((1L << southIndex) & qMap) != 0){           //check if we can get to the enemy piece
                            //generate capture
                            int capture = getPieceAt(board, (1L << southIndex));
                            moves.addMove(Move.encode(queenIndex, southIndex, isWhite ? 4 : 12, capture, 1, 0, 0, 0, 0));
                        }
                    }
                } else southRay &= qMap;

            } else southRay = 0;

            //east
            long eastRay = precomputedDirections[2][queenIndex];
            if((eastRay & qMap) != 0){
                if((eastRay & conflictingPieces) != 0){
                    int eastIndex = 63 - Long.numberOfLeadingZeros(eastRay & conflictingPieces);
                    eastRay = qMap & (eastRay ^ (precomputedDirections[2][eastIndex] | (1L << eastIndex)));

                    if(((1L << eastIndex) & conflictingEnemyPieces) != 0){
                        if(((1L << eastIndex) & qMap) != 0){
                            //generate capture
                            int capture = getPieceAt(board, (1L << eastIndex));
                            moves.addMove(Move.encode(queenIndex, eastIndex, isWhite ? 4 : 12, capture, 1, 0, 0, 0, 0));
                        }
                    }
                } else eastRay &= qMap;
            } else eastRay = 0;

            //west
            long westRay = precomputedDirections[0][queenIndex];
            if((qMap & westRay) != 0){
                if((westRay & conflictingPieces) != 0){
                    int westIndex = Long.numberOfTrailingZeros(westRay & conflictingPieces);
                    westRay = qMap & (westRay ^ (precomputedDirections[0][westIndex] | (1L << westIndex)));

                    if(((1L << westIndex) & conflictingEnemyPieces) != 0){
                        if(((1L << westIndex) & qMap) != 0){
                            //generate capture
                            int capture = getPieceAt(board, (1L << westIndex));
                            moves.addMove(Move.encode(queenIndex, westIndex, isWhite ? 4 : 12, capture, 1, 0, 0, 0, 0));
                        }
                    }
                } else westRay &= qMap;
            } else westRay = 0;

            //north
            long northRay = precomputedDirections[1][queenIndex];
            if((qMap & northRay) != 0){
                if((northRay & conflictingPieces) != 0){
                    int northIndex = Long.numberOfTrailingZeros(northRay & conflictingPieces);
                    northRay = qMap & (northRay ^ ( precomputedDirections[1][northIndex] | (1L << northIndex)));

                    if(((1L << northIndex) & conflictingEnemyPieces) != 0){
                        if(((1L << northIndex) & qMap) != 0){
                            //generate capture
                            int capture = getPieceAt(board, (1L << northIndex));
                            moves.addMove(Move.encode(queenIndex, northIndex, isWhite ? 4 : 12, capture, 1, 0, 0, 0, 0));
                        }
                    }
                } else northRay &= qMap;
            } else northRay = 0;

            //south-west
            long SWRay = precomputedDirections[7][queenIndex];
            if((SWRay & qMap) != 0){
                //there are moves left in the south direction
                if((SWRay & conflictingPieces) != 0) {
                    int SWIndex = 63 - Long.numberOfLeadingZeros(SWRay & conflictingPieces);
                    SWRay = qMap & (SWRay ^ (precomputedDirections[7][SWIndex] | (1L << SWIndex)));

                    if (((1L << SWIndex) & conflictingEnemyPieces) != 0) {
                        if (((1L << SWIndex) & qMap) != 0) {
                            int capture = getPieceAt(board, (1L << SWIndex));
                            moves.addMove(Move.encode(queenIndex, SWIndex, isWhite ? 4 : 12, capture, 1, 0, 0, 0, 0));
                        }
                    }
                }else SWRay &= qMap;
            }else SWRay = 0;

            //south-east
            long SERay = precomputedDirections[6][queenIndex];
            if((qMap & SERay) != 0){
                if((SERay & conflictingPieces) != 0){
                    int SEIndex = 63 - Long.numberOfLeadingZeros(SERay & conflictingPieces);
                    SERay = qMap & (SERay ^ (precomputedDirections[6][SEIndex] | (1L << SEIndex)));

                    if(((1L << SEIndex) & conflictingEnemyPieces) != 0){
                        if(((1L << SEIndex) & qMap) != 0){
                            int capture = getPieceAt(board, (1L << SEIndex));
                            moves.addMove(Move.encode(queenIndex, SEIndex, isWhite ? 4 : 12, capture, 1, 0, 0, 0, 0));
                        }
                    }
                }
                else SERay &= qMap;
            } else SERay = 0;

            //north-west
            long NWRay = precomputedDirections[4][queenIndex];
            if((qMap & NWRay) != 0){
                if((NWRay & conflictingPieces) != 0){
                    int NWIndex = Long.numberOfTrailingZeros(NWRay & conflictingPieces);
                    NWRay = qMap & (NWRay ^ (precomputedDirections[4][NWIndex] | (1L << NWIndex)));

                    if(((1L << NWIndex) & conflictingEnemyPieces) != 0){
                        if(((1L << NWIndex) & qMap) != 0){
                            int capture = getPieceAt(board, (1L << NWIndex));
                            moves.addMove(Move.encode(queenIndex, NWIndex, isWhite ? 4 : 12, capture, 1, 0, 0, 0, 0));
                        }
                    }
                } else NWRay &= qMap;
            } else NWRay = 0;

            //north-east
            long NERay = precomputedDirections[5][queenIndex];
            if((qMap & NERay) != 0){
                if((NERay & conflictingPieces) != 0){
                    int NEIndex = Long.numberOfTrailingZeros(NERay & conflictingPieces);
                    NERay = qMap & (NERay ^ ( precomputedDirections[5][NEIndex] | (1L << NEIndex)));

                    if(((1L << NEIndex) & conflictingEnemyPieces) != 0){
                        if(((1L << NEIndex) & qMap) != 0){
                            int capture = getPieceAt(board, (1L << NEIndex));
                            moves.addMove(Move.encode(queenIndex, NEIndex, isWhite ? 4 : 12, capture, 1, 0, 0, 0, 0));
                        }
                    }
                } else NERay &= qMap;
            } else NERay = 0;

            qMap &= (southRay | eastRay | northRay | westRay | SWRay | SERay | NWRay | NERay);

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




    private static void generateKingMoves(BitBoard board, MoveList moves){
        long enemyPieces, allyPieces;
        long kingBoard;
        if(isWhite){
            kingBoard = board.getWhiteKingBoard();
            enemyPieces = board.getBlackPieces();
            allyPieces = board.getWhitePieces();

            if(checkMask[0] == -1){
                //if king is not in check
                if(board.isWhiteQueenCastle() && (rayMovement[7][3] & (enemyPieces | allyPieces)) == 0 ){
                    //castling is available and there are no pieces between king and rook
                    if(!PrecomputeMasks.isSquareAttacked(board, 4, isWhite) && !PrecomputeMasks.isSquareAttacked(board, 5, isWhite)){
                        //check if king passes through check and if it lands on check
                        moves.addMove(Move.encode(3, 5, 5, 0, 0, 0, 1, 0, 0));
                    }
                }
                if(board.isWhiteKingCastle() && (rayMovement[0][3] & (enemyPieces | allyPieces)) == 0 ){
                    if(!PrecomputeMasks.isSquareAttacked(board, 2, isWhite) && !PrecomputeMasks.isSquareAttacked(board, 1, isWhite)){
                        moves.addMove(Move.encode(3, 1, 5, 0, 0, 0, 1, 0, 0));
                    }
                }
            }
        }else{
            kingBoard = board.getBlackKingBoard();
            enemyPieces = board.getWhitePieces();
            allyPieces = board.getBlackPieces();

            if(checkMask[0] == -1){
                //if the king is not in check
                if(board.isBlackQueenCastle() && (rayMovement[59][63] & (enemyPieces | allyPieces)) == 0 ){
                    if(!PrecomputeMasks.isSquareAttacked(board, 60, isWhite) && !PrecomputeMasks.isSquareAttacked(board, 61, isWhite)){
                        moves.addMove(Move.encode(59, 61, 13, 0, 0, 0, 1, 0, 0));
                    }
                }
                if(board.isBlackKingCastle() && (rayMovement[59][56] & (enemyPieces | allyPieces)) == 0 ){
                    if(!PrecomputeMasks.isSquareAttacked(board, 58, isWhite) && !PrecomputeMasks.isSquareAttacked(board, 57, isWhite)){
                        moves.addMove(Move.encode(59, 57, 13, 0, 0, 0, 1, 0, 0));
                    }
                }
            }
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

            if(PrecomputeMasks.isSquareAttacked(board, to, isWhite)){
                continue;
            }

            if(((1L << to) & enemyPieces) != 0){
                //capture
                int capture = getPieceAt(board, (1L << to));
                moves.addMove(Move.encode(kingIndex, to, isWhite ? 5 : 13, capture, 1, 0, 0, 0, 0));
                continue;
            }

            moves.addMove(Move.encode(kingIndex, to, isWhite ? 5 : 13, 0, 0, 0, 0, 0, 0));
        }
    }




    private static int getPieceAt(BitBoard board, long targetMask) {
            if (isWhite) {
                if ((targetMask & board.getBlackPawnBoard()) != 0)   return 8;  // Pawn
                if ((targetMask & board.getBlackKnightBoard()) != 0) return 9;  // Knight
                if ((targetMask & board.getBlackRookBoard()) != 0)   return 10; // Rook
                if ((targetMask & board.getBlackBishopBoard()) != 0) return 11; // Bishop
                if ((targetMask & board.getBlackQueenBoard()) != 0)  return 12; // Queen
                if ((targetMask & board.getBlackKingBoard()) != 0)   return 13; // King
            } else {
                if ((targetMask & board.getWhitePawnBoard()) != 0)   return 0;
                if ((targetMask & board.getWhiteKnightBoard()) != 0) return 1;
                if ((targetMask & board.getWhiteRookBoard()) != 0)   return 2;
                if ((targetMask & board.getWhiteBishopBoard()) != 0) return 3;
                if ((targetMask & board.getWhiteQueenBoard()) != 0)  return 4;
                if ((targetMask & board.getWhiteKingBoard()) != 0)   return 5;
            }

        //TODO if we ever get to capture the king is check mate(should not happen as the list of moves get's verified to be nonempty for check mate validity)
        return -1;
    }
}

