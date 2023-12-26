package WizardTD;

/**
 * The Damageable interface outlines the requirements for objects capable of taking damage,
 * by defining the methods takeDamage and isAlive which classes implementing this interface
 * must implement.
 */
public interface Damageable {   
    
    /**
     * Applies damage to the object.
     *
     * @param rawDamage The amount of damage to be applied to the object, without considering armour yet.
     */
    public void takeDamage(double rawDamage);

    /**
     * Checks whether the object is currently alive (i.e. current health greater than 0)
     *
     * @return true if the object is alive, false otherwise.
     */
    public boolean isAlive();
}