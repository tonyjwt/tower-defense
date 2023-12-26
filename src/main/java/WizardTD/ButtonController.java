package WizardTD;

import java.util.LinkedList;

/**
 * ButtonController class. Responsible for managing all the buttons displayed on screen. Extends the Controller class.
 */
public class ButtonController extends Controller {
    
    /**
     * An enumeration representing various actions associated with buttons.
     * Define behavior of the buttons.
     */
    public static enum ButtonAction {
        FastForward, Pause, Tower, Range, Speed, Damage, ManaPool, Bomb;
    }

    public LinkedList<Button> buttons = new LinkedList<>();

    public static final String[] BUTTON_LABELS = new String[]{"FF", "P", "T", "U1", "U2", "U3", "M", "B"};
    public static String[] BUTTON_DESCS = new String[]{"2x speed", "Pause", "Build\ntower", "Upgrade\nrange", "Upgrade\nspeed", "Upgrade\ndamage", "Mana pool\ncost: ", "Bomb\ncost: " + Integer.toString((int) App.BOMB_COST)};
    
    /*
     * Constructor
     */
    public ButtonController(App app) {
        this.app = app;
    }

    /*
     * Calls draw() for every button
     */
    @Override
    public void draw() {
        for (Button button : buttons) {
            button.draw();
        }
    }

    @Override
    public void tick() {

    }

    /**
     * Adds a new button to the collection of buttons.
     *
     * @param x The X-coordinate of the button's position.
     * @param y The Y-coordinate of the button's position.
     * @param action The action associated with the button.
     * @param label The label text for the button.
     * @param desc The description text for the button.
     */
    public void addButton(float x, float y, ButtonAction action, String label, String desc) {
        buttons.add(new Button(x, y, action, label, desc, app));
    }
}