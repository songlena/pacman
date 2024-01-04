package pacman;
 /**
 * Enum that represents the directions of movement: LEFT, RIGHT, UP, AND DOWN. This is used for ghost and pacman
  * movement.
 */
public enum Direction {
    LEFT, RIGHT, UP, DOWN;
    /**
     * Method that gets the opposite direction in a switch statement. It is called in the Ghost class to determine the
     * valid direction of Ghost movement.
     */
    public Direction getOpposite() {
        switch (this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            default:
                return LEFT;
        }
    }
}
