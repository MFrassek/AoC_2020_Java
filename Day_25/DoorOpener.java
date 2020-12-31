import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DoorOpener {
    public static void main(String[] args) {
        long[] data = readData(args[0]);
        long cardResult = data[0];
        int cardLoopSize = getCardLoopSize(cardResult);
        long doorResult = data[1];
        System.out.println("Encryption key: "
            + getEncryptionKey(doorResult, cardLoopSize));
    }
    private static long[] readData(String fileName) {
        long[] data = new long[2];
        try {
            File inFile = new File(fileName);
            Scanner inScanner = new Scanner(inFile);
            data[0] = inScanner.nextLong();
            data[1] = inScanner.nextLong();
            inScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Exiting due to Exception");
            e.printStackTrace();
            System.exit(0);
        }
        return data;
    }
    private static int getCardLoopSize(long cardResult) {
        int cardLoopSize = 1;
        long cardTempVal = 7L;
        while (cardTempVal != cardResult) {
            cardLoopSize ++;
            cardTempVal = (cardTempVal * 7) % 20201227;
        }
        return cardLoopSize;        
    }
    private static long getEncryptionKey(long doorResult, int cardLoopSize) {
        long result = 1;
        for (int i = 0; i < cardLoopSize; i ++) {
            result *= doorResult;
            result %= 20201227;
        }
        return result;
    }
}
