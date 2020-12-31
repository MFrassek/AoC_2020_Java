import java.lang.StringBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class GameOfCubesSimulation {
    public static void main(String[] args) {
        String[][] baseGridData = readData(args[0]);
        int cycles = Integer.parseInt(args[1]);
        System.out.println("Active cubes after " + cycles + " cycles in a 3-dimensional space: "
            + simulateNdimensionalConwayCubesForMCycles(baseGridData, 3, cycles));
        System.out.println("Active cubes after " + cycles + " cycles in a 4-dimensional space: "
            + simulateNdimensionalConwayCubesForMCycles(baseGridData, 4, cycles));
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
    private static int simulateNdimensionalConwayCubesForMCycles(
            String[][] baseGridData, int dimensions, int cycles) {
        GameOfCubes goc = new GameOfCubes(baseGridData, dimensions, cycles);
        for (int i = 0; i < cycles; i ++) {
            goc.step();
        }
        return(goc.countActiveCubes());        
    }
}
class GameOfCubes {

    int width;
    int height;
    int depth;
    int trength;
    int[][][][] board;

    public GameOfCubes(String[][] baseGridData, int dimensions, int cycles) {
        width = getBaseGridWidth(baseGridData) + 2 * cycles;
        height = getBaseGridHeight(baseGridData) + 2 * cycles;
        if (dimensions >= 3) {
            depth = 1 + cycles;
        } else {
            depth = 1;
        }
        if (dimensions >= 4) {
            trength = 1 + cycles;
        } else {
            trength = 1;
        }
        board = new int[trength][depth][width][height];
        for (int y = 0; y < baseGridData.length; y ++) {
            for (int x = 0; x < baseGridData[y].length; x ++) {
                if (baseGridData[y][x].equals("#")) {
                    board[0][0][x + cycles][y + cycles] = 1;
                } else if (baseGridData[y][x].equals(".")) {
                    board[0][0][x + cycles][y + cycles] = 0;
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

    public void printBoard() {
        for (int w = 0; w < trength; w ++) {
            for (int z = 0; z < depth; z ++) {
                for (int y = 0; y < height; y ++) {
                    StringBuilder line = new StringBuilder();
                    for (int x = 0; x < width; x ++) {
                        if (board[w][z][x][y] == 0) {
                            line.append("."); 
                        } else if (board[w][z][x][y] == 1) {
                            line.append("#");
                        }
                    }
                    System.out.println(line.toString());
                }
                System.out.println("");
            }
            System.out.println("");
        }
        System.out.println("");
    }
    public void setOccupied(int x, int y, int z, int w) {
        this.board[w][z][x][y] = 1;
    }
    public void setEmtpy(int x, int y, int z, int w) {
        this.board[w][z][x][y] = 0;
    }
    public void step() {
        int[][][][] newBoard = new int[trength][depth][width][height];
        for (int w = 0; w < trength; w ++) {
            for (int z = 0; z < depth; z ++) {
                for (int y = 0; y < height; y ++) {
                    for (int x = 0; x < width; x ++) {
                        int activeNeighbors = countActiveNeighbors(x, y, z, w);
                        newBoard[w][z][x][y] = getNewState(board[w][z][x][y], activeNeighbors);
                    }
                }
            }
        }
        board = newBoard;
    }
    public int countActiveNeighbors(int x, int y, int z, int w) {
        int count = 0;
        for (int wi = w -1; wi <= w + 1; wi ++) {
            for (int zi = z - 1; zi <= z + 1;  zi ++) {
                for (int yi = y - 1; yi <= y + 1; yi ++) {
                    for (int xi = x - 1; xi <= x + 1; xi ++) {
                        if (wi != w || zi != z || yi != y || xi != x) {
                            count += getState(xi, yi, zi, wi);
                        }
                    }
                }
            }
        }
        return count;
    }

    public int getState(int x, int y, int z, int w) {
        if (x < 0 || x >= width 
                || y < 0 || y >= height 
                || z >= depth || z <= -depth 
                || w >= trength || w <= -trength) {
            return 0;
        } else if (z < 0) {
            return getState(x, y, -z, w);
        } else if (w < 0) {
            return getState(x, y, z, -w);
        } else {
            return board[w][z][x][y];
        }
    }
    private int getNewState(int currentState, int activeNeighbors) {
        if ((currentState == 1 && (activeNeighbors != 2 && activeNeighbors != 3)) 
                || (currentState == 0 && activeNeighbors != 3)) {
            return 0;
        }
        if ((currentState == 1 && (activeNeighbors == 2 || activeNeighbors == 3))
                || (currentState == 0 && activeNeighbors == 3)) {
            return 1;
        }
        return -1;
    }
    public int countActiveCubes() {
        int count = 0;
        for (int w = -trength + 1; w < trength; w ++ ) {
            for (int z = -depth + 1; z < depth; z ++) {
                for (int y = 0; y < height; y ++) {
                    for (int x = 0; x < width; x ++) {
                        count += getState(x, y, z, w);
                    }
                }
            }
        }
        return count;
    }
}