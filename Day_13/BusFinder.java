import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Collections;

public class BusFinder {
    public static void main(String[] args) {
        ArrayList<Object> busData = readInData(args[0]);
        System.out.println("Product of waiting time and bus id for earliest bus: "
            + getProductOfWaitingTimeAndBusIdForEarliestBus(busData));
        System.out.println("Earliest time point with respective pattern of bus arrivals: "
            + getEarliestStartTimeOfConstestBusArrivalPattern(busData));
    }
    private static ArrayList<Object> readInData(String fileName) {
        ArrayList<Object> inData = new ArrayList<Object>();
        try {
            File inFile = new File(fileName);
            Scanner inScanner = new Scanner(inFile);
            inData.add(Integer.parseInt(inScanner.nextLine()));
            inData.add(inScanner.nextLine().split(","));
            inScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Exiting due to Exception:");
            e.printStackTrace();
            System.exit(0);
        }
        return inData;
    }
    private static int getProductOfWaitingTimeAndBusIdForEarliestBus(
            ArrayList<Object> busData) {
        int earliestDeparture = (int) busData.get(0);
        List<Integer> filteredBusNames = filterBusNames(busData);
        int minWaitingTime = Integer.MAX_VALUE;
        int result = -1;
        for (int busName : filteredBusNames) {
            int waitingTime = busName - (earliestDeparture % busName);
            if (waitingTime < minWaitingTime) {
                minWaitingTime = waitingTime;
                result = waitingTime * busName;
            }
        }
        return result;        
    }
    private static List<Integer> filterBusNames(ArrayList<Object> busData) {
        return Arrays.stream((String[]) busData.get(1))
                .filter(s -> s.matches("\\d+"))
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }
    private static long getEarliestStartTimeOfConstestBusArrivalPattern(
            ArrayList<Object> busData) {
        HashMap<Integer, Integer> busToOffset = getBusToOffsetHashMap(busData);
        long total = 0;
        for (int key : busToOffset.keySet()) {
            long sectionValue = busToOffset
                .keySet()
                .stream()
                .mapToLong(num -> (long) num).filter(num -> num != key)
                .reduce(1, (subproduct, element) -> subproduct * element);
            if (sectionValue % key != busToOffset.get(key)) {
                sectionValue *= findMultiplicantForModEqualsX(
                    sectionValue % key, key, busToOffset.get(key));
            }
            total += sectionValue;
        }
        long lcm = getLcmOfCoprimeKeys(busToOffset);
        return lcm - total % lcm;
    }
    private static HashMap<Integer, Integer> getBusToOffsetHashMap(
            ArrayList<Object> busData) {
        String[] busNames = (String[]) busData.get(1);
        HashMap<Integer, Integer> busToOffset = new HashMap<Integer, Integer>();
        for (int i = 0; i < busNames.length; i ++) {
            if (!busNames[i].equals("x")) {
                busToOffset.put(Integer.parseInt(busNames[i]), i);
            }
        }
        return busToOffset;        
    }
    private static long findMultiplicantForModEqualsX(
            long currentNumber, long moduloNumber, long target) {
        for (int i = 1; i <= moduloNumber; i ++) {
            if ((currentNumber * i - target) % moduloNumber == 0) {
                return i;
            }
        }
        return -1;
    }
    private static long getLcmOfCoprimeKeys(HashMap<Integer, Integer> busToOffset) {
        return busToOffset
            .keySet()
            .stream()
            .mapToLong(num -> (long) num)
            .reduce(1, (subproduct, element) -> subproduct * element);
    }
}