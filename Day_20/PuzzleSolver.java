import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;

public class PuzzleSolver {
    public static void main(String[] args) {
        HashMap<Integer, PuzzlePiece> puzzle = readInData(args[0]);
        assignMatchesAndNeighborPositionsToAllPieces(puzzle);
        System.out.println("Product of corner piece ids: "
            + calculateProductOfCornerPieceIds(puzzle));
        ArrayList<ArrayList<PuzzlePiece>> solvedGrid = solveGrid(puzzle);
        PuzzlePiece completePiece = assembleAllContentsToOnePiece(solvedGrid);
        long hashTagCount = completePiece.countHashTags();
        System.out.println("Count of hash tags not belonging to sea monster bodies: "
            + findNonSeaMonsterHashTagCountOfCorrectConfigurations(completePiece, hashTagCount));
    }
    private static void assignMatchesAndNeighborPositionsToAllPieces(
            HashMap<Integer, PuzzlePiece> puzzle) {
        for (int keyI : puzzle.keySet()) {
            for (int keyJ : puzzle.keySet()) {
                if (keyI != keyJ) {
                    PuzzlePiece pieceI = puzzle.get(keyI);
                    PuzzlePiece pieceJ = puzzle.get(keyJ);
                    if (piecesShareBorder(pieceI, pieceJ)) {
                        pieceI.matches.add(keyJ);
                        assignJsRelativePositionToI(pieceI, pieceJ);
                    }
                }
            }
        }
    }
    private static long calculateProductOfCornerPieceIds(HashMap<Integer, PuzzlePiece> puzzle) {
        long total = 1;
        for (int key : puzzle.keySet()) {
            if (puzzle.get(key).matches.size() == 2) {
                total *= puzzle.get(key).id;
            }
        }
        return total;
    }
    private static ArrayList<ArrayList<PuzzlePiece>> solveGrid(
            HashMap<Integer, PuzzlePiece> puzzle) {
        var solvedGrid = new ArrayList<ArrayList<PuzzlePiece>>();
        solvedGrid.add(solveFirstRow(puzzle));
        PuzzlePiece previousRowFirstPiece = solvedGrid.get(0).get(0);
        while (previousRowFirstPiece.hasBottomNeighbor()) {
            solvedGrid.add(solveSubsequentRow(puzzle, previousRowFirstPiece));
            previousRowFirstPiece = previousRowFirstPiece.bottomNeighbor;
        }
        return solvedGrid;
    }
    private static PuzzlePiece assembleAllContentsToOnePiece(
            ArrayList<ArrayList<PuzzlePiece>> solvedGrid) {
        ArrayList<String> fullContent = new ArrayList<String>();
        for (var row : solvedGrid) {
            for (int i = 0; i < row.get(0).content.size();  i ++) {
                StringBuilder line = new StringBuilder();
                for (var ele : row) {
                    line.append(ele.content.get(i));
                }
                fullContent.add(line.toString());
            }
        }
        return new PuzzlePiece(0, fullContent, true);
    }
    private static ArrayList<PuzzlePiece> solveFirstRow(HashMap<Integer, PuzzlePiece> puzzle) {
        var solvedRow = new ArrayList<PuzzlePiece>();
        PuzzlePiece firstPiece = getAnyCornerPiece(puzzle);
        orientPieceInAccordanceWithItsTopLeftCornerPosition(firstPiece);
        solvedRow.add(firstPiece);
        PuzzlePiece previousPiece = firstPiece;
        while (previousPiece.hasRightNeighbor()) {
            PuzzlePiece currentPiece = previousPiece.rightNeighbor;
            orientRightPieceInAccordanceWithLeftPiece(currentPiece, previousPiece);
            orientPieceInAccordanceWithItsTopRowPosition(currentPiece);
            solvedRow.add(currentPiece);
            previousPiece = currentPiece;
        }
        return solvedRow;
    }
    private static ArrayList<PuzzlePiece> solveSubsequentRow(
            HashMap<Integer, PuzzlePiece> puzzle, PuzzlePiece previousRowFirstPiece) {
        var solvedRow = new ArrayList<PuzzlePiece>();
        PuzzlePiece currentRowFirstPiece = previousRowFirstPiece.bottomNeighbor;
        orientBottomPieceInAccordanceWithTopPiece(currentRowFirstPiece, previousRowFirstPiece);
        orientPieceInAccordanceWithItsLeftColumnPosition(currentRowFirstPiece);
        solvedRow.add(currentRowFirstPiece);
        PuzzlePiece previousPiece = currentRowFirstPiece;
        PuzzlePiece previousRowPreviousPiece = previousRowFirstPiece;
        while (previousPiece.hasRightNeighbor()) {
            PuzzlePiece currentPiece = previousPiece.rightNeighbor;
            orientRightPieceInAccordanceWithLeftPiece(currentPiece, previousPiece);
            PuzzlePiece previousRowCurrentPiece = previousRowPreviousPiece.rightNeighbor;
            orientBottomPieceInAccordanceWithTopPiece(currentPiece, previousRowCurrentPiece);
            solvedRow.add(currentPiece);
            previousPiece = currentPiece;
            previousRowPreviousPiece = previousRowCurrentPiece;
        }
        return solvedRow;
    }
    private static PuzzlePiece getAnyCornerPiece(HashMap<Integer, PuzzlePiece> puzzle) {
        for (int key : puzzle.keySet()) {
            if (puzzle.get(key).matches.size() == 2) {
                return puzzle.get(key);
            }
        }
        return new PuzzlePiece();
    }
    private static void orientPieceInAccordanceWithItsTopLeftCornerPosition(
            PuzzlePiece topLeftCornerPiece) {
        if (!topLeftCornerPiece.hasRightNeighbor()) {
            topLeftCornerPiece.mirrorXAxis();
        }
        if (!topLeftCornerPiece.hasBottomNeighbor()) {
            topLeftCornerPiece.mirrorYAxis();
        }
    }
    private static void orientRightPieceInAccordanceWithLeftPiece(
            PuzzlePiece rightPiece, PuzzlePiece leftPiece) {
        if (rightPiece.topNeighbor == leftPiece) {
            rightPiece.rotateClockwise();
            rightPiece.mirrorXAxis();
        } else if (rightPiece.rightNeighbor == leftPiece) {
            rightPiece.mirrorXAxis();
        } else if (rightPiece.bottomNeighbor == leftPiece) {
            rightPiece.rotateClockwise();
        }
    }
    private static void orientPieceInAccordanceWithItsTopRowPosition(PuzzlePiece topRowPiece) {
        if (!topRowPiece.hasBottomNeighbor()) {
            topRowPiece.mirrorYAxis();
        }
    }
    private static void orientBottomPieceInAccordanceWithTopPiece(
            PuzzlePiece bottomPiece, PuzzlePiece topPiece) {
        if (bottomPiece.rightNeighbor == topPiece) {
            bottomPiece.rotateClockwise();
            bottomPiece.mirrorYAxis();
        } else if (bottomPiece.bottomNeighbor == topPiece) {
            bottomPiece.mirrorYAxis();
        } else if (bottomPiece.leftNeighbor == topPiece) {
            bottomPiece.rotateClockwise();
        }
    }
    private static void orientPieceInAccordanceWithItsLeftColumnPosition(
            PuzzlePiece leftColumnPiece) {
        if (!leftColumnPiece.hasRightNeighbor()) {
            leftColumnPiece.mirrorXAxis();
        }
    }
    private static boolean piecesShareBorder(PuzzlePiece pieceI, PuzzlePiece pieceJ) {
        HashSet<String> intersection = (HashSet<String>) pieceI.allBorders.clone();
        intersection.retainAll(pieceJ.allBorders);
        return intersection.size() > 0;
    }
    private static void assignJsRelativePositionToI(PuzzlePiece pieceI, PuzzlePiece  pieceJ) {
        if (pieceJ.allBorders.contains(pieceI.leftBorder)
                || pieceJ.allBorders.contains(pieceI.leftBorderReverse)) {
            pieceI.leftNeighbor = pieceJ;
        } else if (pieceJ.allBorders.contains(pieceI.rightBorder)
                || pieceJ.allBorders.contains(pieceI.rightBorderReverse)) {
            pieceI.rightNeighbor = pieceJ;
        } else if (pieceJ.allBorders.contains(pieceI.topBorder)
                || pieceJ.allBorders.contains(pieceI.topBorderReverse)) {
            pieceI.topNeighbor = pieceJ;
        } else if (pieceJ.allBorders.contains(pieceI.bottomBorder)
                || pieceJ.allBorders.contains(pieceI.bottomBorderReverse)) {
            pieceI.bottomNeighbor = pieceJ;
        }
    }
    private static long findNonSeaMonsterHashTagCountOfCorrectConfigurations(
            PuzzlePiece completePiece, long hashTagCount) {
        PuzzlePiece copyPiece = new PuzzlePiece(1, completePiece.content, true);
        long nonSeaMonsterHashTagCount = -1L;
        for (int i = 0; i < 4; i ++) {
            nonSeaMonsterHashTagCount = findNonSeaMonsterHashTagCount(copyPiece, hashTagCount);
            if (nonSeaMonsterHashTagCount != -1L) {   
                return nonSeaMonsterHashTagCount; 
            }
            copyPiece.rotateClockwise();
        }
        copyPiece.mirrorXAxis();
        for (int i = 0; i < 4; i ++) {
            nonSeaMonsterHashTagCount = findNonSeaMonsterHashTagCount(copyPiece, hashTagCount);
            if (nonSeaMonsterHashTagCount != -1L) {   
                return nonSeaMonsterHashTagCount; 
            }
            copyPiece.rotateClockwise();
        }
        return nonSeaMonsterHashTagCount; 
    }
    private static long findNonSeaMonsterHashTagCount(
            PuzzlePiece completePiece, long hashTagCount) {
        int seaMonsterCount = 0;
        for (int x = 0; x < completePiece.content.get(0).length() - 18; x ++) {
            for (int y = 0; y < completePiece.content.size() - 2; y ++) {
                if (completePiece.checkForSeaMonster(x, y)) {
                    seaMonsterCount ++;
                }
            }
        }
        if (seaMonsterCount > 0) {
            return hashTagCount - 15 * seaMonsterCount;
        }
        return -1;
    }
    private static void printSolvedGrid(ArrayList<ArrayList<PuzzlePiece>> solvedGrid) {
        for (ArrayList<PuzzlePiece> row : solvedGrid) {
            for (PuzzlePiece piece : row) {
                System.out.print(piece.id + " ");
            }
            System.out.print("\n");
        }
        for (ArrayList<PuzzlePiece> row : solvedGrid) {
            for (PuzzlePiece piece : row) {
                piece.printId();
                piece.print();
            }
        }
    }
    private static HashMap<Integer, PuzzlePiece> readInData(String fileName) {
        HashMap<Integer, PuzzlePiece> puzzle = new HashMap<Integer, PuzzlePiece>();
        try {
            File inFile = new File(fileName);
            Scanner fileScanner = new Scanner(inFile);
            while (fileScanner.hasNext()) {
                var puzzlePiece = readInPuzzlePiece(fileScanner);
                puzzle.put(puzzlePiece.id, puzzlePiece);
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Exiting due to Exception");
            e.printStackTrace();
            System.exit(0);
        }
        return puzzle;
    }
    private static PuzzlePiece readInPuzzlePiece(Scanner fileScanner) {
        ArrayList<String> pieceContent = new ArrayList<String>();
        int id = Integer.parseInt(fileScanner.nextLine().split("[ :]")[1]);
        while (fileScanner.hasNext()) {
            String currentLine = fileScanner.nextLine();
            if (currentLine.length() == 0) {
                return new PuzzlePiece(id, pieceContent);
            } else {
                pieceContent.add(currentLine);
            }
        }
        return new PuzzlePiece(id, pieceContent);
    }
}


class PuzzlePiece {
    int id;
    ArrayList<String> content;
    HashSet<String> allBorders;
    String topBorder;
    String topBorderReverse;
    String bottomBorder;
    String bottomBorderReverse;
    String leftBorder;
    String leftBorderReverse;
    String rightBorder;
    String rightBorderReverse;
    PuzzlePiece topNeighbor;
    PuzzlePiece bottomNeighbor;
    PuzzlePiece leftNeighbor;
    PuzzlePiece rightNeighbor;
    HashSet<Integer> matches;

