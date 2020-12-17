import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;

public class ConsoleChecker {
    public static void main(String[] args) {
        ArrayList<String> instructions = readFileData(args[0]);
        System.out.println("acc value at first looping-back: " 
            + visitAllInstructionsUntilFirstLoop(instructions));
        System.out.println("acc value after successful run of fixed instructions: "
            + fixCorruptedNopOrJmpPosToBreakOutOfLoop(instructions));
    }
    private static ArrayList<String> readFileData(String filePath) {
        ArrayList<String> fileData = new ArrayList<String>();
        try {
            File inFile = new File(filePath);
            Scanner fileScanner = new Scanner(inFile);
            while (fileScanner.hasNext()) {
                fileData.add((String) fileScanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Exiting due to exception");
            e.printStackTrace();
            System.exit(0);
        }
        return fileData;
    }
    private static int visitAllInstructionsUntilFirstLoop(
            ArrayList<String> instructions) {
        HashSet<Integer> visitedLines = new HashSet<Integer>();
        HashMap<String, Integer> currentValues = initializeCurrentValuesHashMap();
        while (!visitedLines.contains(currentValues.get("pos"))) {
            String instruction = instructions.get(currentValues.get("pos"));
            visitedLines.add(currentValues.get("pos"));
            executeInstruction(instruction, currentValues);
        }
        return currentValues.get("acc");
    }
    private static int fixCorruptedNopOrJmpPosToBreakOutOfLoop(
            ArrayList<String> instructions) {
        HashSet<Integer> visitedLines = new HashSet<Integer>();
        HashMap<String, Integer> currentValues = initializeCurrentValuesHashMap();     
        while (!visitedLines.contains(currentValues.get("pos"))) {
            visitedLines.add(currentValues.get("pos"));
            String instruction = instructions.get(currentValues.get("pos"));
            if (instruction.startsWith("acc")) {
                executeInstruction(instruction, currentValues);
            } else {
                int accValueAtEnd = 
                    accValueIfChangeEndsLoop(instructions, currentValues, visitedLines);
                if (accValueAtEnd != -1) {
                    return accValueAtEnd;
                } else {
                    executeInstruction(instruction, currentValues);
                }
            }
        }
        return -1;
    }
    private static HashMap<String, Integer> initializeCurrentValuesHashMap() {
        HashMap<String, Integer> currentValues = new HashMap<String, Integer>();
        currentValues.put("acc", 0);
        currentValues.put("pos", 0);
        return currentValues;        
    }
    private static void executeInstruction(
            String instruction, HashMap<String, Integer> currentValues) {
        if (instruction.startsWith("nop")) {
            currentValues.put("pos", currentValues.get("pos") + 1);
        } else if (instruction.startsWith("acc")) {
            currentValues.put("pos", currentValues.get("pos") + 1);
            currentValues.put("acc", currentValues.get("acc") 
                + Integer.parseInt(instruction.split(" ")[1]));
        } else if (instruction.startsWith("jmp")) {
            currentValues.put("pos", currentValues.get("pos") 
                + Integer.parseInt(instruction.split(" ")[1]));
        }
    }
    private static int accValueIfChangeEndsLoop(
            ArrayList<String> instructions, HashMap<String, Integer> currentValues,
            HashSet<Integer> visitedLines) {
        ArrayList<String> modInstructions = 
            makeModifiedInstructions(instructions, currentValues);
        HashMap<String, Integer> copiedCurrentValues = 
            (HashMap<String, Integer>) currentValues.clone();
        HashSet<Integer> copiedVisitedLines = (HashSet<Integer>) visitedLines.clone();
        return getResultAtEndOfInstructions(
                modInstructions, copiedVisitedLines, copiedCurrentValues);        
    }
    private static ArrayList<String> makeModifiedInstructions(
            ArrayList<String> instructions, HashMap<String, Integer> currentValues) {
        ArrayList<String> modInstructions = (ArrayList<String>) instructions.clone();
        String instruction = instructions.get(currentValues.get("pos"));
        String modInstruction;
        if (instruction.startsWith("nop")) {
            modInstruction = instruction.replace("nop", "jmp");
        } else {
            modInstruction = instruction.replace("jmp", "nop");
        }
        modInstructions.set(currentValues.get("pos"), modInstruction);
        return modInstructions;
    }
    private static int getResultAtEndOfInstructions(
            ArrayList<String> instructions, HashSet<Integer> visitedLines,
            HashMap<String, Integer> currentValues) {
        try {
            do {
                String instruction = instructions.get(currentValues.get("pos"));
                visitedLines.add(currentValues.get("pos"));
                executeInstruction(instruction, currentValues);
            } while (!visitedLines.contains(currentValues.get("pos")));
            return -1;
        } catch (IndexOutOfBoundsException e) {
            return currentValues.get("acc");
        }
    }
}