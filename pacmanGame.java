package pacman;


import cs15.fnl.pacmanSupport.CS15SquareType;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class pacmanGame {
    private Pane gamePane;
    private SideBar sideBar;
    private smartSquare[][] maze;
    private Timeline timeline;
    private Pacman pacman;
    private Queue<Ghost> ghostPen;
    private Ghost[] ghosts;
    private Direction movement;
    private boolean gameHasEnded = false;
    private boolean isPaused = false;
    private double counter;
    private double countFright;
    private double counterPen;
    public boolean frightMode;
    private int countDot;
    private int countLives;
    private BoardCoordinate pacmanStartLoc;
    private Label labelPause;

    public pacmanGame(Pane gamePane, SideBar sideBar) {
        this.gamePane = gamePane;
        this.sideBar = sideBar;
        this.maze = new smartSquare[Constants.MAZE_SIZE][Constants.MAZE_SIZE];
        this.ghostPen = new LinkedList<>();
        this.counter = 0;
        this.counterPen = 0;
        this.countFright = 0;
        this.countDot = 0;
        this.countLives = 3;
        this.ghosts = new Ghost[Constants.GHOST_ARRAY];
        this.movement = Direction.DOWN;
        this.frightMode = false;
        this.labelPause = new Label("Game Paused");
        this.pauseLabel(this.labelPause);
        this.generateMap();
        this.setUpTimeline();
        this.gamePane.setOnKeyPressed((KeyEvent e) -> this.handleKeyPress(e));
        this.gamePane.setFocusTraversable(true);
    }
    /**
     * This method resets the game by setting the start location of pacman to the boardBoordinate center start location
     * again, updating the total lives left, looping through the array of ghosts and reseting their positions, and it
     * is called in the ghost class under the collide override method.
     */
    public void reset() {
        this.counterPen = 0;
        this.countFright = 0;
        this.counter = 0;
        this.frightMode = false;
        this.movement = Direction.DOWN;
        for (int i = 0; i < this.ghosts.length; i++) {
            this.ghosts[i].reset();
            if (i > 0) {
                this.ghostPen.add(this.ghosts[i]);
            }
        }
        this.pacman.setStartLocation(this.pacmanStartLoc);
        this.livesUpdate();
    }
    /**
     * This method generates the pacman game map. maze is a 2D array of type smartSquare, and I loop through the
     * 23x23 map using two for loops and set the square row and column to i * 20 and j * 20 since the square's width is
     * 20. Then, with the help of the supportMap, I used a switch statement to differentiate between the locations of
     * the walls, the energizers, the dots, the ghosts, and the pacman to generate the initial map.
     */
    private void generateMap() {
        CS15SquareType[][] supportMap = cs15.fnl.pacmanSupport.CS15SupportMap.getSupportMap();
        for (int i = 0; i < Constants.MAZE_SIZE; i++) {
            for (int j = 0; j < Constants.MAZE_SIZE; j++) {
                this.maze[i][j] = new smartSquare(this.gamePane);
                this.maze[i][j].setRow(i * Constants.SQUARE_SIZE); //square width
                this.maze[i][j].setCol(j * Constants.SQUARE_SIZE);
            }
        }
        for (int i = 0; i < Constants.MAZE_SIZE; i++) { //i is row
            for (int j = 0; j < Constants.MAZE_SIZE; j++) { //j is column
                CS15SquareType element = supportMap[i][j];
                switch (element) {
                    case WALL:
                        this.maze[i][j].setWall(Constants.WALL_COLOR);
                        break;
                    case PACMAN_START_LOCATION:
                        this.pacman = new Pacman(this.gamePane, this, j, i, this.getMaze());
                        this.pacmanStartLoc = new BoardCoordinate(i, j, false);
                        break;
                    case DOT:
                        Dot dot = new Dot(this.gamePane, this, j, i);
                        this.maze[i][j].collidables(dot);
                        break;
                    case ENERGIZER:
                        Energizer energizer = new Energizer(this.gamePane, this, j, i);
                        this.maze[i][j].collidables(energizer);
                        break;
                    case GHOST_START_LOCATION:
                        this.setUpGhosts(i, j);
                        break;
                    default:
                        break;
                }
            }
        }
        this.bringToFront();
    }
    /**
     * This method is called in the generateMap() method above for the switch case of ghosts. ghosts is an array of
     * type Ghost, and I created four ghosts in this method and tweaked the parameters slightly to differentiate the
     * location and colors of each ghost. Then, I looped through the array of ghosts and added them to the ArrayList of
     * collidables established in the smartSquare class.
     */
    private void setUpGhosts(int i, int j) {
        this.ghosts[0] = new Ghost(this.gamePane, this, j, i - Constants.GHOST0_SETUP, this.pacman,
                Constants.GHOST_0);
        this.ghosts[1] = new Ghost(this.gamePane, this, j - Constants.UNIT_INCREMENT, i, this.pacman,
                Constants.GHOST_1);
        this.ghosts[2] = new Ghost(this.gamePane, this, j, i, this.pacman, Constants.GHOST_2);
        this.ghosts[3] = new Ghost(this.gamePane, this, j + Constants.UNIT_INCREMENT, i, this.pacman,
                Constants.GHOST_3);
        for (int index = 0; index < this.ghosts.length; index++) {
            this.maze[i][j].collidables(this.ghosts[index]);
            if (index > 0) this.ghostPen.add(this.ghosts[index]);
        }
    }
    /**
     * This method brings the pacman and the ghosts to the front of the map so that they are visible.
     */
    private void bringToFront() {
        this.pacman.bringToFront();
        for (int i = 0; i < this.ghosts.length; i++) {
            this.ghosts[i].bringToFront();
        }
    }
    /**
     * This method sets up the timeline/animation of the game and continuously calls the gameUpdate method which
     * contains many key methods for the function of the game.
     */
    private void setUpTimeline() {
        KeyFrame kf = new KeyFrame(Duration.seconds(Constants.DURATION),
                (ActionEvent e) -> this.gameUpdate());
        this.timeline = new Timeline(kf);
        this.timeline.setCycleCount(Animation.INDEFINITE);
        this.timeline.play();
    }
    /**
     * The gameUpdate() method is called in the setUpTimeline() method and calls many other important methods in the
     * pacmanGame class and from the Pacman class. For example, the pacman must constantly be moving, wrap movement
     * must constantly be checked, pacman collisions must constantly be checked, and the game over method should be
     * continuously called to determine if the player wins or loses.
     */
    private void gameUpdate() {
        this.initPen();
        this.pacman.movePacman(this.movement);
        this.pacman.checkWrap();
        this.checkCollision();
        this.updateGameMode();
        this.checkCollision();
        this.checkGameOver();
    }
    /**
     * This method checks to see if the game is over. If the total lives = 0 or the dot count is 186 (the counter
     * goes up after each dot or energizer collision with the pacman, meaning the pacman eats everything), then I check
     * for the condition of game over or winner. If the lives = 0, the screen will display Game Over, and else, the
     * screen will display Winner. The timeline is then stopped.
     */
    private void checkGameOver() {
        if ((this.countLives == Constants.LIVES_END) || (this.countDot == Constants.COUNT_DOT_WINS)) {
            if (this.countLives == Constants.LIVES_END) {
                this.gameOver("Game Over");
            } else {
                this.gameOver("Winner!");
            }
            this.gameHasEnded = true;
            this.gamePane.setFocusTraversable(false);
            this.timeline.stop();
        }
    }
    /**
     * This method is called by checkGameOver() from above and takes in a String parameter. A label is created and then
     * added to a VBox. Next, the VBox is added to the gamePane and the layout is set.
     */
    private void gameOver(String labelText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font: italic bold 30px arial, serif; -fx-text-fill: #FFFB00;");
        VBox labelBox = new VBox(label);
        labelBox.setAlignment(Pos.CENTER);
        labelBox.setPrefHeight(this.gamePane.getHeight());
        labelBox.setPrefWidth(this.gamePane.getWidth());

        this.gamePane.getChildren().add(labelBox);
    }

    /**
     * Sets up the format for the pause label.
     */
    private void pauseLabel(Label label) {
        label.setStyle("-fx-font: italic bold 20px arial, serif; -fx-text-fill: #FFFB00;");
    }
    /**
     * This method is responsible for handling the key press events. In each case of the switch, I check to see if the
     * timeline status is still running, and if so, depending on the arrow direction that is pressed, the pacman will
     * move in that given direction. setMovement is a method defined in the Pacman class that takes in parameters
     * for direction and the current direction of pacman. This means that the pacman will continuously move in the
     * direction of the arrow key pressed until another valid movement key press is made.
     */
    private void handleKeyPress(KeyEvent e) {
        switch (e.getCode()) {
            case RIGHT:
                System.out.println("reached");
                if (this.timeline.getStatus() == Animation.Status.RUNNING)
                    this.movement = this.pacman.setMovement(Direction.RIGHT, this.movement);
                break;
            case LEFT:
                if (this.timeline.getStatus() == Animation.Status.RUNNING)
                    this.movement = this.pacman.setMovement(Direction.LEFT, this.movement);
                break;
            case UP:
                if (this.timeline.getStatus() == Animation.Status.RUNNING)
                    this.movement = this.pacman.setMovement(Direction.UP, this.movement);
                break;
            case DOWN:
                if (this.timeline.getStatus() == Animation.Status.RUNNING)
                    this.movement = this.pacman.setMovement(Direction.DOWN, this.movement);
                break;
            case P:
                if(this.gameHasEnded) {
                    break;
                }
                if (this.isPaused) {
                    this.timeline.play();
                    this.gamePane.getChildren().remove(this.labelPause);
                } else {
                    this.timeline.pause();
                    this.gamePane.getChildren().add(this.labelPause);
                }
                this.isPaused = !this.isPaused;
                break;
            default:
                break;
        }
        e.consume();
    }
    /**
     * This method returns the maze, a 2D array of type smartSquare and is used in the switch statement above of
     * map generation for the pacman.
     */
    public smartSquare[][] getMaze() {
        return this.maze;
    }
    /**
     * This method increments a counter by a constant duration and checks if the counter exceeds 5/the ghost pen is not
     * empty. If these conditions are met, it releases a ghost from the pen, updates its location in the game's maze,
     * and resets the counter to zero.
     */
    private void initPen() {
        this.counterPen += Constants.DURATION;
        if (this.counterPen >= Constants.COUNTER_PEN_SECONDS && !this.ghostPen.isEmpty()) {
            Ghost ghost = this.ghostPen.remove();
            this.maze[ghost.getGhostRow()][ghost.getGhostCol()].deleteCollidables(ghost);
            ghost.setGhostLoc(Constants.GHOST_INITIAL_X, Constants.GHOST_INITIAL_Y);
            this.maze[Constants.MAZE_RELEASE_ROW][Constants.MAZE_RELEASE_COL].collidables(ghost);
            this.counterPen = 0;
        }
    }
    /**
     * This method adds the ghost in the parameter to the pen by setting the ghost's x and y location. Then, the ghost
     * in the parameter is added to the ghostPen Linkedlist. Next, the ghost is added to the ArrayList of collidables
     * in smartSquare.
     */
    public void addToPen(Ghost ghost) {
        this.ghostPen.add(ghost);
        ghost.setGhostLoc(Constants.SPACING_OFFSET * Constants.SQUARE_SIZE,
                Constants.SPACING_OFFSET * Constants.SQUARE_SIZE);
        this.maze[Constants.MAZE_PEN_LOC][Constants.MAZE_PEN_LOC].collidables(ghost);
    }
    /**
     * This method updates the score and is called in the collide methods of Dot, and Energizer with different
     * int values passed in. Then, the sideBar class updates the score based on the integer passed in. The countDot is
     * incremented by 1 so that I can check if the game is over when the countDot value equals 186.
     */
    public void scoreUpdate(int score) {
        this.countDot += Constants.UNIT_INCREMENT;
        this.sideBar.updateScore(score);
    }
    /**
     * This method does the same as the previous method but only updates for ghost collision with pacman. This is
     * because the countDot integer should not increase when a ghost collides with pacman, only when dots and
     * energizers collide.
     */
    public void ghostScoreUpdate(int score) {
        this.sideBar.updateScore(score);
    }
    /**
     * This method updates the lives be decrementing the total countLives by 1, calling the updateLives method from
     * the sideBar class, and checking if the game is over once the countLives = 0.
     */
    public void livesUpdate() {
        this.countLives -= Constants.UNIT_INCREMENT;
        this.sideBar.updateLives();
        this.checkGameOver();
    }
    /**
     *This method checks if Pacman hits anything while moving. Every time Pacman moves to a new square, the method
     *looks at all the objects in that square and handles any collisions.
     */
    private void checkCollision() {
        smartSquare currentLocation = this.maze[this.pacman.getCenterY()/Constants.SQUARE_SIZE]
                [this.pacman.getCenterX()/Constants.SQUARE_SIZE];
        ArrayList<Collidable> elements = currentLocation.getCollidables();
        if (!elements.isEmpty()) {
            for (int i = 0; i < elements.size(); i++) {
                elements.get(i).collide();
                currentLocation.clear();
            }
        }
    }
    /**
     * This method is responsible for updating the target for the ghost through a switch statement that changes between
     * chase, scatter, and frighten by setting the direction of the ghost given board coordinate values. In chase mode,
     * pacman's location is passed through so the ghosts know to chase it (true is also passed in for isTarget). In
     * scatter, wach ghost moves to one corner of the map and isTarget is set to false. In frighten, the ghosts move
     * randomly and do not follow pacman's movement.
     */
    public void updateGhostTarget(GameMode mode) {
        int r = (int) (this.pacman.getCenterY() / Constants.SQUARE_SIZE);
        int c = (int) (this.pacman.getCenterX() / Constants.SQUARE_SIZE);

        switch (mode) {
            case CHASE:
                this.setGhostDirections(
                        new BoardCoordinate(r, c, true),
                        //Ensure c-3 is not negative
                        new BoardCoordinate(r + Constants.CHASE_ROW_COORD2,
                                Math.max(c - Constants.CHASE_MAX_COORD2, 0), true),
                        //Ensure c+2 is not negative
                        new BoardCoordinate(r, Math.max(c + Constants.CHASE_MAX_COORD3, 0), true),
                        //r-4 should be okay as it can't be negative
                        new BoardCoordinate(r - Constants.CHASE_MAX_COORD4, c, true)
                );
                break;
            case SCATTER:
                this.setGhostDirections(
                        new BoardCoordinate(Constants.SCATTER_COORDA, Constants.SCATTER_COORDB, false),
                        new BoardCoordinate(Constants.SCATTER_COORDA, Constants.SCATTER_COORDA, false),
                        new BoardCoordinate(Constants.SCATTER_COORDB, Constants.SCATTER_COORDB, false),
                        new BoardCoordinate(Constants.SCATTER_COORDB, Constants.SCATTER_COORDA, false)
                );
                break;
            case FRIGHTEN:
                for (int i = 0; i < this.ghosts.length; i++) {
                    this.ghosts[i].setDirection(null);
                }
        }
    }
    /**
     * This method assigns directions to ghosts based on provided coordinates passed through the parameter. It checks
     * each coordinate to ensure it falls within the specified bounds of the map. For each valid coordinate, the
     * corresponding ghost's direction is updated to the new coordinate through the setDirection method in the Ghost
     * class.
     */
    private void setGhostDirections(BoardCoordinate... coordinates) {
        int ROW_MAX = Constants.ROW_MAX;
        int COL_MAX = Constants.COL_MAX;
        for (int i = 0; i < Math.min(this.ghosts.length, coordinates.length); i++) {
            if (coordinates[i].getRow() >= 0 && coordinates[i].getRow() <= ROW_MAX &&
                    coordinates[i].getColumn() >= 0 && coordinates[i].getColumn() <= COL_MAX) {
                this.ghosts[i].setDirection(coordinates[i]);
            }
        }
    }
    /**
     * This method is responsible for changing the modes of the ghost's movement: chase, scatter, and frighten. The
     * method first checks to see if the game is not in fright mode, then loops through another if else if else
     * statement. A counter is implemented to set the different modes in a given amount of time. For example,
     * the chase mode is implemented for 20 seconds, the scatter mode is implemented for 7 seconds, and the fright
     * mode is implemented for 7 seconds. In the outer else statement, the fright mode is set to false once 7 seconds
     * have passed and countFright returns back to zero so that this process can be repeated throughout the game.
     */
    private void updateGameMode() {
        //this.updateGhostTarget(GameMode.FRIGHTEN);
        if (!this.frightMode) {
            this.counter += Constants.DURATION;
            if (this.counter >= Constants.CHASE_MODE_MIN && this.counter < Constants.CHASE_MODE_MAX) {
                this.updateGhostTarget(GameMode.CHASE);
            } else if (this.counter >= Constants.SCATTER_MODE_MIN && this.counter < Constants.SCATTER_MODE_MAX) {
                this.updateGhostTarget(GameMode.SCATTER);
            } else {
                this.counter = 0;
            }
        } else {
            this.countFright += Constants.DURATION;
            this.updateGhostTarget(GameMode.FRIGHTEN);
            if (this.countFright >= Constants.FRIGHT_MODE_MAX) {
                this.setFrightMode(false);
                this.countFright = 0;
            }
        }
    }
    /**
     * This method is responsible for determining if the ghosts are in fright mode or not through a boolean value
     * frightMode. Then, in the constructor, frightMode is set to false. This method is called in the updateGameMode
     * method, where when the countFright timer equals/exceeds 7, the fright mode is set to false through the parameter
     * isFright.
     */
    public void setFrightMode(Boolean isFright) {
        this.frightMode = isFright;
    }
    /**
     * This method returns the boolean value of frightMode and is called in the Ghost class collide method as well
     * as the set direction method.
     */
    public Boolean getFrightMode() {
        return this.frightMode;
    }
}
