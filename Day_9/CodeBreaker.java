import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;

public class CodeBreaker {
    public static void main(String[] args) {
        ArrayList<Long> fileData = readFileData(args[0]);
        Long firstRuleBreaker = getFirstRuleBreaker(fileData);
        System.out.println("First element to break the rule: " 
            + firstRuleBreaker);
        System.out.println("Encyption weakness: " 
            + getEncryptionWeakness(firstRuleBreaker, fileData));
    }
    private static ArrayList<Long> readFileData(String fileName) {
        ArrayList<Long> fileData = new ArrayList<Long>();
        try {
            File inFile = new File(fileName);
            Scanner fileScanner = new Scanner(inFile);
            while (fileScanner.hasNext()) {
                fileData.add(Long.parseLong(fileScanner.nextLine()));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Exiting due to exception");
            e.printStackTrace();
            System.exit(0);
        }
        return fileData;
    }
    private static boolean elementIsSumOfTwoElementsInArrayList(
            Long element, ArrayList<Long> precedingElements) {
        HashSet<Long> complementElements = new HashSet<Long>();
        for (Long precedingElement : precedingElements) {
            if (precedingElement - element != element 
                    && complementElements.contains(precedingElement)) {
                return true;
            }
            complementElements.add(element - precedingElement);
        }
        return false;
    }
    private static Long getFirstRuleBreaker(ArrayList<Long> fileData) {
        int preambleSize = 25;
        for (int i = 0; i < fileData.size() - preambleSize; i ++) {
            Long nextElement = fileData.get(i + preambleSize);
            ArrayList<Long> precedingElements = 
                new ArrayList<Long>(fileData.subList(i, i + preambleSize));
            if (!elementIsSumOfTwoElementsInArrayList(nextElement, precedingElements)) {
                return nextElement;
            }
        }
        return -1L;
    }
    private static Long getEncryptionWeakness(Long ruleBreaker, ArrayList<Long> fileData) {
        for (int start = 0; start < fileData.size(); start ++) {
            for (int end = start + 2; end < fileData.size(); end ++) {
                ArrayList<Long> subList = new ArrayList<Long>(fileData.subList(start, end));
                Long subListSum = subList.stream().mapToLong(i -> i).sum();
                if (subListSum.equals(ruleBreaker)) {
                    return (Long) Collections.min(subList) + Collections.max(subList);
                } else if (subListSum > ruleBreaker) {
                    break;
                }
            }
        }
        return -1L;
    }
}