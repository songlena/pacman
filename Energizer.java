package pacman;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class Energizer implements Collidable{
    private Pane gamePane;
    private pacmanGame game;
    private Circle energizer;
    public Energizer(Pane gamePane, pacmanGame game, int x, int y){
        this.gamePane = gamePane;
        this.game = game;
        //new circle of specific dimensions, center x and y locations with an offset, and color
        this.energizer = new Circle((x * Constants.SQUARE_SIZE) + Constants.SPACING_OFFSET,
                (y * Constants.SQUARE_SIZE) + Constants.SPACING_OFFSET,
                Constants.ENERGIZER_RADIUS, Constants.DOT_COLOR);
        //adds the circle graphically to the gamePane
        this.gamePane.getChildren().add(this.energizer);
    }
    /**
     * Implements the interface Collidable with the method collide. Once the pacman collides with the energizer, the
     * score updates by 100, and the energizer is removed graphically from the gamePane. This method is called in the
     * checkCollision method of the pacmanGame class. Additionally, the setFrightMode method is called from the
     * pacmanGame class since the Pacman "eats" an energizer.
     */
    @Override
    public void collide() {
        this.game.scoreUpdate(Constants.ENERGIZER_SCORE);
        this.gamePane.getChildren().remove(this.energizer);
        this.game.setFrightMode(true);
    }
}
