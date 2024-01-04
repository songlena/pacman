package pacman;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class Pacman{
    private Pane gamePane;
    private pacmanGame game;
    private Circle pacman;
    private Direction movement;
    private smartSquare [][] maze;
    public Pacman(Pane gamePane, pacmanGame game, int x, int y, smartSquare[][] maze){
        this.gamePane = gamePane;
        this.game = game;
        this.movement = Direction.DOWN;
        this.maze = maze;
        //sets the circle's center x and y's with an offset, the circle's radius, and the circle's color to yellow
        this.pacman = new Circle((x * Constants.SQUARE_SIZE) + Constants.SPACING_OFFSET,
                (y * Constants.SQUARE_SIZE) + Constants.SPACING_OFFSET, Constants.PACMAN_RADIUS,
                Constants.PACMAN_COLOR);
        //graphically adds pacman circle to the gamePane
        this.gamePane.getChildren().add(this.pacman);
    }
    /**
     * Method used in pacmanGame to set pacman's start location to the parameter passed in (of type BoardCoordinate).
     * It is called in the pacman reset method.
     */
    public void setStartLocation(BoardCoordinate start){
        this.pacman.setCenterX(start.getColumn() * Constants.SQUARE_SIZE + Constants.SPACING_OFFSET);
        this.pacman.setCenterY(start.getRow() * Constants.SQUARE_SIZE + Constants.SPACING_OFFSET);
    }
    /**
     * Method returns an integer of pacman's center y location
     */
    public int getCenterY(){
        return (int) this.pacman.getCenterY();
    }
    /**
     * Method returns an integer of pacman's center x location
     */
    public int getCenterX(){
        return (int) this.pacman.getCenterX();
    }
    /**
     * Method that checks if pacman can move given parameters y and x. This means that the maze square's color can't
     * be the same color as the wall in order for pacman to be able to move. Called throughout the Pacman class.
     */
    public boolean validMovement(int y, int x) {
        int row = (int) (this.pacman.getCenterY()/Constants.SQUARE_SIZE);
        int col = (int) (this.pacman.getCenterX()/Constants.SQUARE_SIZE);
        if(this.maze[row + y][col + x].getFill() != Constants.WALL_COLOR){
            return true;
        }
        return false;
    }
    /**
     * Wraps the pacman around the screen once it reaches the edge. If the location of the pacman is less than or equal
     * to 10, the pacman's center is set to 450. If the location of the pacman is greater than or equal to 450, the
     * pacman's center is set to 10.
     */
    public void checkWrap() {
        double newPacmanXLoc = this.pacman.getCenterX();
        if (newPacmanXLoc <= Constants.PACMAN_LEFT_BORDER) {
            this.pacman.setCenterX(Constants.PACMAN_RIGHT_BORDER);
            return;
        }
        if (newPacmanXLoc >= Constants.PACMAN_RIGHT_BORDER) {
            this.pacman.setCenterX(Constants.PACMAN_LEFT_BORDER);
        }
    }
    /**
     * This method moves the pacman and is called in pacmanGame through a method associated with the timeline. It uses
     * validMovement() from above to check if the pacman is not moving into a wall. In each case, the
     * parameters of x and y are different to accommodate for the specific direction. Then, the center of the pacman
     * is set to an increment of the square width in each direction case.
     */
    public void movePacman(Direction movement) {
        switch (movement) {
            case RIGHT:
                if (this.validMovement(Constants.PACMAN_RIGHT_Y, Constants.PACMAN_RIGHT_X)) {
                    this.pacman.setCenterX(this.pacman.getCenterX() + Constants.SQUARE_SIZE);
                }
                break;
            case LEFT:
                if (this.validMovement(Constants.PACMAN_LEFT_Y, Constants.PACMAN_LEFT_X)) {
                    this.pacman.setCenterX(this.pacman.getCenterX() - Constants.SQUARE_SIZE);
                }
                break;
            case UP:
                if (this.validMovement(Constants.PACMAN_UP_Y, Constants.PACMAN_UP_X)) {
                    this.pacman.setCenterY(this.pacman.getCenterY() - Constants.SQUARE_SIZE);
                }
                break;
            case DOWN:
                if (this.validMovement(Constants.PACMAN_DOWN_Y, Constants.PACMAN_DOWN_X)) {
                    this.pacman.setCenterY(this.pacman.getCenterY() + Constants.SQUARE_SIZE);
                }
                break;
        }
    }
    /**
     * This method sets the direction of the pacman to right, left, up, or down given the direction parameter that
     * is passed in through the pacmanGame class and the current direction. This means that the pacman will continuously
     * move in the given direction once an arrow key is pressed without having to continually press/hold the key down.
     * The validMovement() method from above is called again with slightly modified x and y values, and if the
     * statement returns true in each case, then the direction is set to whatever "movement" is passed within the
     * parameter.
     */
    public Direction setMovement(Direction movement, Direction current) {
            switch (movement) {
                case RIGHT:
                    if (this.validMovement(Constants.PACMAN_RIGHT_Y, Constants.PACMAN_RIGHT_X)) {
                        return Direction.RIGHT;
                    }
                    break;
                case LEFT:
                    if (this.validMovement(Constants.PACMAN_LEFT_Y, Constants.PACMAN_LEFT_X)) {
                        return Direction.LEFT;
                    }
                    break;
                case UP:
                    if (this.validMovement(Constants.PACMAN_UP_Y, Constants.PACMAN_UP_X)) {
                        return Direction.UP;
                    }
                    break;
                case DOWN:
                    if (this.validMovement(Constants.PACMAN_DOWN_Y, Constants.PACMAN_DOWN_X)) {
                        return Direction.DOWN;
                    }
                    break;
            }
            return current;
    }
    /**
     * graphically brings the pacman to the front of the screen
     */
    public void bringToFront(){
        this.pacman.toFront();
    }
}
