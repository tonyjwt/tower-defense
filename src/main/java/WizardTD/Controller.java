package WizardTD;

/**
 * The abstract Controller class provides a basic structure for other specific controllers.
 * Subclasses implement the draw() and tick() methods to control rendering and game logic.
 */
public abstract class Controller {
    
    protected App app; // A reference to the instance of App

    /**
     * Implement this method to define the logic for rendering Sprites.
     */
    public abstract void draw();

    /**
     * Implement this method to define the game's logic (e.g. movement, updating state)
     */
    public abstract void tick();

}