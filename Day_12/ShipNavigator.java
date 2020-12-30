import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ShipNavigator {
    public static void main(String[] args) {
        ArrayList<String> commands = readInCommands(args[0]);
        System.out.println("Manhattan distance after executing the commands as ship commands: "
            + getManhattanDistanceAfterCommands(new Ship(0, 0, "E"), commands));
        System.out.println("Manhattan distance after executing the commands as way point commands: "
            + getManhattanDistanceAfterCommands(new WayPointShip(0, 0, "E"), commands));
    }    
    private static ArrayList<String> readInCommands(String fileName) {
        ArrayList<String> commands = new ArrayList<String>();
        try {
            File inFile = new File(fileName);
            Scanner inScanner = new Scanner(inFile);
            while (inScanner.hasNext()) {
                commands.add(inScanner.nextLine());
            }
            inScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Exiting due to Exception");
            e.printStackTrace();
            System.exit(0);
        }
        return commands;
    }
    private static int getManhattanDistanceAfterCommands(Ship ship, ArrayList<String> commands) {
        for (String command : commands) {
            ship.executeCommand(command);
        }
        return ship.getManhattanDistance();     
    }
}

class Ship {
    int x_pos;
    int y_pos;
    String direction;

    public Ship(int x_pos, int y_pos, String direction) {
        this.x_pos = x_pos;
        this.y_pos = y_pos;
        this.direction = direction;
    }

    public void executeCommand(String command) {
        String indicator = command.substring(0,1);
        int extent = Integer.parseInt(command.substring(1));
        if (indicator.equals("F")) {
            moveInDirection(direction, extent);
        } else if ("ESWN".contains(indicator)) {
            moveInDirection(indicator, extent);
        } else if ("LR".contains(indicator)) {
            turnByDegrees(indicator, extent);
        }
    }
    private void moveInDirection(String direction, Integer distance) {
        if (direction.equals("E")) {
            x_pos += distance;
        } else if (direction.equals("W")) {
            x_pos -= distance;
        } else if (direction.equals("N")) {
            y_pos += distance;
        } else if (direction.equals("S")) {
            y_pos -= distance;
        }
    }
    private void turnByDegrees(String direction, Integer degrees) {
        int turns = degrees / 90;
        if (direction.equals("L")) {
            for (int i = 0; i < turns; i ++) {
                turnLeftOnce();
            }
        } else if (direction.equals("R")) {
            for (int i = 0; i < turns; i ++) {
                turnRightOnce();
            }
        }
    }
    private void turnLeftOnce() {
        if (direction.equals("E")) {
            direction = "N";
        } else if (direction.equals("N")) {
            direction = "W";
        } else if (direction.equals("W")) {
            direction = "S";
        } else if (direction.equals("S")) {
            direction = "E";
        }
    }
    private void turnRightOnce() {
        if (direction.equals("E")) {
            direction = "S";
        } else if (direction.equals("S")) {
            direction = "W";
        } else if (direction.equals("W")) {
            direction = "N";
        } else if (direction.equals("N")) {
            direction = "E";
        }
    }
    public int getManhattanDistance() {
        return Math.abs(x_pos) + Math.abs(y_pos);
    }
}

class WayPointShip extends Ship {
    WayPoint wayPoint;

    public WayPointShip(int x_pos, int y_pos, String direction) {
        super(x_pos, y_pos, direction);
        wayPoint = new WayPoint(10, 1);
    }

    public void executeCommand(String command) {
        String indicator = command.substring(0,1);
        int extent = Integer.parseInt(command.substring(1));
        if (indicator.equals("F")) {
            followWayPoint(extent);
        } else if ("ESWN".contains(indicator)) {
            wayPoint.moveInDirection(indicator, extent);
        } else if ("LR".contains(indicator)) {
            wayPoint.turnByDegrees(indicator, extent);
        }
    }
    private void followWayPoint(int extent) {
        x_pos += wayPoint.x_pos * extent;
        y_pos += wayPoint.y_pos * extent;
    }
}

class WayPoint {
    int x_pos;
    int y_pos;

    public WayPoint(int x_pos, int y_pos) {
        this.x_pos = x_pos;
        this.y_pos = y_pos;
    }

    public void moveInDirection(String direction, Integer distance) {
        if (direction.equals("E")) {
            x_pos += distance;
        } else if (direction.equals("W")) {
            x_pos -= distance;
        } else if (direction.equals("N")) {
            y_pos += distance;
        } else if (direction.equals("S")) {
            y_pos -= distance;
        }
    }
    public void turnByDegrees(String direction, Integer degrees) {
        int turns = degrees / 90;
        if (direction.equals("L")) {
            for (int i = 0; i < turns; i ++) {
                turnLeftOnce();
            }
        } else if (direction.equals("R")) {
            for (int i = 0; i < turns; i ++) {
                turnRightOnce();
            }
        }
    }
    private void turnLeftOnce() {
        int temp =  x_pos;
        x_pos = -y_pos;
        y_pos = temp;
    }
    private void turnRightOnce() {
        int temp = x_pos;
        x_pos = y_pos;
        y_pos = -temp;
    }
}