package MoveGenerator.utils;

public class Debugging {
    private static void printMask(long mask){
        System.out.println(" ");

        for(int i = 0; i < 8; i++){
            System.out.print("R" + (i + 1) + ": ");
            for(int j = 0; j < 8; j++){
                long piece = mask & 0x8000000000000000L;
                System.out.print( piece != 0 ? "X " : "_ ");
                mask = mask << 1;
            }
            System.out.println(" ");
        }

        System.out.println(" ");
    }

    private static void printWithMessage(String message, long mask){
        System.out.println("-----------------------------");
        System.out.println(message);
        System.out.println("-----------------------------");
        printMask(mask);
    }
}
