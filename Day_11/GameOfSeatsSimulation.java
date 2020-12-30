import java.lang.StringBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class GameOfSeatsSimulation {
    public static void main(String[] args) {
        String[][] baseGridData = readData(args[0]);
        System.out.println("Seat count once equilibrium is reached for basic version: "
            + getFinaleOccupationCountForGameOfSeatsVariant(
                new GameOfSeats(baseGridData)));
        System.out.println("Seat count once equilibrium is reached for line of sight version: "
            + getFinaleOccupationCountForGameOfSeatsVariant(
                new LineOfSightGameOfSeats(baseGridData)));
    }
    private static String[][] readData(String fileName) {
        ArrayList<String[]> dataRead = new ArrayList<String[]>();
        try {
            File inFile = new File(fileName);
            Scanner inScanner = new Scanner(inFile);
            while (inScanner.hasNext()) {
                dataRead.add(inScanner.nextLine().split(""));
            }
            inScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Exiting due to Exception");
            e.printStackTrace();
            System.exit(0);
        }
        String[][] fileData = new String[dataRead.size()][dataRead.get(0).length];
        for (int i = 0; i < dataRead.size(); i ++) {
            fileData[i] = dataRead.get(i);
        }
        return fileData;
    }
    private static int getFinaleOccupationCountForGameOfSeatsVariant(GameOfSeats gos) {
        int[][] currentBoard = gos.storeBoard();
        gos.step();
        while (!gos.hasSameBoardAsReferenceBoard(currentBoard)) {
            currentBoard = gos.storeBoard();
            gos.step();
        }
        return gos.countOccupiedSeats();
    }
}

class GameOfSeats {

    int width;
    int height;
    int[][] board;

    public GameOfSeats(int width, int height) {
        this.width = width;
        this.height = height;
        board = new int[width][height];
    }
    public GameOfSeats(String[][] baseGridData) {
        width = getBaseGridWidth(baseGridData);
        height = getBaseGridHeight(baseGridData);
        board = new int[width][height];
        for (int y = 0; y < height; y ++) {
            for (int x = 0; x < width; x ++) {
                if (baseGridData[y][x].equals("L")) {
                    board[x][y] = 0;
                } else if (baseGridData[y][x].equals("#")) {
                    board[x][y] = 1;
                } else if (baseGridData[y][x].equals(".")) {
                    board[x][y] = -1;
                }
            }
        }
    }
    private static int getBaseGridWidth(String[][] baseGridData) {
        return baseGridData[0].length;
    }
    private static int getBaseGridHeight(String[][] baseGridData) {
        return baseGridData.length;
    }

    public void step() {
        int[][] newBoard = new int[width][height];
        for (int y = 0; y < height; y ++) {
            for (int x = 0; x < width; x ++) {
                int occupiedNeighbors = countOccupiedNeighborSeats(x, y);
                newBoard[x][y] = getNewState(board[x][y], occupiedNeighbors);
            }
        }
        board = newBoard;
    }
    public void setOccupied(int x, int y) {
        board[x][y] = 1;
    }
    public void setEmtpy(int x, int y) {
        board[x][y] = 0;
    }
    public void setFloor(int x, int y) {
        board[x][y] = -1;
    }
    protected int countOccupiedNeighborSeats(int x, int y) {
        int count = 0;
        count += getSimplifiedState(x - 1, y - 1);
        count += getSimplifiedState(x, y - 1);
        count += getSimplifiedState(x + 1, y - 1);

        count += getSimplifiedState(x - 1, y);
        count += getSimplifiedState(x + 1, y);

        count += getSimplifiedState(x - 1, y + 1);
        count += getSimplifiedState(x, y + 1);
        count += getSimplifiedState(x + 1, y + 1);

        return count;
    }
    protected int getSimplifiedState(int x, int y) {
        if (x < 0 || x >= width) {
            return 0;
        }
        if (y < 0 || y >= height) {
            return 0;
        }
        if (board[x][y] == 1) {
            return 1;
        } else {
            return 0;
        }

    }
    protected int getNewState(int currentState, int occupiedNeighbors) {
        if ((currentState == 1 && occupiedNeighbors >= 4) 
            || (currentState == 0 && occupiedNeighbors > 0)) return 0;
        if ((currentState == 1 && occupiedNeighbors < 4)
            || (currentState == 0 && occupiedNeighbors == 0)) return 1;
        if (currentState == -1) return -1;
        return -2;
    }

    public void printBoard() {
        for (int y = 0; y < height; y ++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < width; x ++) {
                if (board[x][y] == 0) {
                    line.append("L"); 
                } else if (board[x][y] == 1) {
                    line.append("#");
                } else if (board[x][y] == -1) {
                    line.append(".");
                }
            }
            System.out.println(line.toString());
        }
        System.out.println("");
    }
    public int[][] storeBoard() {
        int[][] storeBoard = new int[width][height];
        for (int y = 0; y < height; y ++) {
            for (int x = 0; x < width; x ++) {
                storeBoard[x][y] = board[x][y];
            }
        }
        return storeBoard;
    }
    public boolean hasSameBoardAsReferenceBoard(int[][] referenceBoard) {
        for (int y = 0; y < height; y ++) {
            for (int x = 0; x < width; x ++) {
                if (board[x][y] != referenceBoard[x][y]) {
                    return false;
                }
            }
        }
        return true;
    }
    public int countOccupiedSeats() {
        int count = 0; 
        for (int y = 0; y < height; y ++) {
            for (int x = 0; x < width; x ++) {
                count += getSimplifiedState(x, y);
            }
        }
        return count;
    }
}

class LineOfSightGameOfSeats extends GameOfSeats {
    
    public LineOfSightGameOfSeats(int width, int height) {
        super(width, height);
    }
    public LineOfSightGameOfSeats(String[][] baseGridData) {
        super(baseGridData);
    }
    public int countOccupiedNeighborSeats(int x, int y) {
        int count = 0;
        count += getSimplifiedStateOfFirstSeatInDirection(x, y, -1, - 1);
        count += getSimplifiedStateOfFirstSeatInDirection(x, y, 0, - 1);
        count += getSimplifiedStateOfFirstSeatInDirection(x, y, +1, - 1);

        count += getSimplifiedStateOfFirstSeatInDirection(x, y, -1, 0);
        count += getSimplifiedStateOfFirstSeatInDirection(x, y, +1, 0);

        count += getSimplifiedStateOfFirstSeatInDirection(x, y, -1, +1);
        count += getSimplifiedStateOfFirstSeatInDirection(x, y, 0, +1);
        count += getSimplifiedStateOfFirstSeatInDirection(x, y, +1, +1);

        return count;
    }
    protected int getSimplifiedStateOfFirstSeatInDirection(int x, int y, int xDir, int yDir) {
        x += xDir;
        y += yDir;
        while (x >= 0 && x < width && y >= 0 && y < height) {
            if (board[x][y] >= 0) {
                return board[x][y];
            }
            x += xDir;
            y += yDir;
        }
        return 0;
    }
    protected int getNewState(int currentState, int occupiedNeighbors) {
        if ((currentState == 1 && occupiedNeighbors >= 5) 
            || (currentState == 0 && occupiedNeighbors > 0)) return 0;
        if ((currentState == 1 && occupiedNeighbors < 5)
            || (currentState == 0 && occupiedNeighbors == 0)) return 1;
        if (currentState == -1) return -1;
        return -2;
    }
}