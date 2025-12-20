package chess;

import chess.bitboard.BitBoard;
import chess.bitboard.MoveGenerator;
import chess.search.FenTranslator;
import chess.search.Perft;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static void TestPerft(){
        BitBoard board = new BitBoard(10, 200);
        String[] FEN = FenTranslator.FENS;
        List<Integer> results = new ArrayList<>();
        int depth = 4;

        MoveGenerator moveGen = new MoveGenerator();

        int cnt = 0;
        for(String s : FEN){
            cnt++;
            System.out.println("-- Gen number: " + cnt + "--");
            FenTranslator.translate(s, board);
            int count = Perft.count_nodes(board, depth, moveGen);
            results.add(count);
        }

        int[] res =  FenTranslator.RESULTS[depth - 1];
        List<Integer> expected = new ArrayList<>(res.length);
        int match = 0;
        for(int i = 0; i < res.length; i++){
            expected.add(res[i]);
            if(res[i] == results.get(i)){
                match ++;
            }

            //show moves
            if(i == 120){
                System.out.println("--------------------------");
                FenTranslator.translate(FEN[i], board);
                Perft.DEBUG_count_nodes(board, depth, moveGen);
            }


        }

        double matchPercentage = (double) match / res.length;
        matchPercentage *= 100;

        System.out.println("-----------------------------------------");
        System.out.println("depth   :  " + depth);
        System.out.println("results :  " + results);
        System.out.println("expected:  " + expected);
        System.out.println("match   :  " + matchPercentage + "%");
        System.out.println("remain  :  " + (res.length - match));
        System.out.println("total   :  " + res.length );
        System.out.println("-----------------------------------------");
    }

    public static void main(String[] args) {
        TestPerft();
        //TestBoardApplication.main(args);
        //PlayerBotApplication.main(args);
    }
}
