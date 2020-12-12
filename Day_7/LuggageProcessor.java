import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

public class LuggageProcessor {
    public static void main(String[] args) {
        ArrayList<String> rules = readInRules(args[0]);
        String bagColor = "shiny gold";
        System.out.println("Number of bags that can eventually contain a " 
            + bagColor + " bag: " + countAllAncestors(bagColor, rules));
        System.out.println("Number of bags that are contained by a " 
            + bagColor + " bag: " + countAllDescendents(bagColor, rules));
    }
    private static ArrayList<String> readInRules(String fileName) {
        ArrayList<String> rules = new ArrayList<String>();
        try {
            File inFile = new File(fileName);
            Scanner fileScanner = new Scanner(inFile);
            while (fileScanner.hasNext()) {
                rules.add(fileScanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return rules;
    }
    private static Integer countAllAncestors(String bag, ArrayList<String> rules) {
        HashMap<String, HashSet<String>> containedInMap = getContainedInMap(rules);
        HashSet<String> allAncestorsSet = new HashSet<String>();
        addAncestorsToHashset(containedInMap, bag, allAncestorsSet);
        return allAncestorsSet.size();
    }
    private static Integer countAllDescendents(String bag, ArrayList<String> rules) {
        HashMap<String, HashMap<String, Integer>> containsMap = getContainsMap(rules);
        return countDescendents(containsMap, bag);        
    }

    private static HashMap<String, HashSet<String>> getContainedInMap(ArrayList<String> rules) {
        HashMap<String, HashSet<String>> containedInMap = 
            new HashMap<String, HashSet<String>>();
        for (String rule : rules) {
            String[] splitRule = rule.substring(0, rule.length() - 1).split(" contain ");
            String containerBagType = getContainerBagTypeFromInfo(splitRule[0]);
            String[] containedBagsInfo = splitRule[1].split(", ");
            for (String containedBagInfo : containedBagsInfo) {
                String containedBagType = getContainedBagTypeFromInfo(containedBagInfo);
                if (containedInMap.containsKey(containedBagType)) {
                    containedInMap.get(containedBagType).add(containerBagType);
                } else {
                    HashSet<String> containedInSet = new HashSet<String>();
                    containedInSet.add(containerBagType);
                    containedInMap.put(containedBagType, containedInSet);
                }
            }
        }
        return containedInMap;
    }
    private static HashMap<String, HashMap<String, Integer>> getContainsMap(ArrayList<String> rules) {
        HashMap<String, HashMap<String, Integer>> containsMap = 
            new HashMap<String, HashMap<String, Integer>>();
        for (String rule : rules) {
            String[] splitRule = rule.substring(0, rule.length() - 1).split(" contain ");
            String containerBagType = getContainerBagTypeFromInfo(splitRule[0]);
            String[] containedBagsInfo = splitRule[1].split(", ");
            for (String containedBagInfo : containedBagsInfo) {
                Integer containedBagCount = getContainedBagCountFromInfo(containedBagInfo);
                String containedBagType = getContainedBagTypeFromInfo(containedBagInfo);
                if (containsMap.containsKey(containerBagType)) {
                    containsMap.get(containerBagType).put(containedBagType, containedBagCount);
                } else {
                    HashMap<String, Integer> containedMap = new HashMap<String, Integer>();
                    containedMap.put(containedBagType, containedBagCount);
                    containsMap.put(containerBagType, containedMap);
                }
            }
        }
        return containsMap;
    }
    private static void addAncestorsToHashset(
            HashMap<String, HashSet<String>> containedInMap, String containedBagType, HashSet<String> allAncestorsSet) {
        if (containedInMap.containsKey(containedBagType)) {
            HashSet<String> containedInSet = containedInMap.get(containedBagType);
            containedInSet.forEach((containerBagType) -> {
                allAncestorsSet.add(containerBagType);
                addAncestorsToHashset(containedInMap, containerBagType, allAncestorsSet);
            });
        }    
    }
    private static Integer countDescendents(
            HashMap<String, HashMap<String, Integer>> containsMap, String containerBagType) {
        int descendents = 0;
        if (containsMap.containsKey(containerBagType)) {
            HashMap<String, Integer> containedMap = containsMap.get(containerBagType);
            for (String containedBagType : containedMap.keySet()) {
                int containedBagCount = containedMap.get(containedBagType);
                descendents += containedBagCount;
                descendents += containedBagCount * countDescendents(containsMap, containedBagType);
            }
        }
        return descendents;
    }

    private static String getContainerBagTypeFromInfo(String bagInfo) {
        String[] bagInfoArray = bagInfo.split(" ");
        StringBuilder bagTypeBuilder = new StringBuilder();
        bagTypeBuilder.append(bagInfoArray[0]);
        bagTypeBuilder.append(" ");
        bagTypeBuilder.append(bagInfoArray[1]);
        return bagTypeBuilder.toString();
    }
    private static String getContainedBagTypeFromInfo(String bagInfo) {
        String[] bagInfoArray = bagInfo.split(" ");
        StringBuilder bagTypeBuilder = new StringBuilder();
        bagTypeBuilder.append(bagInfoArray[1]);
        bagTypeBuilder.append(" ");
        bagTypeBuilder.append(bagInfoArray[2]);
        return bagTypeBuilder.toString();
    }
    private static Integer getContainedBagCountFromInfo(String bagInfo) {
        String[] bagInfoArray = bagInfo.split(" ");
        if (bagInfoArray[0].equals("no")) {
            return 0;
        } else {
            return Integer.parseInt(bagInfoArray[0]);
        }
    }
}