package chess.search;

import chess.bitboard.BitBoard;
import chess.bitboard.MoveGenerator;
import chess.move.Move;
import chess.move.MoveList;

public class Perft {
    public static long count_nodes(BitBoard board, int depth){
        long count = 0;
        //base case
        if(depth == 1) {
            return MoveGenerator.generateMoves(board, board.isWhiteTurn).getSize();
        }

        MoveList moves = MoveGenerator.generateMoves(board, board.isWhiteTurn);
        for(int i = 0; i < moves.getSize(); i++){
            board.makeMove(moves.getMoves()[i]);

            //String dest = printMoveFromTo(moves.getMoves()[i]);
            long cnt = count_nodes(board, depth - 1);
            //System.out.println(dest + " : " + (cnt));
            count += cnt;

            board.undoMove(moves.getMoves()[i]);
        }
        return count;
    }



    public static int DEBUG_count_nodes(BitBoard board, int depth, int show){
        boolean whiteKingCastle;
        boolean blackKingCastle;
        boolean whiteQueenCastle;
        boolean blackQueenCastle;

        int count = 0;
        //base case
        if(depth == 1) {
            return MoveGenerator.generateMoves(board, board.isWhiteTurn).getSize();
        }

        MoveList moves = MoveGenerator.generateMoves(board, board.isWhiteTurn);
        for(int i = 0; i < moves.getSize(); i++){
            String dest = printMoveFromTo(moves.getMoves()[i]);
//            if(dest.equals("g2f1")){
//                Move.printMove(moves.getMoves()[i]);
//            }

            whiteKingCastle = board.isWhiteKingCastle();
            blackKingCastle = board.isBlackKingCastle();
            whiteQueenCastle = board.isWhiteQueenCastle();
            blackQueenCastle = board.isBlackQueenCastle();

            board.makeMove(moves.getMoves()[i]);

            int cnt = DEBUG_count_nodes(board, depth - 1, show);
            if(depth == show){
                System.out.println(dest + " : " + (cnt));
            }


            count += cnt;
            board.undoMove(moves.getMoves()[i]);

            board.setWhiteKingCastle(whiteKingCastle);
            board.setBlackKingCastle(blackKingCastle);
            board.setWhiteQueenCastle(whiteQueenCastle);
            board.setBlackQueenCastle(blackQueenCastle);
        }
        return count;
    }



    public static void print_count_nodes(BitBoard board, int depth){
        //base case
        if(depth == 1) {
            int[] generated = MoveGenerator.generateMoves(board, board.isWhiteTurn).getMoves();
            for(int i = 0; i< MoveGenerator.generateMoves(board, board.isWhiteTurn).getSize(); i++){
                Move.printMove(generated[i]);
            }
        }
    }


    public static String printMoveFromTo(int move){
        int from = Move.getFrom(move);
        int to = Move.getTo(move);
        int colFrom = 7 - (from % 8);
        int rowFrom = 1 + (from / 8);
        int colTo = 7 - (to % 8);
        int rowTo = 1+ (to / 8);
        char colFromChar = (char) (colFrom + 'a');
        char colToChar = (char) (colTo + 'a');
        char rowFromChar = (char) (rowFrom + '0');
        char rowToChar = (char) (rowTo + '0');
        return "" + colFromChar + rowFromChar + colToChar + rowToChar;
    }
}
