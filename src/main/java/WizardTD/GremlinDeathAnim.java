package WizardTD;

import java.rmi.server.ExportException;

import processing.core.PImage;

/**
 * Class displaying the animation of a gremlin's death.
 */
public class GremlinDeathAnim {

    public float drawX;
    public float drawY;

    public int animSpriteIndex = 0;
    public int frameCount = 0;

    public PImage sprite;

    private App app;

    /**
     * Initializes a GremlinDeathAnim object with the specified attributes.
     *
     * @param x   The X-coordinate of the animation's position.
     * @param y   The Y-coordinate of the animation's position.
     * @param app App instance
     */
    public GremlinDeathAnim(float x, float y, App app) { 
        this.app = app;

        this.drawX = x + app.monsterController.GREMLIN_X_OFFSET;
        this.drawY = y + app.monsterController.GREMLIN_Y_OFFSET;

        try {
            this.sprite = app.gremlinDeathSprites.get(animSpriteIndex);
        } catch (RuntimeException e) {
            // if testing, will get exception, can ignore.
        }
        
    }

    /**
     * Advances the animation frame and updates the sprite.
     */
    public void tick() {
        frameCount++;
        if (frameCount >= 5) {
            frameCount = 1;
            animSpriteIndex++;
            this.sprite = app.gremlinDeathSprites.get(animSpriteIndex);
        }
    }

    /**
     * Draws the gremlin death animation by displaying its sprite (for current frame) at the current position.
     */
    public void draw() {
        app.image(this.sprite, drawX, drawY);
    }
}