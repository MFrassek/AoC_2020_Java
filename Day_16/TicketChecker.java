import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TicketChecker {
    public static void main(String[] args) {
        DataContainer dataContainer = readInData(args[0]);
        HashMap<String, HashSet<Integer>> fieldNamesToValidValues = 
            dataContainer.getFieldNamesToValidValues();
        HashSet<Integer> allValidValues = getAllValidValues(fieldNamesToValidValues);
        ArrayList<ArrayList<Integer>> nearbyTickets = dataContainer.getNearbyTickets();
        System.out.println("Ticket scanning error rate: " 
            + getTicketScanningErrorRate(nearbyTickets, allValidValues));
        ArrayList<ArrayList<Integer>> cleanTickets = cleanUpTickets(nearbyTickets, allValidValues);
        HashMap<Integer, HashSet<Integer>> ticketPositionsToUsedValues = 
            getTicketPositionsToUsedValuesMap(cleanTickets);
        HashMap<Integer, HashSet<String>> intersectionsFullLengethed2D = 
            getFullLengthedIntersections2D(ticketPositionsToUsedValues, fieldNamesToValidValues);
        HashMap<String, Integer> fieldNameToTicketPosition = 
            getFieldNameToTicketPosition(intersectionsFullLengethed2D);
        System.out.println("Product of the values in fields beginning with 'departure': "
            + calculateProductOfDepartureFields(
                fieldNameToTicketPosition, dataContainer.getMyTicket()));
    }
    private static DataContainer readInData(String fileName) {
        try {
            File inFile = new File(fileName);
            Scanner inScanner = new Scanner(inFile);
            return new DataContainer(inScanner);
        } catch (FileNotFoundException e) {
            System.out.println("Exiting due to exception");
            e.printStackTrace();
            System.exit(0);
        }
        return new DataContainer();
    }
    private static HashSet<Integer> getAllValidValues(
            HashMap<String, HashSet<Integer>> fieldNamesToValidValues) {
        HashSet<Integer> allValidValues = new HashSet<Integer>();
        for (String key : fieldNamesToValidValues.keySet()) {
            allValidValues.addAll(fieldNamesToValidValues.get(key));
        }
        return allValidValues;
    }
    private static int getTicketScanningErrorRate(
            ArrayList<ArrayList<Integer>> nearbyTickets,
            HashSet<Integer> allValidValues) {
        int result = 0;
        for (ArrayList<Integer> ticket : nearbyTickets) {
            for (int field : ticket) {
                if (!allValidValues.contains(field)) {
                    result += field;
                }
            }
        }
        return result;        
    }
    private static ArrayList<ArrayList<Integer>> cleanUpTickets(
            ArrayList<ArrayList<Integer>> nearbyTickets,
            HashSet<Integer> withinAnyRange) {
        var cleanTickets = new ArrayList<ArrayList<Integer>>();
        for (ArrayList<Integer> ticket : nearbyTickets) {
            if (isValid(ticket, withinAnyRange)) {
                cleanTickets.add(ticket);
            }
        }
        return cleanTickets;
    }
    private static HashMap<Integer, HashSet<Integer>> getTicketPositionsToUsedValuesMap(
            ArrayList<ArrayList<Integer>> cleanTickets){
        var ticketPositionsToUsedValues = new HashMap<Integer, HashSet<Integer>>();
        for (int i = 0; i < cleanTickets.get(0).size();  i++) {
            HashSet<Integer> usedValues = new HashSet<Integer>();
            for (ArrayList<Integer> ticket : cleanTickets) {
                usedValues.add(ticket.get(i));
            }
            ticketPositionsToUsedValues.put(i, usedValues);
        }
        return ticketPositionsToUsedValues;
    }
    private static HashMap<Integer, HashSet<String>> getFullLengthedIntersections2D(
            HashMap<Integer, HashSet<Integer>> ticketPositionsToUsedValues,
            HashMap<String, HashSet<Integer>> fieldNamesToValidValues){
        var intersectionsFullLengethed2D = new HashMap<Integer, HashSet<String>>();
        for (int ticketPosition : ticketPositionsToUsedValues.keySet()) {
            var intersectionsFullLengethed1D = new HashSet<String>();
            for (String fieldName : fieldNamesToValidValues.keySet()) {
                HashSet<Integer> usedValues = (HashSet<Integer>) 
                    ticketPositionsToUsedValues.get(ticketPosition).clone();
                usedValues.retainAll(fieldNamesToValidValues.get(fieldName));
                if (usedValues.size() == ticketPositionsToUsedValues.get(ticketPosition).size()) {
                    intersectionsFullLengethed1D.add(fieldName);
                }
            }
            intersectionsFullLengethed2D.put(ticketPosition, intersectionsFullLengethed1D);
        }
        return intersectionsFullLengethed2D;
    }
    private static HashMap<String, Integer> getFieldNameToTicketPosition(
            HashMap<Integer, HashSet<String>> intersectionsFullLengethed2D) {
        HashMap<String, Integer> fieldNameToTicketPosition = new HashMap<String, Integer>();
        for (int i = 0; i < intersectionsFullLengethed2D.size(); i++) {
            for (int ticketPosition : intersectionsFullLengethed2D.keySet()) {
                var currentFieldNames = intersectionsFullLengethed2D.get(ticketPosition);
                if (currentFieldNames.size() == 1) {
                    String singleValidFieldName = 
                        intersectionsFullLengethed2D.get(ticketPosition).iterator().next();
                    fieldNameToTicketPosition.put(singleValidFieldName, ticketPosition);
                    removeFieldNameFromIntersetions2DHashMap(
                        intersectionsFullLengethed2D, singleValidFieldName);
                    break;
                }
            }
        }
        return fieldNameToTicketPosition;
    }
    private static long calculateProductOfDepartureFields(
            HashMap<String, Integer> fieldNameToTicketPosition,
            ArrayList<Integer> myTicket) {
        return fieldNameToTicketPosition.keySet()
            .stream()
            .filter(s -> s.startsWith("departure"))
            .map(s -> (long) myTicket.get(fieldNameToTicketPosition.get(s)))
            .reduce(1L, (a,b) -> a*b);
    }

    private static boolean isValid(
            ArrayList<Integer> ticket, HashSet<Integer> withinAnyRange) {
        for (int field : ticket) {
            if (!withinAnyRange.contains(field)) {
                return false;
            }
        }
        return true;
    }
    private static void removeFieldNameFromIntersetions2DHashMap(
            HashMap<Integer, HashSet<String>> intersections2D, String fieldName) {
        for (int ticketPosition : intersections2D.keySet()) {
            intersections2D.get(ticketPosition).remove(fieldName);
        }
    }
}

