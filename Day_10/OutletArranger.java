import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class OutletArranger {
    public static void main(String[] args) {
        ArrayList<Integer> joltages = readInput(args[0]);
        System.out.println(
            "The count of joltage differences = 1 multiplied by the count " +
            "of joltage differences = 3 is: " + getJoltageDifferenceOneAndThreeProduct(joltages));
        var pathsFromOutlet = initializePathsFromOutlet(joltages);
        System.out.println(
            "The number of distinct adapter arrangements is: " 
            + getPathCountFromCurrentOutlet(joltages, 0, pathsFromOutlet));
    }
    private static ArrayList<Integer> readInput(String fileName) {
        ArrayList<Integer> fileData = new ArrayList<Integer>();
        try {
            File inFile = new File(fileName);
            Scanner fileScanner = new Scanner(inFile);
            while (fileScanner.hasNext()) {
                fileData.add(Integer.parseInt(fileScanner.nextLine()));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Exiting due to exception");
            e.printStackTrace();
            System.exit(0);
        }
        fileData.add(0);
        Collections.sort(fileData);
        return fileData;
    }
    private static int getJoltageDifferenceOneAndThreeProduct(ArrayList<Integer> joltages) {
        HashMap<Integer, Integer> joltageDifferences = new HashMap<Integer, Integer>();
        joltageDifferences.put(1, 0);
        joltageDifferences.put(2, 0);
        joltageDifferences.put(3, 1);
        for (int i = 1; i < joltages.size(); i ++) {
            int joltageDiff = joltages.get(i) - joltages.get(i - 1);
            assert joltageDiff <= 3 : "Gap in joltages between adapters to large";
            joltageDifferences.put(joltageDiff, joltageDifferences.get(joltageDiff) + 1);
        }
        return joltageDifferences.get(1) * joltageDifferences.get(3);
    }
    private static HashMap<Integer, Long> initializePathsFromOutlet(ArrayList<Integer> joltages) {
        HashMap<Integer, Long> pathsFromOutlet = new HashMap<Integer, Long>();
        pathsFromOutlet.put(joltages.get(joltages.size() - 1), (long) 1);
        return pathsFromOutlet;
    }
    private static long getPathCountFromCurrentOutlet(
            ArrayList<Integer> joltages, int start, HashMap<Integer, Long> pathsFromOutlet) {
        int currentOutletJoltage = joltages.get(start);
        long pathCountFromCurrentOutlet = 0;
        for (int i = 1; i <= 3 && start + i < joltages.size(); i ++) {
            int targetOutletJoltage = joltages.get(start + i);
            if (targetOutletJoltage <= currentOutletJoltage + 3) {
                if (pathsFromOutlet.containsKey(targetOutletJoltage)) {
                    pathCountFromCurrentOutlet += pathsFromOutlet.get(targetOutletJoltage);
                } else {
                    pathCountFromCurrentOutlet +=
                        getPathCountFromCurrentOutlet(joltages, start + i, pathsFromOutlet);
                }
            }
        }
        pathsFromOutlet.put(currentOutletJoltage, pathCountFromCurrentOutlet);
        return pathCountFromCurrentOutlet;
    }
}