import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Collections;

public class SeatFinder {
    public static void main(String[] args) {
        HashSet<Integer> seatIds = readInSeatIds(args[0]);
        System.out.println("Highest seat number: " + findMaxSeatId(seatIds));
        System.out.println("Missing seat number: " + findMissingSeatId(seatIds));        
    }
    private static HashSet<Integer> readInSeatIds(String fileName) {
        HashSet<Integer> seatIds = new HashSet<Integer>(); 
        try {
            File inFile = new File(fileName);
            Scanner fileScanner = new Scanner(inFile);
            while (fileScanner.hasNext()) {
                String binarySeatCode = makeSeatCodeBinary(fileScanner.nextLine());
                seatIds.add(Integer.parseInt(binarySeatCode, 2));
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return seatIds;
    }
    private static String makeSeatCodeBinary(String seatCode) {
        String binarySeatCode = seatCode.replace('F', '0')
            .replace('B', '1').replace('L', '0').replace('R', '1');
        return binarySeatCode;
    }
    private static int findMaxSeatId(HashSet<Integer> seatIds) {
        return Collections.max(seatIds);
    }
    private static int findMissingSeatId(HashSet<Integer> seatIds) {
        int minId = Collections.min(seatIds);
        int maxId = Collections.max(seatIds);
        for (int i = minId; i <= maxId; i ++) {
            if (!seatIds.contains(i)) {
                return i;
            }
        }
        return -1;
    }
}