    public PuzzlePiece() {
    }
    public PuzzlePiece(int id, ArrayList<String> content) {
        matches = new HashSet<Integer>();
        this.id = id;
        var borderBuilders = makeBorderBuilders(content);
        topBorder = borderBuilders.get(0).toString();
        topBorderReverse = borderBuilders.get(0).reverse().toString();
        bottomBorder = borderBuilders.get(1).toString();
        bottomBorderReverse = borderBuilders.get(1).reverse().toString();
        leftBorder = borderBuilders.get(2).toString();
        leftBorderReverse = borderBuilders.get(2).reverse().toString();
        rightBorder = borderBuilders.get(3).toString();
        rightBorderReverse = borderBuilders.get(3).reverse().toString();
        allBorders = new HashSet<String>(Arrays.asList(
            topBorder, topBorderReverse, bottomBorder, bottomBorderReverse,
            leftBorder, leftBorderReverse, rightBorder, rightBorderReverse));
        this.content = getTrimmedContent(content);
    }
    public PuzzlePiece(int id, ArrayList<String> content, boolean withBorderFlag) {
        this(id, content);
        if (withBorderFlag) {
            this.content = content;
        }
    }
    private static ArrayList<StringBuilder> makeBorderBuilders(ArrayList<String> content) {
        StringBuilder topBorder = new StringBuilder();
        topBorder.append(content.get(0));
        StringBuilder bottomBorder = new StringBuilder();
        bottomBorder.append(content.get(content.size() - 1));
        StringBuilder leftBorder = new StringBuilder();
        StringBuilder rightBorder = new StringBuilder();
        for (String row : content) {
            leftBorder.append(row.charAt(0));
            rightBorder.append(row.charAt(row.length() - 1));
        }
        ArrayList<StringBuilder> borderBuilders = new ArrayList<StringBuilder>();
        borderBuilders.add(topBorder);
        borderBuilders.add(bottomBorder);
        borderBuilders.add(leftBorder);
        borderBuilders.add(rightBorder);
        return borderBuilders;
    }
    private ArrayList<String> getTrimmedContent(ArrayList<String> content) {
        var trimmedContent = new ArrayList<String>();
        for (int i = 1; i < content.size() - 1; i ++) {
            trimmedContent.add(content.get(i).substring(1, content.get(i).length() - 1));
        }
        return trimmedContent;
    }
    public long countHashTags() {
        return content
                .stream()
                .map(s -> Arrays.asList(s.split(""))
                    .stream()
                    .filter(e -> e.equals("#"))
                    .count())
                .reduce(0L, (a,b) -> a + b);
    }
    public void print() {
        for (String row : this.content) {
            System.out.println(row);
        }
        System.out.println();
    }
    public void printNeigbors() {
        if (topNeighbor != null) {
            System.out.println("Top: " + topNeighbor.id);
        }
        if (rightNeighbor != null) {
            System.out.println("Right: " + rightNeighbor.id);
        }
        if (bottomNeighbor != null) {
            System.out.println("Bottom: " + bottomNeighbor.id);
        }
        if (leftNeighbor != null) {
            System.out.println("Left: " + leftNeighbor.id);
        }
    }
    public void printId() {
        System.out.println(id);
    }
    public boolean hasTopNeighbor() {
        if (topNeighbor != null) {
            return true;
        } else {
            return false;
        }
    }
    public boolean hasRightNeighbor() {
        if (rightNeighbor != null) {
            return true;
        } else {
            return false;
        }
    }
    public boolean hasBottomNeighbor() {
        if (bottomNeighbor != null) {
            return true;
        } else {
            return false;
        }
    }
    public boolean hasLeftNeighbor() {
        if (leftNeighbor != null) {
            return true;
        } else {
            return false;
        }
    }
    public void mirrorXAxis() {
        mirrorYBorders();
        mirrorXNeighbors();
        mirrorXContent();
    }
    public void mirrorYAxis() {
        mirrorYBorders();
        mirrorYNeighbors();
        mirrorYContent();
    }
    public void rotateClockwise() {
        rotateBordersClockwise();
        rotateNeighborsClockwise();
        rotateContentClockwise();
    }
    private void mirrorXBorders() {
        String temp = leftBorder;
        leftBorder = rightBorder;
        rightBorder = temp;
        temp = topBorder;
        topBorder = topBorderReverse;
        topBorderReverse = temp;
        temp = bottomBorder;
        bottomBorder = bottomBorderReverse;
        bottomBorderReverse = temp;
    }
    private void mirrorXNeighbors() {
        PuzzlePiece tempNeighbor = leftNeighbor;
        leftNeighbor = rightNeighbor;
        rightNeighbor = tempNeighbor;
    }
    private void mirrorXContent() {
        ArrayList<String> newContent = new ArrayList<String>();
        for (String row : content) {
            StringBuilder rowB = new StringBuilder();
            rowB.append(row);
            newContent.add(rowB.reverse().toString());
        }
        content = newContent;
    }
    private void mirrorYBorders() {
        String temp = topBorder;
        topBorder = bottomBorder;
        bottomBorder = temp;
        temp = leftBorder;
        leftBorder = leftBorderReverse;
        leftBorderReverse = temp;
        temp = rightBorder;
        rightBorder = rightBorderReverse;
        rightBorderReverse = temp;
    }
    private void mirrorYNeighbors() {
        PuzzlePiece tempNeighbor = topNeighbor;
        topNeighbor = bottomNeighbor;
        bottomNeighbor = tempNeighbor;
    }
    private void mirrorYContent() {
        ArrayList<String> newContent = new ArrayList<String>();
        for (int i = content.size() - 1; i >= 0; i --) {
            newContent.add(content.get(i));
        }
        content = newContent;
    }
    private void rotateBordersClockwise() {
        String temp = rightBorder;
        rightBorder = topBorder;
        topBorder = leftBorderReverse;
        leftBorderReverse = bottomBorderReverse;
        bottomBorderReverse = temp;

        temp = rightBorderReverse;
        rightBorderReverse = topBorderReverse;
        topBorderReverse = leftBorder;
        leftBorder = bottomBorder;
        bottomBorder = temp;
    }
    private void rotateNeighborsClockwise() {
        PuzzlePiece tempNeighbor = rightNeighbor;
        rightNeighbor = topNeighbor;
        topNeighbor = leftNeighbor;
        leftNeighbor = bottomNeighbor;
        bottomNeighbor = tempNeighbor;
    }
    private void rotateContentClockwise() {
        ArrayList<String> newContent = new ArrayList<String>();
        for (int i = 0; i < content.get(0).length(); i ++) {
            StringBuilder rowB = new StringBuilder();
            for (int j = content.size() - 1; j >= 0; j --) {
                rowB.append(content.get(j).charAt(i));
            }
            newContent.add(rowB.toString());
        }
        content = newContent;
    }
    public boolean checkForSeaMonster(int x, int y) {
        if (content.get(y + 1).charAt(x) == '#' && content.get(y + 2).charAt(x + 1) == '#'
            && content.get(y + 2).charAt(x + 4) == '#' && content.get(y + 1).charAt(x + 5) == '#'
            && content.get(y + 1).charAt(x + 6) == '#' && content.get(y + 2).charAt(x + 7) == '#'
            && content.get(y + 2).charAt(x + 10) == '#' && content.get(y + 1).charAt(x + 11) == '#'
            && content.get(y + 1).charAt(x + 12) == '#' && content.get(y + 2).charAt(x + 13) == '#'
            && content.get(y + 2).charAt(x + 16) == '#' && content.get(y + 1).charAt(x + 17) == '#'
            && content.get(y + 1).charAt(x + 18) == '#' && content.get(y).charAt(x + 18) == '#'
            && content.get(y + 1).charAt(x + 19) == '#'
            ) {
            return true;
        } else {
            return false;
        }
    }
}