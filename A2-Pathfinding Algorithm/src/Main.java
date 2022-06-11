import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    // boolean to observe if we have a teleport map or not
    static boolean containsTeleports = false;

    public static void main(String[] args) throws IOException {

        // Scanner for input
        Scanner input = new Scanner(System.in);

        // an input path variable
        String inputPath = "teleportInput(10)E.txt";

        // create a 2d array map based on the input file specified
        String[][] map = createMapArray(inputPath);

        // display the map on screen
        for (String[] line : map) {
            for (String character : line) {
                // add an extra space if there's no T, so we get a prettier table
                if (character.contains("T"))
                    System.out.print(character + "  ");
                else
                    System.out.print(character + "   ");
            }
            System.out.println();
        }
        System.out.println();

        // input prompt
        System.out.println("Please enter starting row value (top to bottom, 1-" + (map.length) + "):");
        int row = input.nextInt() - 1;
        System.out.println("Please enter starting col value (left to right, 1-" + (map[0].length) + "):");
        int col = input.nextInt() - 1;
        System.out.println("Please enter ending row value (top to bottom, 1-" + (map.length) + "):");
        int endRow = input.nextInt() - 1;
        System.out.println("Please enter ending col value (left to right, 1-" + (map[0].length) + "):");
        int endCol = input.nextInt() - 1;

        // input value checks to make sure input won't break the program
        if (row > map.length - 1 || col > map[0].length - 1 ||
                endRow > map.length - 1 || endCol > map[0].length - 1 ||
                row < 0 || col < 0 ||
                endRow < 0 || endCol < 0) {
            System.out.println("Please enter valid dimensions only please!");
            System.exit(0);
        }

        // add cells in the 2D array first
        Cell[][] allCells = new Cell[map.length][map[0].length];
        if (!containsTeleports) {
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (i == row && j == col) {
                        if (map[i][j].equals("F")) {
                            System.out.println("Cannot start from void location!");
                            System.exit(0);
                        } else {
                            allCells[i][j] = new Cell(i, j, map[i][j]);
                        }
                    } else if (i == endRow && j == endCol) {
                        if (map[i][j].equals("F")) {
                            System.out.println("Cannot end at void location!");
                            System.exit(0);
                        } else {
                            allCells[i][j] = new Cell(i, j, map[i][j]);
                        }
                    } else {
                        if (map[i][j].equals("F"))
                            // insert an empty cell (represented as null)
                            allCells[i][j] = null;
                        else
                            // insert a cell with this and calculate the heuristic
                            // INTERNALLY within the cell
                            allCells[i][j] = new Cell(i, j, map[i][j]);
                    }
                }
            }
        } else {
            // make cells with teleports...
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (i == row && j == col) {
                        if (map[i][j].equals("F")) {
                            System.out.println("Cannot start from void location!");
                            System.exit(0);
                        } else if (map[i][j].contains("T")) {
                            System.out.println("Cannot start from teleport location!");
                            System.exit(0);
                        } else {
                            allCells[i][j] = new Cell(i, j, map[i][j]);
                        }
                    } else if (i == endRow && j == endCol) {
                        if (map[i][j].equals("F")) {
                            System.out.println("Cannot end at void location!");
                            System.exit(0);
                        } else if (map[i][j].contains("T")) {
                            System.out.println("Cannot end at teleport location!");
                            System.exit(0);
                        } else {
                            allCells[i][j] = new Cell(i, j, map[i][j]);
                        }
                    } else {
                        if (map[i][j].equals("F"))
                            // insert an empty cell (represented as null)
                            allCells[i][j] = null;
                        else
                            // insert a cell with this, and derive teleport index automatically if able
                            allCells[i][j] = new Cell(i, j, map[i][j]);
                    }
                }
            }
        }
        // add branches next!
        for (int i = 0; i < allCells.length; i++) {
            for (int j = 0; j < allCells[i].length; j++) {
                if (allCells[i][j] != null) {

                    // vertical boundary checks
                    int newV;
                    if (i == 0) {
                        // upper link points to bottom of map
                        newV = map.length - 1;
                        if (allCells[newV][j] != null)
                            allCells[i][j].addBranch(allCells[newV][j].incomingWeight, allCells[newV][j]);
                        // lower is as expected
                        if (allCells[i + 1][j] != null)
                            allCells[i][j].addBranch(allCells[i + 1][j].incomingWeight, allCells[i + 1][j]);
                    } else if (i == map.length - 1) {
                        // lower link points to top of map
                        newV = 0;
                        if (allCells[newV][j] != null)
                            allCells[i][j].addBranch(allCells[newV][j].incomingWeight, allCells[newV][j]);
                        // upper is as expected
                        if (allCells[i - 1][j] != null)
                            allCells[i][j].addBranch(allCells[i - 1][j].incomingWeight, allCells[i - 1][j]);
                    } else {
                        // lower is as expected
                        if (allCells[i + 1][j] != null)
                            allCells[i][j].addBranch(allCells[i + 1][j].incomingWeight, allCells[i + 1][j]);
                        // upper is as expected
                        if (allCells[i - 1][j] != null)
                            allCells[i][j].addBranch(allCells[i - 1][j].incomingWeight, allCells[i - 1][j]);
                    }

                    // horizontal boundary checks
                    int newH;
                    if (j == 0) {
                        // left link points to right of map
                        newH = map[i].length - 1;
                        if (allCells[i][newH] != null)
                            allCells[i][j].addBranch(allCells[i][newH].incomingWeight, allCells[i][newH]);
                        // right is as expected
                        if (allCells[i][j + 1] != null)
                            allCells[i][j].addBranch(allCells[i][j + 1].incomingWeight, allCells[i][j + 1]);
                    } else if (j == map[i].length - 1) {
                        // right link points to left side of map
                        newH = 0;
                        if (allCells[i][newH] != null)
                            allCells[i][j].addBranch(allCells[i][newH].incomingWeight, allCells[i][newH]);
                        // left is as expected
                        if (allCells[i][j - 1] != null)
                            allCells[i][j].addBranch(allCells[i][j - 1].incomingWeight, allCells[i][j - 1]);
                    } else {
                        // right is as expected
                        if (allCells[i][j + 1] != null)
                            allCells[i][j].addBranch(allCells[i][j + 1].incomingWeight, allCells[i][j + 1]);
                        // left is as expected
                        if (allCells[i][j - 1] != null)
                            allCells[i][j].addBranch(allCells[i][j - 1].incomingWeight, allCells[i][j - 1]);
                    }
                }
            }
        }
        Cell result;
        if (!containsTeleports) {
            result = aStar(row, col, endRow, endCol, allCells);
        } else
            result = dStraWithTeleports(row, col, endRow, endCol, allCells);
        // print the result of what we made
        System.out.print("Output: ");
        printPath(result);
    }

    // create 2D array of cell characters based on the text file given
    private static String[][] createMapArray(String path) throws IOException {

        // an arraylist of the lines we record from the text file
        ArrayList<String> lines = new ArrayList<>();

        // File path is passed as parameter
        File inputMap = new File(path);

        // Creating an object of BufferedReader class
        BufferedReader br = new BufferedReader(new FileReader(inputMap));

        // Declaring a string variable
        String inputLine;
        // Condition holds true till
        // there is character in a string
        while ((inputLine = br.readLine()) != null) {
            lines.add(inputLine);
        }

        // we can use the same value for both length and width of map ASSUMING they are the same in input files!
        // will break if otherwise
        String[][] output = new String[lines.size()][lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            output[i] = lines.get(i).split(" ");
            // if we ever find T1, we have teleports!
            if (lines.get(i).contains("T1 "))
                containsTeleports = true;
        }

        // return the 2d array
        return output;
    }

    // aStar algorithm returns the shortest path from the end cell to the start cell via parent links
    private static Cell aStar(int row, int col, int endRow, int endCol, Cell[][] allCells) {

        // hold collections of cells that record what is available to move to or already visited
        PriorityQueue<Cell> closedList = new PriorityQueue<>();
        PriorityQueue<Cell> openList = new PriorityQueue<>();

        // initialize the start cell
        allCells[row][col].g = 0; // update the g val (total weight from start, which would be 0)
        allCells[row][col].calculateHeuristic(endRow, endCol); // update the h val (manhattan distance from target)
        allCells[row][col].updateScore(); // updates f = g+h value;
        openList.add(allCells[row][col]); // add to the list of available cells

        // keep running while we still have cells to reach
        while (!openList.isEmpty()) {

            // grab the next best possible cell to visit
            Cell current = openList.peek();

            // if we've arrived at the end cell
            if (current == allCells[endRow][endCol])
                return current;

            // otherwise, view all the edges connected to this cell
            for (Edge edge : current.neighbors) {

                // grab an adjacent cell and record its weight
                Cell next = edge.cell;
                int totalWeight = current.g + edge.weight;

                // if we haven't observed this cell yet in some capacity (not yet opened or not yet closed)
                // we'll add it to the open list
                if (!openList.contains(next) && !closedList.contains(next)) {
                    next.parent = current; // update the parent
                    next.g = totalWeight; // update g
                    next.calculateHeuristic(endRow, endCol); // update h
                    next.updateScore(); // update f = g+h
                    openList.add(next); // add this adjacent cell to the list off possible cells to travel to
                }

                // if we have observed this cell before in some capacity, then we'll first check to see
                // if it's a viable option to reconsider
                else {

                    // if this weight from parent has not been updated on this cell yet (ie we have not been on it)
                    // or rather, if we have been made aware of it, but not been on it per se
                    if (totalWeight < next.g) {
                        next.parent = current; // update the parent
                        next.g = totalWeight; // update g
                        next.calculateHeuristic(endRow, endCol); // update h
                        next.updateScore(); // update f = g+h

                        // add this cell back into the possible moves
                        if (closedList.contains(next)) {
                            closedList.remove(next);
                            openList.add(next);
                        }
                    }
                }
            }

            // we are all set with this cell, we can remove it from open and put it in closed
            openList.remove(current);
            closedList.add(current);
        }
        // return this if we have not reached the end cell, since reaching the end cell wil trigger a different return
        return null;
    }

    // dStraWithTeleports algorithm to run similar to aStar algorithm from before, but without heuristic
    private static Cell dStraWithTeleports(int row, int col, int endRow, int endCol, Cell[][] allCells) {

        // hold collections of cells that record what is available to move to or already visited
        PriorityQueue<Cell> closedList = new PriorityQueue<>();
        PriorityQueue<Cell> openList = new PriorityQueue<>();

        // initialize the start cell
        Cell start = allCells[row][col];
        start.f = 0;
        openList.add(start); // add to the list of available cells

        // keep running while we still have cells to reach
        while (!openList.isEmpty()) {

            // grab the next best possible cell to visit
            Cell current = openList.peek();

            // remove current from the open list since we know we're looking at it and never will again
            openList.remove(current);

            // if we've arrived at the end cell
            if (current == allCells[endRow][endCol]) {
                return current;
            }

            // Reference Cell will see if we have a teleport or not
            Cell thisCell = null;

            // if we have chosen to take a teleport, we want to account for that
            if (current.teleportIndex > 0) {

                // find the destination cell and log it (initialized to purposefully be outside the map)
                int destRow = allCells.length + 5;
                int destCol = allCells.length + 5;
                outer:
                for (int i = 0; i < allCells.length; i++) {
                    for (int j = 0; j < allCells[i].length; j++) {

                        // if we're looking at the destination cell, mark it and leave the outer for loop
                        if (allCells[i][j] != null) {
                            if (allCells[i][j].teleportIndex == current.teleportIndex) {
                                if (i != current.row || j != current.col) {
                                    // we have found the destination cell!
                                    destRow = i;
                                    destCol = j;

                                    // we can stop looking now
                                    break outer;
                                }
                            }
                        }
                    }
                }

                // if we haven't found an appropriate location within the map
                if (destRow > allCells.length) {
                    System.out.println(destRow);
                    System.out.println("Matching teleport not found for teleport number " + current.teleportIndex +
                            ", please check your input map again");
                    System.exit(1);
                }
                // otherwise, we're good to continue
                else {

                    // hop along the destination teleport and log it (don't need to put it in open since
                    // it's not an option to go here or not. We're just moving right along to it and
                    // logging the parent accordingly.)
                    Cell teleDest = allCells[destRow][destCol];
                    // should add 0 to teleDest's weight compared to current
                    calculateMinimumDistance(teleDest, teleDest.incomingWeight, current);
                    teleDest.parent = current;

                    // we are all set with the teleport source.
                    // Remove it from the openList and add it to the closedList
                    closedList.add(current);

                    // derive the previous location from before the teleport
                    // to get a new location after the teleport
                    int dir = 0; // will be 0-3 based on where we CAME FROM (up, down, left, right resp.)
                    // came from down
                    if (current.parent.row > current.row)
                        dir = 1;
                        // came from left
                    else if (current.parent.col < current.col)
                        dir = 2;
                        // came from right
                    else if (current.parent.col > current.col)
                        dir = 3;
                    // came from up
                    // (otherwise, came from up)

                    // make a cell for the new location after the teleport based on this initial direction
                    Cell newLoc = null;

                    // boolean to keep track of cell found process (don't want to hop onto an F cell)
                    boolean cellFound = false;

                    // if we came from up, go down (but only if we can)
                    if (dir == 0 &&
                            teleDest.row < allCells.length - 1 &&
                            allCells[teleDest.row + 1][teleDest.col] != null) {
                        newLoc = allCells[teleDest.row + 1][teleDest.col];
                        cellFound = true;
                    }
                    // if we came from down, go up (but only if we can)
                    else if (dir == 1 &&
                            teleDest.row > 0 &&
                            allCells[teleDest.row - 1][teleDest.col] != null) {
                        newLoc = allCells[teleDest.row - 1][teleDest.col];
                        cellFound = true;
                    }
                    // if we came from left, go right (but only if we can)
                    else if (dir == 2 &&
                            teleDest.col < allCells[0].length - 1 &&
                            allCells[teleDest.row][teleDest.col + 1] != null) {
                        newLoc = allCells[teleDest.row][teleDest.col + 1];
                        cellFound = true;
                    }
                    // if we came from right, go left (but only if we can)
                    else if (dir == 3 &&
                            teleDest.col > 0 &&
                            allCells[teleDest.row][teleDest.col - 1] != null) {
                        newLoc = allCells[teleDest.row][teleDest.col - 1];
                        cellFound = true;
                    }

                    // if we didn't find a cell in this process (intended direction won't work)
                    if (!cellFound) {
                        // attempt down first
                        if (teleDest.row < allCells.length - 1 &&
                                allCells[teleDest.row + 1][teleDest.col] != null)
                            newLoc = allCells[teleDest.row + 1][teleDest.col];
                            // try up next
                        else if (teleDest.row > 0 &&
                                allCells[teleDest.row - 1][teleDest.col] != null)
                            newLoc = allCells[teleDest.row - 1][teleDest.col];
                            // then try right
                        else if (teleDest.col < allCells[0].length - 1 &&
                                allCells[teleDest.row][teleDest.col + 1] != null)
                            newLoc = allCells[teleDest.row][teleDest.col + 1];
                            // and finally left
                        else if (teleDest.col > 0 &&
                                allCells[teleDest.row][teleDest.col - 1] != null)
                            newLoc = allCells[teleDest.row][teleDest.col - 1];
                            // if we still don't have anything from this, in that a teleport is surrounded entirely
                            // unavailable spaces
                        else {
                            System.out.println("RARE CASE: TELEPORT CAN'T BE HOPPED OUT OF! TRY AGAIN!");
                            System.exit(0);
                        }
                    }

                    // should add newLoc's weight to teleDest's weight compared to current
                    calculateMinimumDistance(newLoc, newLoc.incomingWeight, teleDest);
                    newLoc.parent = teleDest;
                    // add to open
                    openList.add(newLoc);

                    closedList.add(teleDest);

                    // we're all set with teleDest now, so we can set that aside now as well
                    // no need to worry about the openList though, since we never added it there.

                    // if we just landed on the destination spot after the teleport
                    if (newLoc == allCells[endRow][endCol])
                        return newLoc;

                    // otherwise, handle this further like we would with current without the teleport logic...
                    else
                        thisCell = newLoc;
                }
            } else
                // thisCell will be in the openList, since it is equivalent to current as an object
                // therefore, when we delete it from OpenList later, it will be there for deletion
                thisCell = current;

            // we're on this cell, so we can remove it from open
            // teleport destination wouldn't be in the openList though?
            openList.remove(thisCell);

            // otherwise, view all the edges connected to this cell
            for (Edge edge : thisCell.neighbors) {

                // grab an adjacent cell and record its weight
                Cell next = edge.cell;

                // we'll observe this node if it is available and not the parent of our current node
                // && thisCell.parent != next
                if (!closedList.contains(next)) {
                    // update minimum distance if we aren't simply going back to the parent
                    calculateMinimumDistance(next, next.incomingWeight, thisCell);
                    // add to open
                    openList.add(next);
                }
            }
            // since we have observed all the edges, we're all set with this cell
            closedList.add(thisCell);
        }
        // return this if we have not reached the end cell, since reaching the end cell wil trigger a different return
        return null;
    }

    public static void calculateMinimumDistance(Cell evaluationCell, int edgeWeigh, Cell sourceCell) {
        if (sourceCell.f + edgeWeigh < evaluationCell.f) {
            evaluationCell.f = sourceCell.f + edgeWeigh;
            evaluationCell.parent = sourceCell;
        }
    }

    public static void printPath(Cell target) {

        // begin from our destination cell and track along this changing cell
        Cell n = target;

        // if there's nothing here, output nothing (should not happen)
        if (n == null) {
            System.out.println("Parenting chain has failed!");
            return;
        }

        // a list of the coordinates that we want to display to the user at the end of the program
        List<Integer[]> coords = new ArrayList<>();

        // iterate while there are still parents to trace back to
        while (n.parent != null) {
            coords.add(new Integer[]{n.row, n.col});
            n = n.parent;
        }
        // add the start cell coordinates as well after the loop
        coords.add(new Integer[]{n.row, n.col});

        // reverse the order of the collection, so we can see the coordinates from start to end rather than end to start
        Collections.reverse(coords);

        // loop on the coordinate list and display them
        for (Integer[] coord : coords) {
            System.out.print("[" + (coord[0] + 1) + "," + (coord[1] + 1) + "], ");
        }
    }
}
