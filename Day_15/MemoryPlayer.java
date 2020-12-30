import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.List;
import java.util.HashMap;

public class MemoryPlayer {
    public static void main(String[] args) 
            throws FileNotFoundException {
        List<Integer> startingNumbers = readInData(args[0]);
        System.out.println("2,020th number said: "
            + findNumberAtDesiredPosition(2020, startingNumbers));
        System.out.println("30,000,000th number said: "
            + findNumberAtDesiredPosition(30000000, startingNumbers));
    }
    private static List<Integer> readInData(String fileName) 
            throws FileNotFoundException{
        File inFile = new File(fileName);
        Scanner inScanner = new Scanner(inFile);
        return Arrays.stream(inScanner.nextLine().split(","))
            .map(Integer::valueOf)
            .collect(Collectors.toList());

    }
    private static int findNumberAtDesiredPosition(
            int desiredPosition, List<Integer> startingNumbers) {
        HashMap<Integer, Integer> lastSaidAt = new HashMap<Integer, Integer>();
        int i = 0;
        for (; i < startingNumbers.size() - 1; i++) {
            lastSaidAt.put(startingNumbers.get(i), i);
        }
        int currentNumber = startingNumbers.get(startingNumbers.size() - 1);
        for (; i < desiredPosition - 1; i++) {
            int nextNumber = lastSaidAt.containsKey(currentNumber) ? i - lastSaidAt.get(currentNumber) : 0;
            lastSaidAt.put(currentNumber, i);
            currentNumber = nextNumber;
        }
        return currentNumber;        
    }
}