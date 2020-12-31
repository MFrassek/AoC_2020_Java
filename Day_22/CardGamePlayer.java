import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;
import java.util.HashSet;

public class CardGamePlayer {
    public static void main(String[] args) {
        ArrayList<Deck> decks = readData(args[0]);
        System.out.println("Winner's score after standard game of crab combat: "
            + findWinnersScoreStandardCombat(decks));
        System.out.println("Winner's score after recursive game of crab combat: "
            + findWinnersScoreRecursiveCombat(decks));
    }
    private static ArrayList<Deck> readData(String fileName) {
        ArrayList<Deck> decks = new ArrayList<Deck>();
        try {
            File inFile = new File(fileName);
            Scanner inScanner = new Scanner(inFile);
            Deck deck = new Deck();
            while (inScanner.hasNext()) {
                String currentLine = inScanner.nextLine();
                if (currentLine.equals("")) {
                    decks.add(deck);
                    deck = new Deck();
                } else if (!currentLine.startsWith("Player")) {
                    deck.addBottomCard(Integer.parseInt(currentLine));
                }
            }
            decks.add(deck);
            inScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Exiting due to Exception");
            e.printStackTrace();
            System.exit(0);
        }
        return decks;
    }
    private static long findWinnersScoreStandardCombat(ArrayList<Deck> decks) {
        Deck deck1 = new Deck(decks.get(0));
        Deck deck2 = new Deck(decks.get(1));
        int winner = playStandardCombat(deck1, deck2);
        if (winner == 1) {
            return deck1.calculateFinalValueDestructively();
        } else {
            return deck2.calculateFinalValueDestructively();
        }
    }
    private static long findWinnersScoreRecursiveCombat(ArrayList<Deck> decks) {
        Deck deck1 = new Deck(decks.get(0));
        Deck deck2 = new Deck(decks.get(1));
        int winner = playRecursiveCombat(deck1, deck2);
        if (winner == 1) {
            return deck1.calculateFinalValueDestructively();
        } else {
            return deck2.calculateFinalValueDestructively();
        }
    }
    private static int playStandardCombat(Deck deck1, Deck deck2) {
        while (!deck1.isEmpty() && !deck2.isEmpty()) {
            playRoundStandard(deck1, deck2);
        }
        if (deck2.isEmpty()) {
            return 1;
        } else {
            return 2;
        }
    }
    private static int playRecursiveCombat(Deck deck1, Deck deck2) {
        var visitedStates = new HashSet<ArrayList<LinkedList<Integer>>>();
        var currentLists = new ArrayList<LinkedList<Integer>>();
        while (!deck1.isEmpty() && !deck2.isEmpty()) {
            LinkedList<Integer> storedDeck1 = deck1.getClonedDeck();
            LinkedList<Integer> storedDeck2 = deck2.getClonedDeck();
            currentLists = new ArrayList<LinkedList<Integer>>();
            currentLists.add(storedDeck1);
            currentLists.add(storedDeck2);
            playRoundRecursively(deck1, deck2);
            if (visitedStates.contains(currentLists)) {
                return 1;
            }
            visitedStates.add(currentLists);
        }
        if (deck2.isEmpty()) {
            return 1;
        } else {
            return 2;
        }
    }
    private static void playRoundRecursively(Deck deck1, Deck deck2) {
        int card1 = deck1.getTopCard();
        int card2 = deck2.getTopCard();
        int winner = 0;
        if (card1 > deck1.getSize() || card2 > deck2.getSize()) {
            if (card1 > card2) {
                winner = 1;
            } else if (card2 > card1) {
                winner = 2;
            }
        } else {
            Deck newDeck1 = new Deck(deck1, card1);
            Deck newDeck2 = new Deck(deck2, card2);
            winner = playRecursiveCombat(newDeck1, newDeck2);
        }
        if (winner == 1) {
            deck1.addBottomCard(card1);
            deck1.addBottomCard(card2);
        } else if (winner == 2) {
            deck2.addBottomCard(card2);
            deck2.addBottomCard(card1);
        }
    }
    private static void playRoundStandard(Deck deck1, Deck deck2) {
        int card1 = deck1.getTopCard();
        int card2 = deck2.getTopCard();
        if (card1 > card2) {
            deck1.addBottomCard(card1);
            deck1.addBottomCard(card2);
        } else if (card2 > card1) {
            deck2.addBottomCard(card2);
            deck2.addBottomCard(card1);
        }
    }
}

class Deck {
    Queue<Integer> cards;

    public Deck() {
        cards = new LinkedList<Integer>();
    }
    public Deck(Deck deck, int sectionSize) {
        int counter = 0;
        cards = new LinkedList<Integer>();
        for (Integer card : deck.getDeck()) {
            if (counter >= sectionSize) {
                break;
            }
            cards.add(card);
            counter ++;
        }
    }
    public Deck(Deck deck) {
        cards = new LinkedList<Integer>();
        for (Integer card : deck.getDeck()) {
            cards.add(card);
        }
    }
    public void addBottomCard(Integer card) {
        cards.add(card);
    }
    public int getTopCard() {
        return cards.remove();
    }
    public int getSize() {
        return cards.size();
    }
    public LinkedList<Integer> getClonedDeck() {
        return (LinkedList<Integer>) this.getDeck().clone();
    }
    public LinkedList<Integer> getDeck() {
        return (LinkedList<Integer>) cards;
    }
    public long calculateFinalValueDestructively() {
        long result = 0L;
        for (int i = cards.size(); i > 0; i --) {
            result += i * cards.remove();
        }
        return result;
    }
    public boolean isEmpty() {
        if (cards.size() == 0) {
            return true;
        } else {
            return false;
        }
    }
}