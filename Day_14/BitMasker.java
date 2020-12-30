import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BitMasker {
    public static void main(String[] args) {
        ArrayList<String> commands = readInData(args[0]);
        System.out.println("Sum of all values left in memory: " 
            + getTotalMaskedValue(commands));
        System.out.println("Sum of all values left in memory when floating bits are used: " 
            + getTotalMaskedMemoryValue(commands));
    }
    private static ArrayList<String> readInData(String fileName) {
        ArrayList<String> commands = new ArrayList<String>();
        try {
            File inFile = new File(fileName);
            Scanner inScanner = new Scanner(inFile);
            while (inScanner.hasNext()) {
                commands.add(inScanner.nextLine());
            }
            inScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Exiting due to Exception:");
            e.printStackTrace();
            System.exit(0);
        }
        return commands;
    }
    private static long getTotalMaskedValue(ArrayList<String> commands) {
        HashMap<Long, Long> valuesAtMemoryPositions = new HashMap<Long, Long>();
        long[] masks = new long[3];
        for (String command : commands) {
            if (command.startsWith("mask")) {
                masks = getMasks(command);
            } else if (command.startsWith("mem")) {
                storeMaskedValueInMemory(command, valuesAtMemoryPositions, masks);
            }
        }        
        return getTotalValueFromMap(valuesAtMemoryPositions);
    }
    private static long getTotalMaskedMemoryValue(ArrayList<String> commands) {
        HashMap<Long, Long> valuesAtMemoryPositions = new HashMap<Long, Long>();
        long[] masks = new long[3];
        for (String command : commands) {
            if (command.startsWith("mask")) {
                masks = getMasks(command);
            } else if (command.startsWith("mem")) {
                storeValueInMaskedMemory(command, valuesAtMemoryPositions, masks);
            }
        }        
        return getTotalValueFromMap(valuesAtMemoryPositions);
    }
    private static long[] getMasks(String command) {
        long[] masks = new long[3];
        String maskPart = command.split(" = ")[1];
        long zeroMask = 0;
        long oneMask = 0;
        long xMask = 0;
        for (String element : maskPart.split("")) {
            zeroMask <<= 1;
            oneMask <<= 1;
            xMask <<= 1;
            if (element.equals("0")) {
                zeroMask |= 1;
            } else if (element.equals("1")) {
                oneMask |= 1;
            } else if (element.equals("X")) {
                xMask |= 1;
            }
        }
        masks[0] = zeroMask;
        masks[1] = oneMask;
        masks[2] = xMask;
        return masks;
    }
    private static long getTotalValueFromMap(HashMap<Long, Long> valuesAtMemoryPositions) {
        long total = 0;
        for (long key : valuesAtMemoryPositions.keySet()) {
            total += valuesAtMemoryPositions.get(key);
        }
        return total;
    }
    private static void storeMaskedValueInMemory(
            String command, HashMap<Long, Long> valuesAtMemoryPositions,
            long[] masks) {
        String[] splitCommand = command.split(" = ");
        long memoryPosition = Integer.parseInt(splitCommand[0].split("[\\[\\]]")[1]);
        long value = Integer.parseInt(splitCommand[1]);
        long updatedValue = (value | masks[1]) & ~ masks[0];
        valuesAtMemoryPositions.put(memoryPosition, updatedValue);
    }
    private static void storeValueInMaskedMemory(
            String command, HashMap<Long, Long> valuesAtMemoryPositions,
            long[] masks) {
        String[] splitCommand = command.split(" = ");
        long memoryPosition = Integer.parseInt(splitCommand[0].split("[\\[\\]]")[1]);
        long value = Integer.parseInt(splitCommand[1]);
        HashSet<Long> xMasks = new HashSet<Long>();
        xMasks.add(masks[2]);
        for (int i = 0; i < 36; i ++) {
            long currentSingleDigitMask = (long) 1 << i & masks[2];
            if (currentSingleDigitMask != 0) {
                HashSet<Long> newMasks = new HashSet<Long>();
                for (long xMask : xMasks) {
                    newMasks.add(xMask | currentSingleDigitMask);
                    newMasks.add(xMask & ~ currentSingleDigitMask);
                }
                xMasks.addAll(newMasks);
            }
        }
        for (long xMask : xMasks) {
            long updatedMemoryPosition = (memoryPosition | masks[1]) ^ xMask;
            valuesAtMemoryPositions.put(updatedMemoryPosition, value);
        }
    }
}