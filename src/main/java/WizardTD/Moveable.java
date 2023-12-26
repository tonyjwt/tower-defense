package WizardTD;

/**
 * This abstract class provides the basic structure for classes that can move, eg monsters and fireballs.
 * Subclasses implement the draw() and tick() methods to control rendering and game logic.
 */
public abstract class Moveable {
    protected App app; // A reference to the instance of App

    /**
     * Implement this method to define the logic for rendering Sprites.
     */
    public abstract void draw();

    /**
     * Implement this method to define logic (e.g. movement, updating state)
     * @return : for Monster, returns true if moved in x direction, false otherwise. For fireball, returns true if hit monster, false otherwise.
     */
    public abstract boolean tick();

    /**
     * Determines the sign of a number. Able to be used by all classes that extend Moveable.
     *
     * @param n The number to determine the sign of.
     * @return 1 if the number is positive, -1 if it's negative, and 0 if it's zero.
     */
    protected int sign(double n) { // define using double not float as parameter to avoid lossy conversion errors
        return (int) (n / Math.abs(n));
    }
}