import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoomTiller {
    public static void main(String[] args) {
        ArrayList<String> routes = readData(args[0]);
        HashSet<ArrayList<Integer>> blackTiles = new HashSet<ArrayList<Integer>>();
        flipDestinationTiles(routes, blackTiles);
        System.out.println("Number of black tiles after renovation: "
            + blackTiles.size());
        System.out.println("Number of black tiles after 100 days: "
            + getActiveTilesAfterXDays(blackTiles, 100));
    }
    private static ArrayList<String> readData(String fileName) {
        ArrayList<String> routes = new ArrayList<String>();
        try {
            File inFile = new File(fileName);
            Scanner inScanner = new Scanner(inFile);
            while (inScanner.hasNext()) {
                routes.add(inScanner.nextLine());
            }
            inScanner.close();
        } catch(FileNotFoundException e) {
            System.out.println("Exiting due to Exception");
            e.printStackTrace();
            System.exit(0);
        }
        return routes;
    }
    private static void flipDestinationTiles(
            ArrayList<String> routes, HashSet<ArrayList<Integer>> blackTiles) {
        for (String route : routes) {
            ArrayList<Integer> destinationTile = findDestination(route);
            if (blackTiles.contains(destinationTile)) {
                blackTiles.remove(destinationTile);
            } else {
                blackTiles.add(destinationTile);
            }
        }
    }
    private static int getActiveTilesAfterXDays(HashSet<ArrayList<Integer>> blackTiles, int days) {
        GameOfTiles got = new GameOfTiles(10, 10, days, blackTiles);
        for (int i = 0; i < days; i ++) {
            got.step();
        }
        return got.countActiveTiles();
    }
    private static ArrayList<Integer> findDestination(String route) {
        String[] directions = processRoute(route);
        int[] destination = new int[2];
        for (String direction : directions) {
            switch (direction) {
                case "e":
                    destination[0] += 1;
                    break;
                case "se":
                    destination[1] += 1;
                    break;
                case "sw":
                    destination[0] -= 1;
                    destination[1] += 1;
                    break;
                case "w":
                    destination[0] -= 1;
                    break;
                case "nw":
                    destination[1] -= 1;
                    break;
                case "ne":
                    destination[0] += 1;
                    destination[1] -= 1;
                    break;
            }
        }
        return (ArrayList<Integer>) Arrays.stream(destination)
            .boxed().collect(Collectors.toList());
    }
    private static String[] processRoute(String route) {
        return route.split("(?<=\\G(w|sw|se|e|ne|nw))");
    }
}
class GameOfTiles {
    int qidth;
    int reight;
    int[][] board;

    public GameOfTiles(int qidth, int reight, int padding) {
        this.qidth = qidth + 2 * padding;
        this.reight = reight + 2 * padding;
        this.board = new int[this.qidth][this.reight];
    }
    public GameOfTiles(int qidth, int reight, int padding, HashSet<ArrayList<Integer>> tiles) {
        this(qidth, reight, padding);
        for (ArrayList<Integer> tile : tiles) {
            board[tile.get(0) + padding][tile.get(1) + padding] = 1;
        }
    }

    public void printBoard() {
        for (int r = 0; r < reight; r ++) {
            StringBuilder line = new StringBuilder();
            for (int q = 0; q < qidth; q ++) {
                if (board[q][r] == 0) {
                    line.append(".");
                } else if (board[q][r] == 1) {
                    line.append("#");
                }
            }
            System.out.println(line.toString());
        }
        System.out.println("");
    }
    public void setOccupied(int q, int r) {
        this.board[q][r] = 1;
    }
    public void setEmtpy(int q, int r) {
        this.board[q][r] = 0;
    }
    public void step() {
        int[][] newBoard = new int[qidth][reight];
        for (int r = 0; r < reight; r ++) {
            for (int q = 0; q < qidth; q ++) {
                int activeNeighbors = countActiveNeighbors(q, r);
                newBoard[q][r] = getNewState(board[q][r], activeNeighbors);
            }
        }
        board = newBoard;
    }
    public int countActiveNeighbors(int q, int r) {
        int count = 0;
        count += getState(q + 1, r);
        count += getState(q + 1, r - 1);
        count += getState(q, r - 1);
        count += getState(q - 1, r);
        count += getState(q - 1, r + 1);
        count += getState(q, r + 1);
        return count;
    }

    public int getState(int q, int r) {
        if (q < 0 || q >= qidth
                || r < 0 || r >= reight) {
            return 0;
        } else {
            return board[q][r];
        }
    }
    private int getNewState(int currentState, int activeNeighbors) {
        if ((currentState == 1 && (activeNeighbors != 1 && activeNeighbors != 2))
                || (currentState == 0 && activeNeighbors != 2)) {
            return 0;
        }
        if ((currentState == 1 && (activeNeighbors == 1 || activeNeighbors == 2))
                || (currentState == 0 && activeNeighbors == 2)) {
            return 1;
        }
        return -1;
    }
    public int countActiveTiles() {
        int count = 0;
        for (int r = 0; r < reight; r ++) {
            for (int q = 0; q < qidth; q ++) {
                count += getState(q, r);
            }
        }
        return count;
    }
}