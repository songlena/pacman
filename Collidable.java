package pacman;

public interface Collidable {
    /**
     * interface with the method collide, is implemented in Ghost class, Dot class, and Energizer class (override)
     */
    default void collide(){
    }
}
