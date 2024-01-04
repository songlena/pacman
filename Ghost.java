package pacman;

//import com.sun.javafx.scene.traversal.Direction;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

//import static pacman.GameMode.*;

public class Ghost implements Collidable {
    private Pane gamePane;
    private pacmanGame game;
    private Rectangle ghost;
    private Pacman pacman;
    private Color color;
    private smartSquare[][] maze;
    private Direction currDirection;
    private int startRow;
    private int startCol;

    public Ghost(Pane gamePane, pacmanGame game, int j, int i, Pacman pacman, Color color) {
        this.gamePane = gamePane;
        this.game = game;
        this.pacman = pacman;
        this.color = color;
        this.startRow = i;
        this.startCol = j;
        //sets the x and y location of the ghost and the width and height to 20. Takes in j and i as locations from the
        //maze 2D array of 23x23 in the pacmanGame class.
        this.ghost = new Rectangle(j * Constants.SQUARE_SIZE, i * Constants.SQUARE_SIZE, Constants.SQUARE_SIZE,
                Constants.SQUARE_SIZE);
        this.ghost.setFill(color);
        this.ghost.toFront();
        //sets the direction to up because the ghost needs to stay stationary at the beginning of the game, and it
        //can only logically move left or right.
        this.currDirection = Direction.UP;
        this.maze = this.game.getMaze();
        //adds the ghost rectangle to the gamePane.
        this.gamePane.getChildren().add(this.ghost);
    }
    /**
     * If pacman collides with a ghost and the ghost is in fright mode, the game adds the ghost back to the pen,
     * and updates the score by 200. If the ghost is not in fright mode and the pacman collides with it, the game
     * resets where the pacman goes back to the original center location and the lives decrement.
     */
    @Override
    public void collide() {
        if (this.game.getFrightMode()) {
            this.game.addToPen(this);
            this.game.ghostScoreUpdate(Constants.GHOST_SCORE);
        } else {
            this.game.reset();
        }
    }
    /**
     * This method is called in the pacmanGame class under the other reset method. It removes the ghost from the
     * gamePane, sets the ghost x and y locations back to the original location of the ghost, and adds the ghost
     * back to the gamePane graphically.
     */
    public void reset() {
        this.gamePane.getChildren().remove(this.ghost);
        this.ghost.setX(this.startCol * Constants.SQUARE_SIZE);
        this.ghost.setY(this.startRow * Constants.SQUARE_SIZE);
        this.gamePane.getChildren().add(this.ghost);
    }
    /**
     * This method graphically brings the ghost to the front so that it is visible during gameplay.
     */
    public void bringToFront() {
        this.ghost.toFront();
    }
    /**
     * This method sets the ghost's x and y location based on values passed through the parameter. It is called in
     * the initPen() method in pacmanGame.
     */
    public void setGhostLoc(double xLoc, double yLoc) {
        this.ghost.setX(xLoc);
        this.ghost.setY(yLoc);
    }
    /**
     * This method gets the ghost's column location. I divided the ghost's x location by 20 since that is the size of
     * the square and the method is used for the 2D array maze which needs integers from 1-23.
     */
    public int getGhostCol() {
        return (int) this.ghost.getX()/Constants.SQUARE_SIZE;
    }
    /**
     * This method gets the ghost's row location. I divided the ghost's y location by 20 since that is the size of
     * the square and the method is used for the 2D array maze which needs integers from 1-23.
     */
    public int getGhostRow() {
        return (int) this.ghost.getY()/Constants.SQUARE_SIZE;
    }
    /**
     * This method is in charge of wrapping the ghost across the screen given specific row and col locations. The
     * direction is then set to either left or right depending on which way the ghost wraps across the screen. This
     * method is called within the Ghost class.
     */
    private Direction wrapGhost(int row, int col) {
        if (col == Constants.COL_MIN && row == Constants.ROW_MAX/2) {
            this.ghost.setX(Constants.COL_MAX * Constants.SQUARE_SIZE);
            return Direction.LEFT;
        } else if (col == Constants.COL_MAX && row == Constants.ROW_MAX/2) {
            this.ghost.setX(Constants.COL_MIN);
            return Direction.RIGHT;
        } else return this.currDirection;
    }
    /**
     * First, this method removes the ghost from its current position in the maze. Then, it checks to see
     * if the game is in fright mode. If so, the ghost's direction is set by calling the getNextDirection method and the
     * color of the ghost changes to light blue. Else, it determines the direction of ghost movement using ghostBFS
     * based on the given coordinate and sets the ghost's color to its original. After updating the ghost's current
     * direction, the ghost is added back to its new position in the maze for collision detection.
     */
    public void setDirection(BoardCoordinate coordinate) {
        int row = this.getGhostRow();
        int col = this.getGhostCol();

        this.maze[row][col].deleteCollidables(this);

        Direction direction;
        if (this.game.getFrightMode()) {
            direction = this.getNextDirection(row, col);
            this.ghost.setFill(Constants.GHOST_FRIGHT);
        } else {
            direction = this.ghostBFS(coordinate);
            this.ghost.setFill(this.color);
        }
        this.currDirection = direction;
        this.ghostMovement(direction);

        row = this.getGhostRow();
        col = this.getGhostCol();

        this.maze[row][col].collidables(this);

    }
    /**
     * Ghost movement is in charge of graphically moving the ghosts left, right, up, and down by setting the ghost's
     * x and y locations to the current ghost's x or y location +/-20 to illustrate movement by one square.
     */
    private void ghostMovement(Direction dir) {
        if (dir != null) {
            switch (dir) {
                case RIGHT:
                    this.ghost.setX(this.ghost.getX() + Constants.SQUARE_SIZE);
                    break;
                case LEFT:
                    this.ghost.setX(this.ghost.getX() - Constants.SQUARE_SIZE);
                    break;
                case UP:
                    this.ghost.setY(this.ghost.getY() - Constants.SQUARE_SIZE);
                    break;
                case DOWN:
                    this.ghost.setY(this.ghost.getY() + Constants.SQUARE_SIZE);
                    break;
            }
        }
    }
    /**
     * This method calculates the next move for a ghost. First, it ensures valid movement within the maze's constraints.
     * If so, it evaluates all possible directions- up, down, left, right- and adds those that lead to a valid cell/
     * aren't in the opposite direction to the current direction to a list of valid directions. If the column is not
     * within the valid range, wrapGhost is called to handle edge cases. Then, the method selects a random direction
     * from the list of valid ones or keeps the current direction if no valid directions are available, updating the
     * ghost's current direction accordingly. This method is called within the Ghost class's setDirection method for
     * the Ghost's frighten mode, since the frighten mode indicates random ghost movement.
     */
    private Direction getNextDirection(int row, int col) {
        ArrayList<Direction> validDirs = new ArrayList<>();

        if (col > Constants.COL_MIN && col < Constants.COL_MAX) {
            for (Direction dir : Direction.values()) {

                int newRow = this.newRow(dir, row);
                int newCol = this.newCol(dir, col);

                if (this.isValidCell(newRow, newCol) && this.currDirection != dir.getOpposite()) {
                    validDirs.add(dir);
                }
            }
        } else {
            return this.wrapGhost(row, col);
        }

        if (!validDirs.isEmpty()) {
            int randIndex = (int) (Math.random() * validDirs.size());
            Direction dir = validDirs.get(randIndex);
            this.currDirection = dir;
            return dir;
        } else {
            return this.currDirection;
        }
    }
    /**
     * This method returns true or false based on whether a cell is valid for ghost movement by checking to see if the
     * square in the maze at a given row and col is black, meaning it's not a wall.
     */
    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < this.maze.length && col >= 0 && col < this.maze[0].length &&
                this.maze[row][col].getFill() == Constants.EMPTY_SPACE;
    }
    /**
     * The ghostBFS method first initializes a 2D array to store potential movement directions and a queue for BFS
     * traversal. wrapGhost is called to handle edge cases. The method starts BFS from the ghost's current location,
     * adding neighboring cells to the queue through the getNeighbors method within the Ghost class. The method
     * calculates the distance of each visited cell to the target, and if a cell's distance is shorter than the
     * previously recorded min distance, the method updates the min distance. The method continuously explores
     * neighboring cells, updating the min distance and corresponding direction. This ensures that
     * the ghost takes the shortest path to the target, taking into account all the possible routes the ghost can make
     * through the maze. A while loop is implemented to check if the queue is not empty. Until the queue is empty,
     * the method will continue checking for the min distance to the target.
     */
    public Direction ghostBFS(BoardCoordinate target) {
        //new 2D array maze that holds potential directions
        Direction[][] directionMaze = new Direction[Constants.MAZE_SIZE][Constants.MAZE_SIZE];
        Queue<BoardCoordinate> queue = new LinkedList<>(); //first in first out queue
        Direction direction = null;

        int row = this.getGhostRow();
        int col = this.getGhostCol();
        if (!(col > Constants.COL_MIN && col < Constants.COL_MAX)) {
            return this.wrapGhost(row, col); // handling edge case when the ghost is at not within the boundary, wraps
        }
        BoardCoordinate ghostLoc = new BoardCoordinate(row, col, false); // ghost's initial position
        this.getNeighbors(directionMaze, ghostLoc, queue, true);
        double minDistance = Constants.MIN_DISTANCE; //other distances can't be as big as this distance

        while (!queue.isEmpty()) {
            BoardCoordinate newLoc = queue.remove();

            double distance = this.calcDistance(newLoc, target);
            if (distance < minDistance) { //checking to see if the current distance of the ghost is less than minDist
                minDistance = distance;
                direction = directionMaze[newLoc.getRow()][newLoc.getColumn()];

            }
            this.getNeighbors(directionMaze,newLoc, queue, false);
        }
        this.currDirection = direction;
        return direction;
    }
    /**
     * This method is called by the ghostBFS method above. It iterates over possible directions-
     * up, down, left, right- from the current position and finds the coordinates of adjacent cells. For each
     * neighboring cell, the method checks if it is within the maze boundaries and not in the opposite direction of the
     * current ghost movement. It also checks for valid path movement based on square color (should be black).
     * When a valid neighboring cell is found, the method sets or updates its direction in the direction 2D array,
     * which keeps track of the movement directions used to reach each cell. This is then added to a queue, alongside
     * the board coordinates. This method aids in ghostBFS functionality.
     */
    private void getNeighbors(Direction[][] directionMaze, BoardCoordinate currentLoc, Queue<BoardCoordinate> queue,
                              boolean init) {
        int row = currentLoc.getRow();
        int col = currentLoc.getColumn();
        if (col > Constants.COL_MIN && col < Constants.COL_MAX) {
            for (Direction dir : Direction.values()) {
                int nextRow = this.newRow(dir, row); //check for all valid directions
                int nextCol = this.newCol(dir, col);
                if (this.maze[nextRow][nextCol].getFill() == Constants.EMPTY_SPACE
                        && this.currDirection != dir.getOpposite() && directionMaze[nextRow][nextCol] == null) {
                    if (init) {
                        //sets a direction for the direction maze if init is true, first call
                        directionMaze[nextRow][nextCol] = dir;
                    } else {
                        directionMaze[nextRow][nextCol] = directionMaze[row][col];
                    }
                    queue.add(new BoardCoordinate(nextRow, nextCol, true));
                }
            }
        }
    }
    /**
     * The calcDistance method is used by the ghostBFS method to compare the distance between the minimum distance
     * value and the new stored distance value to the target. This calculation is found by taking the square root
     * of x^2 + y^2, which gives the overall euclidean distance between the current location and the target.
     */
    private double calcDistance(BoardCoordinate current, BoardCoordinate target) {
        double X = current.getColumn() * Constants.SQUARE_SIZE - target.getColumn() * Constants.SQUARE_SIZE;
        double Y = current.getRow() * Constants.SQUARE_SIZE - target.getRow() * Constants.SQUARE_SIZE;
        return Math.sqrt((X * X) + (Y * Y));
    }
    /**
     * This method is called in the getNextDirection method from the Ghost class and essentially aids in the process of
     * determining the next rows of the ghost's movement by adding and subtracting once from the current row value
     * passed into the parameter.
     */
    private int newRow(Direction dir, int currRow) {
        switch (dir) {
            case UP:
                return currRow - Constants.UNIT_INCREMENT;
            case DOWN:
                return currRow + Constants.UNIT_INCREMENT;
            default:
                return currRow;
        }
    }
    /**
     * This method is called in the getNextDirection method from the Ghost class and essentially aids in the process of
     * determining the next columns of the ghost's movement by adding and subtracting once from the current column value
     * passed into the parameter.
     */
    private int newCol(Direction dir, int currCol) {
        switch (dir) {
            case LEFT:
                return currCol - Constants.UNIT_INCREMENT;
            case RIGHT:
                return currCol + Constants.UNIT_INCREMENT;
            default:
                return currCol;
        }
    }

}