import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class TreeCounter {
    public static void main(String[] args) {
        ArrayList<String[]> fileData = getFileData(args[0]);
        System.out.println("Trees encountered on slope (3,1): " 
            + (long) countTreesOnSlope(fileData, 3, 1)); 
        System.out.println("Product of trees encountered on slopes " 
            + "(1,1), (3,1), (5,1), (7,1) and (1,2): " 
            + (long) countTreesOnSlope(fileData, 1, 1) 
                * countTreesOnSlope(fileData, 3, 1) * countTreesOnSlope(fileData, 5, 1) 
                * countTreesOnSlope(fileData, 7, 1) * countTreesOnSlope(fileData, 1, 2));
    }
    private static ArrayList<String[]> getFileData(String fileName) {
        ArrayList<String[]> fileData = new ArrayList<String[]>();
        try {
            File inFile = new File(fileName);
            Scanner fileScanner = new Scanner(inFile);
            while (fileScanner.hasNext()) {
                fileData.add(fileScanner.nextLine().split(""));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileData;  
    }
    private static int countTreesOnSlope(ArrayList<String[]> fileData, int right, int down) {
        int counter = 0;
        int currentPos = 0;
        for (int i = 0; i < fileData.size(); i += down) {
            String[] currentLine = fileData.get(i);
            currentPos %= currentLine.length;
            if (currentLine[currentPos].equals("#")) {
                counter ++;
            }
            currentPos += right;
        }      
        return counter;
    }
}