class DataContainer {
    private HashMap<String, HashSet<Integer>> fieldNamesToValidValues;
    private ArrayList<Integer> myTicket;
    private ArrayList<ArrayList<Integer>> nearbyTickets;
    
    public DataContainer() {
    }
    
    public DataContainer(Scanner inScanner) {
        fieldNamesToValidValues = readFieldNamesToValidValuesMap(inScanner);
        myTicket = readMyTicket(inScanner);
        nearbyTickets = readNearbyTickets(inScanner);
    }

    private static HashMap<String, HashSet<Integer>> readFieldNamesToValidValuesMap(
            Scanner inScanner) {
        var fieldNamesToValidValues = new HashMap<String, HashSet<Integer>>();
        while (inScanner.hasNext()) {
            String currentLine = inScanner.nextLine();
            if (currentLine.length() == 0) {
                break;
            }
            String[] splitLine = currentLine.split(": ");
            fieldNamesToValidValues.put(splitLine[0], processValidRanges(splitLine[1]));
        }
        return fieldNamesToValidValues;  
    }
    private static ArrayList<Integer> readMyTicket(Scanner inScanner) {
        if(!inScanner.nextLine().equals("your ticket:")) {
            System.out.println("Can't read my ticket from here");
            System.exit(0);
        }
        ArrayList<Integer> myTicket = readTicket(inScanner);
        inScanner.nextLine();
        return myTicket;
    }
    private static ArrayList<ArrayList<Integer>> readNearbyTickets(Scanner inScanner) {
        var nearbyTickets = new ArrayList<ArrayList<Integer>>();
        if (!inScanner.nextLine().equals("nearby tickets:")) {
            System.out.println("Can't read nearby tickets from here");
            System.exit(0);
        }
        while (inScanner.hasNext()) {
            nearbyTickets.add(readTicket(inScanner));
        }
        return nearbyTickets;
    }

    private static HashSet<Integer> processValidRanges(String validRangeSpec) {
        HashSet<Integer> validValues = new HashSet<Integer>();
        ArrayList<Integer> rangeBoundaries = (ArrayList<Integer>) Arrays
            .stream(validRangeSpec.split(" or |-"))
            .map(Integer::valueOf)
            .collect(Collectors.toList());
        for (int i = rangeBoundaries.get(0); i <= rangeBoundaries.get(1); i++) {
            validValues.add(i);
        }
        for (int i = rangeBoundaries.get(2); i <= rangeBoundaries.get(3); i++) {
            validValues.add(i);
        }
        return validValues;
    }
    private static ArrayList<Integer> readTicket(Scanner inScanner) {
        return (ArrayList<Integer>) Arrays
            .stream(inScanner.nextLine().split(","))
            .map(Integer::valueOf)
            .collect(Collectors.toList());
    }

    public HashMap<String, HashSet<Integer>> getFieldNamesToValidValues() {
        return fieldNamesToValidValues;
    }
    public ArrayList<Integer> getMyTicket() {
        return myTicket;
    }
    public ArrayList<ArrayList<Integer>> getNearbyTickets() {
        return nearbyTickets;
    }
}