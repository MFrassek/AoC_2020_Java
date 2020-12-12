import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;

public class CustomsDeclarer {
    public static void main(String[] args) {
        ArrayList<String> customItems = readInData(args[0]);
        System.out.println("Total count of customs appearing any time per group: "
            + getTotalCountOfCustomsAppearingAnyTimePerGroup(customItems));
        System.out.println("Total count of customs appearing all the time per group: "
            + getTotalCountOfCustomsAppearingAllTimePerGroup(customItems));

    }
    private static ArrayList<String> readInData(String fileName) {
        ArrayList<String> dataRead = new ArrayList<String>();
        try {
            File inFile = new File(fileName);
            Scanner fileScanner = new Scanner(inFile);
            while (fileScanner.hasNext()) {
                dataRead.add(fileScanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return dataRead;
    }
    private static int getTotalCountOfCustomsAppearingAnyTimePerGroup(ArrayList<String> allItems) {
        int totalCount = 0;
        HashSet<String> customsInGroup = new HashSet<String>();
        for (String personalItems : allItems) {
            if (personalItems.length() > 0) {
                HashSet<String> customsForPerson = 
                    new HashSet<String>(Arrays.asList(personalItems.split("")));
                customsInGroup.addAll(customsForPerson);
            } else {
                totalCount += customsInGroup.size();
                customsInGroup = new HashSet<String>();
            }
        }
        totalCount += customsInGroup.size();
        return totalCount;
    }
    private static int getTotalCountOfCustomsAppearingAllTimePerGroup(ArrayList<String> allItems) {
        int totalCount = 0;
        HashSet<String> customsInGroup = makeAllCustomsHashSet();
        for (String personalItems : allItems) {
            if (personalItems.length() > 0) {
                HashSet<String> customsForPerson = 
                    new HashSet<String>(Arrays.asList(personalItems.split("")));
                customsInGroup.retainAll(customsForPerson);
            } else {
                totalCount += customsInGroup.size();
                customsInGroup = makeAllCustomsHashSet();
            }
        }
        totalCount += customsInGroup.size();
        return totalCount;
    }
    private static HashSet<String> makeAllCustomsHashSet() {
        HashSet<String> allCustoms = new HashSet<String>(Arrays.asList("abcdefghijklmnopqrstuvwxyz".split("")));
        return allCustoms;
    }
}