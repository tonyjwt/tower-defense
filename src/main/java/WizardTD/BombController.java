package WizardTD;

import java.util.LinkedList;

/**
 * BombController class. Responsible for managing instances of the Bomb class.
 */
public class BombController {
    
    public LinkedList<Bomb> bombs = new LinkedList<>();

    private Bomb tempBomb;

    /*
     * Constructor for BombController class
     */
    public BombController() {

    }

    /**
     * Add a Bomb object to a List of instances of the Bomb class.
     *
     * @param bomb The Bomb instance to be added to the collection.
     */
    public void addBomb(Bomb bomb) {
        bombs.add(bomb);
    }
}