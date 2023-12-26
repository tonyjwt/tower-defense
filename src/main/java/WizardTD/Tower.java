package WizardTD;

import processing.core.PImage;
// import processing.core.PApplet;

import java.util.LinkedList;

/**
 * Class representing a tower in the game.
 */
public class Tower {

    private float CELLSIZE; // get from App
    public float x; // for drawing (corresponds to top left of tile, since dimensions of tower sprite = dimensions of grass tile sprite)
    public float y; // for drawing (corresponds to top left of tile, since dimensions of tower sprite = dimensions of grass tile sprite)
    public float centreX; // for calculating distance (corresponds to centre of tower)
    public float centreY; // for calculating distance (corresponds to centre of tower)

    public boolean drawRange = false;
    
    private PImage sprite;

    private App app;

    public int rangeLevel;
    public int speedLevel;
    public int damageLevel;

    private int rangeIndicatorIndex;
    private int speedIndicatorIndex;
    private int damageIndicatorIndex;
    
    public double damage;
    public float range;
    public int fireballFrameInterval;
    public int frameCount;

    public LinkedList<Fireball> fireballs; // fireballs created by THIS tower
    private Fireball tempFireball;

    private LinkedList<Monster> monsters;

    /**
     * Updates the tower's statistics (damage, range, and firing speed) based on its upgrade levels.
     */
    public void updateStats() { // call whenever tower is upgraded
        try {
            this.damage = app.towerController.initTowerDamage * (1 + (0.5*damageLevel));
            this.range = app.towerController.initTowerRange + (CELLSIZE * rangeLevel);
            this.fireballFrameInterval = (int) ((1/(app.towerController.initTowerFiringSpeed + (0.5 * speedLevel)))*app.FPS);

        } catch (NullPointerException e) {
            // if testing, will get NullPointerException, can safely ignore & use arbitrary values:
            this.damage = (double) 40;
            this.range = (float) 96;
            this.fireballFrameInterval = 20;
        }

        
        if ((rangeLevel >= 2) && (speedLevel >= 2) && (damageLevel >= 2)) {
            sprite = app.tower2Sprite;
            rangeIndicatorIndex = rangeLevel - 2;
            speedIndicatorIndex = speedLevel - 2;
            damageIndicatorIndex = damageLevel - 2;
        } else if ((rangeLevel >= 1) && (speedLevel >= 1) && (damageLevel >= 1)) {
            sprite = app.tower1Sprite;
            rangeIndicatorIndex = rangeLevel - 1;
            speedIndicatorIndex = speedLevel - 1;
            damageIndicatorIndex = damageLevel - 1;
        } else {
            sprite = app.tower0Sprite;
            rangeIndicatorIndex = rangeLevel;
            speedIndicatorIndex = speedLevel;
            damageIndicatorIndex = damageLevel;
        }
    }

    /**
     * Initializes a Tower object with the specified attributes.
     *
     * @param x The x-coordinate of the tower.
     * @param y The y-coordinate of the tower.
     * @param rangeLevel The level of the tower's range upgrade.
     * @param speedLevel The level of the tower's speed upgrade.
     * @param damageLevel The level of the tower's damage upgrade.
     * @param app Instance of App
     */
    public Tower(float x, float y, int rangeLevel, int speedLevel, int damageLevel, App app) {
        this.fireballs = new LinkedList<>();
        this.CELLSIZE = (float) app.CELLSIZE;
        this.x = x;
        this.y = y;
        this.centreX = x + (this.CELLSIZE/2);
        this.centreY = y + (this.CELLSIZE/2);
        this.rangeLevel = rangeLevel;
        this.speedLevel = speedLevel;
        this.damageLevel = damageLevel;
        
        this.app = app;

        updateStats();

        this.frameCount = fireballFrameInterval;
        
        this.monsters = app.monsterController.monsters;
    }

    /**
     * Draws the tower's sprite, indicators for range, speed, and damage upgrades, and its attack range (if hovered).
     */
    public void draw() {

        app.image(sprite, x, y);
        app.fill(255,0,255);
        app.textSize(14);

        for (int i = 0; i < rangeIndicatorIndex; i++) {
            app.text("o", x + 9*i, y+6);
        }
        for (int i = 0; i < damageIndicatorIndex; i++) {
            app.text("x", x + 9*i, y+30);
        }
        if (speedIndicatorIndex > 0) {
            app.stroke(170,215,255);
            app.strokeWeight(speedIndicatorIndex+1);
            app.noFill();
            app.rect(x+5,y+5,21,21);
        }
        
        if (drawRange) {
            app.noFill();
            app.strokeWeight(2);
            app.stroke(255,255,0);
            app.ellipse(centreX, centreY, range*2, range*2);
        }

        
    }

     /**
     * Update the tower's state, including firing fireballs at nearby monsters.
     */
    public void tick() {
        
        for (int i = 0; i < fireballs.size(); i++) {
            try {
                tempFireball = fireballs.get(i);
                
                if (tempFireball.tick()) { // if true, then fireball hit gremlin 
                    fireballs.remove(i);
                    continue;
                }
                
                tempFireball.draw();
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }

        if (this.frameCount < this.fireballFrameInterval) {
            frameCount++;
        } else {
            for (Monster monster : monsters) {
                if (app.distance(monster.centreX, monster.centreY, this.centreX, this.centreY) <= range) { // gremlin is in range
                    
                    fireballs.add(new Fireball(centreX, centreY, monster, damage, app));
                    frameCount = 0;
                    break;
                }
            }
        }   
    }
}