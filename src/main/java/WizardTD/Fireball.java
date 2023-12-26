package WizardTD;

import processing.core.PImage;

/*
* Fireball class; defines behaviour of projectiles fired by towers. Extends the Moveable class.
*/
public class Fireball extends Moveable {
    
    private final float FIREBALL_DIAMETER = 6; // in pixels
    private float FIREBALL_SPEED = 5; // pixels per frame

    private float x; // for drawing (top left corner of sprite)
    private float y; // for drawing (top left corner of sprite)

    public float centreX; //centre of fireball
    public float centreY; //centre of fireball

    public float xDiff;
    public float yDiff;

    private double damage;

    private PImage sprite;

    private Monster target;

    /**
     * Initializes a Fireball object with the specified attributes.
     *
     * @param centreX The X-coordinate of the center of the fireball.
     * @param centreY The Y-coordinate of the center of the fireball.
     * @param target The target Monster that the fireball is aimed at.
     * @param damage The raw damage inflicted by the fireball not accounting for armour.
     * @param app App instance
     */
    public Fireball(float centreX, float centreY, Monster target, double damage, App app) {
        this.target = target;

        this.centreX = centreX;
        this.centreY = centreY;

        this.x = centreX - (FIREBALL_DIAMETER/2);
        this.y = centreY - (FIREBALL_DIAMETER/2);

        this.damage = damage;

        try {
            this.sprite = app.loadImage("src/main/resources/WizardTD/fireball.png");
        } catch (Exception e) {
            // will get exception when testing, since cannot load image. can safely ignore in this case.
        }
        

        this.app = app;
    }

    /**
     * Handles logic; update the state of the Fireball during each game tick.
     *
     * Calculates the movement of the fireball towards its target and
     * reduces the target's health if the fireball reaches the target.
     *
     * @return true if the fireball hit the target and inflicted damage, false otherwise.
     */
    @Override
    public boolean tick() {
        xDiff = target.centreX - this.centreX;
        yDiff = target.centreY - this.centreY;
        double distToTarget = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));

        if (distToTarget < FIREBALL_SPEED / 2.0) {
            target.takeDamage(damage);
            return true;
        }

        double dx = xDiff / distToTarget * FIREBALL_SPEED; // amount to move by in x dir
        double dy = yDiff / distToTarget * FIREBALL_SPEED; // amount to move by in y dir

        this.centreX += dx;
        this.centreY += dy;

        this.x = centreX - (FIREBALL_DIAMETER/2);
        this.y = centreY - (FIREBALL_DIAMETER/2);

        return false;
    }

    /**
     * Draws the Fireball by displaying its sprite at the current position.
     */
    @Override
    public void draw() {
        try {
            app.image(this.sprite, x, y);
        } catch (Exception e) {
            // if testing, will get exception, can safely ignore.
        }
        
    }
}