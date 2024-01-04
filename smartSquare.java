package pacman;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class smartSquare{
    private Pane gamePane;
    private Rectangle square;
    private ArrayList<Collidable> collidable;

    /**
     * Constructor that instantiates a new square of type rectangle width the width and height of 20. ArrayList of
     * collidables is also instantiated, which will contain dots, energizers, and ghosts.
     */
    public smartSquare(Pane gamePane){
        this.gamePane = gamePane;
        this.square = new Rectangle(Constants.SQUARE_SIZE, Constants.SQUARE_SIZE);
        this.square.setFill(Constants.EMPTY_SPACE);
        this.collidable = new ArrayList<>();
        gamePane.getChildren().add(this.square);
    }
    /**
     * This method is used in pacmanGame to generate the map of squares and sets the row location of the square to the
     * parameter passed in.
     */
    public void setRow(int rowNum){
        this.square.setY(rowNum);
    }
    /**
     * This method is used in pacmanGame to generate the map of squares and sets the column location of the square to
     * the parameter passed in.
     */
    public void setCol(int colNum){
        this.square.setX(colNum);
    }
    /**
     * This method sets the wall color to the color that is passed in and changes the color of the individual square to
     * dark blue. It is used in the generate map method in pacmanGame.
     */
    public void setWall(Color color) {
        this.square.setFill(color);
    }
    /**
     * This method returns the color of an individual square and is used to check for valid pacman and ghost movement.
     */
    public Color getFill() {
        return (Color) this.square.getFill();
    }
    /**
     * This method is used in pacmanGame to add collidables like Ghost, Dot, and Energizer to the ArrayList of
     * collidables.
     */
    public void collidables(Collidable collidable) {
        this.collidable.add(collidable);
    }
    /**
     * This method is used in pacmanGame to delete collidables from the ArrayList of collidables.
     */
    public void deleteCollidables(Collidable collidable) {
        this.collidable.remove(collidable);
    }
    /**
     * This method is used to clear collidables from the ArrayList of collidables and is used in the
     * checkCollision method of pacmanGame.
     */
    public void clear(){
        this.collidable.clear();
    }
    /**
     * This method is used to return collidables from the ArrayList of collidables and is implemented in the
     * checkCollision method of pacmanGame.
     */
    public ArrayList<Collidable> getCollidables() {
        return this.collidable;
    }
}