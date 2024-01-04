package pacman;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Dot implements Collidable{
    private Pane gamePane;
    private pacmanGame game;
    private Circle dot;
    public Dot(Pane gamePane, pacmanGame game, int x, int y){
        this.gamePane = gamePane;
        this.game = game;
        //new circle of specific dimensions, center x and y locations with an offset, and color
        this.dot = new Circle((x * Constants.SQUARE_SIZE) + Constants.SPACING_OFFSET,
                (y * Constants.SQUARE_SIZE) + Constants.SPACING_OFFSET, Constants.DOT_RADIUS,
                Constants.DOT_COLOR);
        //adds the circle graphically to the gamePane
        this.gamePane.getChildren().add(this.dot);
    }
    /**
     * Implements the interface Collidable with the method collide. Once the pacman collides with the dot, the score
     * updates by 10, and the dot is removed graphically from the gamePane. This method is called in the checkCollision
     * method of the pacmanGame class.
     */
    @Override
    public void collide() {
        this.game.scoreUpdate(Constants.DOT_SCORE);
        this.gamePane.getChildren().remove(this.dot);
    }
}
