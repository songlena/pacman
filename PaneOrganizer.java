package pacman;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class PaneOrganizer {
    private BorderPane root;
    private Pane gamePane;
    private pacmanGame game;
    private SideBar sideBar;
    /**
     * Constructor creates the borderpane root, adds sideBar to the root, and instantiates a new instance of pacmanGame
     **/
    public PaneOrganizer(){
        this.root = new BorderPane();
        this.setUpGamePane();
        this.sideBar = new SideBar(this.root);
        this.game = new pacmanGame(this.gamePane, this.sideBar);
    }
    /**
     * returns the root BorderPane
     **/
    public BorderPane getRoot(){
        return this.root;
    }
    /**
     * creates a new pane called gamePane and adds the gamePane to the root
     **/
    private void setUpGamePane() {
        this.gamePane = new Pane();
        this.root.setCenter(this.gamePane);
        this.gamePane.setFocusTraversable(true);
    }
}
