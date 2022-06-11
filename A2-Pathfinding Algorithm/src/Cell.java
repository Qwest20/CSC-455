import java.util.ArrayList;

public class Cell implements Comparable<Cell> {

    // coordinates
    public final int row;
    public final int col;
    
    // weight for when cell is traversed onto
    public final int incomingWeight;

    // for teleport cells only! Starts at 0 if otherwise
    // can be checked via (is teleport index greater than 0, since there will never be a T0)
    public int teleportIndex = 0;

    // Parent in the path
    public Cell parent = null;

    // collection of edges to observe neighboring cells
    public ArrayList<Edge> neighbors;

    // Sum for cell analysis
    public int f = Integer.MAX_VALUE;
    // Weight summation from the start cell
    public int g = Integer.MAX_VALUE;
    // Heuristic for Manhattan distance from goal
    public int h = 0;

    // Constructor which additionally interprets incoming weight
    public Cell(int row, int col, String val) {
        this.row = row;
        this.col = col;
        this.neighbors = new ArrayList<>();
        if(val.equals("F"))
            incomingWeight = Integer.MAX_VALUE;
        else if(val.contains("T")) {
            teleportIndex = Integer.parseInt(val.replaceAll("T", ""));
            incomingWeight = 0;
        }
        else
            incomingWeight = Integer.parseInt(val);
    }

    // compareTo method for tracking the PQ's in Algorithm
    @Override
    public int compareTo(Cell o) {
        return Double.compare(this.f, o.f);
    }

    // add a new branch
    public void addBranch(int weight, Cell c) {
        Edge newEdge = new Edge(weight, c);
        neighbors.add(newEdge);
    }

    // return the Manhattan distance from given destination coordinate
    public void calculateHeuristic(int fRow, int fCol) {
        this. h = (Math.abs(fRow-this.row) + Math.abs(fCol - this.col));
    }

    // simply update the f value given the g and h values
    public void updateScore() {
        f = g+h;
    }
}