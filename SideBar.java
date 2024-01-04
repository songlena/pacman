package pacman;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class SideBar {
    private HBox bottomPane;
    private Label scoreLabel;
    private Label livesLabel;
    private BorderPane root;
    private int score;
    private int lives;

    public SideBar(BorderPane root){
        this.root = root;
        this.score = 0;
        this.lives = 3;
        this.setUpSideBar(); //sets up the entire bottomPane, buttons, and labels
    }
    /**
     * This method creates a new bottomPane HBox, adds the bottomPane to the root BorderPane, sets the color of the
     * bottomPane to lightblue, creates a quit button, sets the texts of the lives and score label, and adds the
     * button and labels to the bottomPane.
     */
    private void setUpSideBar() {
        this.bottomPane = new HBox();
        this.bottomPane.setAlignment(Pos.CENTER);
        this.root.setBottom(this.bottomPane);
        this.bottomPane.setStyle("-fx-background-color: lightblue");
        this.bottomPane.setPrefSize(Constants.SCENE_WIDTH - Constants.SQUARE_SIZE, Constants.SQUARE_SIZE);
        this.bottomPane.setSpacing(Constants.SIDEBAR_SPACING);
        this.bottomPane.setFocusTraversable(false);

        Button b1 = new Button("Quit");
        b1.setOnAction((ActionEvent e) -> System.exit(0));
        this.livesLabel = new Label("Lives: 3");
        this.scoreLabel = new Label("Score: 0");
        this.bottomPane.getChildren().addAll(this.livesLabel, this.scoreLabel, b1);
    }
    /**
     * This method is responsible for updating the score label and is called in the pacmanGame class.
     */
    public void updateScore(int score) {
        this.score += score;
        this.scoreLabel.setText("Score: " + this.score);
    }
    /**
     * This method is responsible for updating the lives label and is called in the pacmanGame class.
     */
    public void updateLives() {
        this.lives -= 1;
        this.livesLabel.setText("Lives: " + this.lives);
    }
}
