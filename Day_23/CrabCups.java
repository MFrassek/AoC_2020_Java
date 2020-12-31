import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Queue;
import java.util.LinkedList;
import java.util.stream.IntStream;

public class CrabCups {
    public static void main(String[] args) {
        int[] data = readInData(args[0]);
        System.out.println("Sequence with 10 cups after 100 moves: "
            + get10CupSequenceAfter100Moves(data));
        System.out.println("Sequence with 1,000,000 cups after 10,000,000 moves: "
            + getProductOfFirstTwoCupsAfterXAfterXMoves(data, 10000000));
    }
    private static int[] readInData(String fileName) {
        try {
            File inFile = new File(fileName);
            Scanner inScanner = new Scanner(inFile);
            int[] fullData = IntStream.rangeClosed(1, 1000000)
                .boxed().mapToInt(Number::intValue).toArray();
            int[] data = Arrays.stream(inScanner.nextLine().split(""))
                .mapToInt(Integer::parseInt).toArray();
            for (int i = 0; i < data.length; i ++) {
                fullData[i] = data[i];
            }
            inScanner.close();
            return fullData;
        } catch (FileNotFoundException e) {
            System.out.println("Exiting due to Exception");
            e.printStackTrace();
            System.exit(0);
        }
        return new int[0];
    }
    private static String get10CupSequenceAfter100Moves(int[] data) {
        int[] shortData = Arrays.copyOfRange(data, 0, 9);
        CupCircle cups = new CupCircle(shortData);
        for (int i = 0; i < 100; i ++) {
            cups.move();
        }
        return cups.getCupSequenceStartingAfterX(1);
    }
    private static long getProductOfFirstTwoCupsAfterXAfterXMoves(int[] data, int moves) {
        CupCircle cups = new CupCircle(data);
        for (int i = 0; i < moves; i ++) {
            cups.move();
        }
        return cups.getProductOfFirstTwoCupsAfterX(1);
    }
}
class CupCircle {
    Cup head;
    HashMap<Integer, Cup> valueToCup;
    int circleSize;
    Queue<Cup> cupsInHand;

    public CupCircle(int[] cupValues) {
        valueToCup = new HashMap<Integer, Cup>();
        cupsInHand = new LinkedList<Cup>();
        Cup currentCup = new Cup(cupValues[0]);
        head = currentCup;
        valueToCup.put(cupValues[0], currentCup);
        for (int i = 1; i < cupValues.length; i ++) {
            Cup previousCup = currentCup;
            currentCup = new Cup(cupValues[i]);
            linkNeighboringCups(previousCup, currentCup);
            valueToCup.put(cupValues[i], currentCup);
        }
        linkNeighboringCups(currentCup, head);
        circleSize = cupValues.length;
        linkAllCupsWithBelowValueCups();
    }
    private void linkNeighboringCups(Cup firstCup, Cup secondCup) {
        firstCup.setNextCup(secondCup);
        secondCup.setPreviousCup(firstCup);
    }
    private void linkAllCupsWithBelowValueCups() {
        for (int i : valueToCup.keySet()) {
            Cup currentCup = valueToCup.get(i);
            Cup belowValueCup = valueToCup.get(Math.floorMod(i - 2, circleSize) + 1);
            currentCup.setBelowValueCup(belowValueCup);
        }
    }
    public void move() {
        pickUpClockwiseCups(3);
        Cup destinationCup = head;
        do {
            destinationCup = destinationCup.getBelowValueCup();
        } while (destinationCup.isPickedUp());
        putDownClockwiseCupsAfterCup(destinationCup);
        head = head.getNextCup();
    }
    private void pickUpClockwiseCups(int numberOfPickedUpCups) {
        Cup pseudoHead = head;
        for (int i = 0; i < numberOfPickedUpCups; i ++) {
            pseudoHead = pseudoHead.getNextCup();
            pseudoHead.pickUp();
            cupsInHand.add(pseudoHead);
        }
        Cup firstAfterPickedUpCups = pseudoHead.getNextCup();
        linkNeighboringCups(head, firstAfterPickedUpCups);
    }
    private void putDownClockwiseCupsAfterCup(Cup destinationCup) {
        Cup firstAfterDestinationCup = destinationCup.getNextCup();
        Cup pseudoHead = destinationCup;
        while (cupsInHand.size() > 0) {
            Cup currentCup = cupsInHand.remove();
            currentCup.putDown();
            linkNeighboringCups(pseudoHead, currentCup);
            pseudoHead = currentCup;
        }
        linkNeighboringCups(pseudoHead, firstAfterDestinationCup);
    }
    public String getCupSequenceStartingAfterX(int x) {
        Cup xCup = valueToCup.get(x);
        Cup currentCup = xCup.getNextCup();
        StringBuilder cupSequence = new StringBuilder();
        while (currentCup.getValue() != x) {
            cupSequence.append(currentCup.getValue());
            currentCup = currentCup.getNextCup();
        }
        return cupSequence.toString();
    }
    public void print100CupsAfterX(int x) {
        Cup xCup = valueToCup.get(x);
        Cup currentCup = xCup.getNextCup();
        for (int i = 0; i < 100; i ++) {
            System.out.print(currentCup.getValue() + " ");
            currentCup = currentCup.getNextCup();
        }
        System.out.println();
    }
    public long getProductOfFirstTwoCupsAfterX(int x) {
        Cup xCup = valueToCup.get(x);
        Cup firstCup = xCup.getNextCup();
        Cup secondCup = firstCup.getNextCup();
        return (long) firstCup.getValue() * secondCup.getValue();
    }
}

class Cup {
    private int value;
    private boolean pickedUp;
    private Cup nextCup;
    private Cup previousCup;
    private Cup belowValueCup;

    public Cup(int value) {
        this.value = value;
        pickedUp = false;
    }
    public void setNextCup(Cup nextCup) {
        this.nextCup = nextCup;
    }
    public void setPreviousCup(Cup previousCup) {
        this.previousCup = previousCup;
    }
    public void setBelowValueCup(Cup belowValueCup) {
        this.belowValueCup = belowValueCup;
    }
    public int getValue() {
        return value;
    }
    public Cup getNextCup() {
        return nextCup;
    }
    public Cup getPreviousCup() {
        return previousCup;
    }
    public Cup getBelowValueCup() {
        return belowValueCup;
    }
    public void pickUp() {
       this.pickedUp = true;
    }
    public void putDown() {
        this.pickedUp = false;
    }
    public boolean isPickedUp() {
        return pickedUp;
    }
}