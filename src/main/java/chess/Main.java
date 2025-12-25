package chess;

import chess.bitboard.BitBoard;
import chess.search.FenTranslator;
import chess.search.Perft;

import java.util.Arrays;

public class Main {
    private static void TestPerft(){
        BitBoard board = new BitBoard(10);
        String[] FEN = FenTranslator.FENS;
        long[] results = new long[FEN.length];
        double[] time_spent = new double[FEN.length];
        int depth = 6;

//        long start = System.nanoTime();
//        FenTranslator.translate("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1", board);
//        long count = Perft.count_nodes(board, depth);
//        System.out.println(count);
//        long end = System.nanoTime();
//
//        double ms = (end - start) / 1_000_000.0;
//        System.out.println("Time taken: " + ms + " ms");

        int cnt = 0;
        long start, end;
        for(String s : FEN){
            FenTranslator.translate(s, board);

            start = System.nanoTime();
            long count = Perft.count_nodes(board, depth);
            end = System.nanoTime();
            time_spent[cnt] = (end - start) / 1_000_000.0;

            results[cnt] = count;
            cnt++;
        }

        int[] expected = FenTranslator.RESULTS[depth - 1];
        int match = 0;
        for(int i = 0; i < expected.length; i++){
            if(expected[i] == results[i]){
                match ++;
            }

            //show moves
//            if(i == 12){
//                System.out.println("--------------------------");
//                FenTranslator.translate("4k2r/8/r7/R7/8/8/8/4K2R w Kk - 0 1", board);
//                Perft.DEBUG_count_nodes(board, 2);
//            }


        }

        double matchPercentage = (double) match / expected.length;
        matchPercentage *= 100;

        System.out.println("-----------------------------------------");
        System.out.println("depth   :  " + depth);
        System.out.println("results :  " + Arrays.toString(results));
        System.out.println("expected:  " + Arrays.toString(expected));
        System.out.println("time    :  " + Arrays.toString(time_spent));
        System.out.println("match   :  " + matchPercentage + "%");
        System.out.println("remain  :  " + (expected.length - match));
        System.out.println("total   :  " + expected.length );
        System.out.println("-----------------------------------------");
    }

    static void main(String[] args) {
        TestPerft();
        //PerftTesting.testing();
        //TestBoardApplication.main(args);
        //PlayerBotApplication.main(args);
        //BotVsBotApplication.main(args);
    }
}
