import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.ArrayList;

public class ExpenseReport {
    public static void main(String[] args) {
        int wantedSum = 2020;
        Integer[] originalList = readExpensesFile(args[0]);
        System.out.println("The product of two elements adding up to " 
            + wantedSum + " is: " + getProductOfTwoElementsAddingUpToWantedSum(
            wantedSum, originalList));
        System.out.println("The product of three elements adding up to " 
            + wantedSum + " is: " + getProductOfThreeElementAddingUpToWantedSum(
                wantedSum, originalList));

    }
    private static Integer[] readExpensesFile(String fileName) {
        ArrayList<Integer> dataRead = new ArrayList<Integer>();
        try {
            File inFile = new File(fileName);
            Scanner fileScanner = new Scanner(inFile);
            while (fileScanner.hasNext()) {
                dataRead.add(fileScanner.nextInt());
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Integer[] fileData = new Integer[dataRead.size()];
        for (int i = 0; i < dataRead.size(); i ++) {
            fileData[i] = dataRead.get(i);
        }
        return fileData;
    }
    private static int getProductOfThreeElementAddingUpToWantedSum(
            int wantedSum, Integer[] originalList) {
        for (int i  = 0; i < originalList.length; i ++) {
            int firstElement = originalList[i];
            int subSum = wantedSum - firstElement;
            int productForSubSum = getProductOfTwoElementsAddingUpToWantedSum(
                subSum, originalList);
            if (productForSubSum >= 0) {
                return firstElement * productForSubSum;
            }
        }
        return -1;
    }
    private static int getProductOfTwoElementsAddingUpToWantedSum(
            int wantedSum, Integer[] originalList) {
        HashSet<Integer> complementSet = makeComplementSet(wantedSum, originalList);
        for (int i = 0; i < originalList.length; i ++) {
            if (complementSet.contains(originalList[i])) {
                return (wantedSum - originalList[i]) * originalList[i];
            }
        }
        return -1;
    }
    private static HashSet<Integer> makeComplementSet(
            int wantedSum, Integer[] originalList) {
        HashSet<Integer> complementSet = new HashSet<Integer>();
        for (int element : originalList) {
            complementSet.add(wantedSum - element);
        }
        return complementSet;
    }

}