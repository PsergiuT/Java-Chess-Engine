package chess.search.testing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PerftTesting {
    public static void testing(){
        Map<String, Integer> mapExpected = new HashMap<>();
        Map<String, Integer> mapResults = new HashMap<>();

        try  {
            BufferedReader brExpected = new BufferedReader(new FileReader("D:\\Chess\\Algorithms\\Chess_Bot\\src\\main\\java\\chess\\search\\files\\expected.csv"));
            BufferedReader brResults = new BufferedReader(new FileReader("D:\\Chess\\Algorithms\\Chess_Bot\\src\\main\\java\\chess\\search\\files\\results.csv"));

            String line;
            while ((line = brExpected.readLine()) != null) {
                String[] split = line.split(":");
                mapExpected.put(split[0].strip(), Integer.parseInt(split[1].strip()));
            }

            while ((line = brResults.readLine()) != null) {
                String[] split = line.split(":");
                mapResults.put(split[0].strip(), Integer.parseInt(split[1].strip()));
            }

            System.out.println("Dest : Expected , Result");

            for(String key : mapExpected.keySet()){
                for(String key2 : mapResults.keySet()){
                    if(key.equals(key2)){
                        System.out.println(key + " -> |" + mapExpected.get(key) + " , " + mapResults.get(key) + "| Dif: " + (mapExpected.get(key) - mapResults.get(key)));
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
