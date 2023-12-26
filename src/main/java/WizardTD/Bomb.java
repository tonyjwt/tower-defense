package WizardTD;

/**
 * Bomb class.
 */
public class Bomb {
    public float centreX;
    public float centreY;

    /**
     * Initialise a Bomb object with specified attributes.
     *
     * @param x The X-coordinate of the Bomb's position.
     * @param y The Y-coordinate of the Bomb's position.
     * @param damageRadius The radius of the damage area caused by the bomb explosion.
     * @param bombDamage The amount of damage the bomb causes to monsters within its damage radius.
     * @param app Instance of App
     */
    public Bomb(float x, float y, float damageRadius, float bombDamage, App app) { 
        // intialise variables
        this.centreX = x;
        this.centreY = y;

        for (Monster monster : app.monsterController.monsters) {
            if (app.distance(monster.centreX, monster.centreY, this.centreX, this.centreY) <= damageRadius) {
                // in range, so deal damage.
                monster.currentHP -= monster.armour * bombDamage;
            }
        }
    }
}