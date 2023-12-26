    package WizardTD;

    import processing.core.PImage;

    import java.util.ArrayList;

    /**
    * Class representing a monster in the game. Extends the Moveable class.
    */
    public class Monster extends Moveable implements Damageable {
        private final float HEALTH_BAR_LENGTH = (float) 29.0;
        private final float HEALTH_BAR_WIDTH = (float) 2.6;

        private final float HEALTH_BAR_X_OFFSET = (float) -4.5;
        private final float HEALTH_BAR_Y_OFFSET = (float) -4.5;

        public String type;

        public final float spriteDiameter = 20; // in pixels
        
        public float x;
        public float y;

        public float centreX;
        public float centreY;
        
        public int SPAWN_POINT_INDEX;

        public int pathPointIndex = 0;
        
        private float maxHP;
        public float currentHP;

        private float NORMAL_SPEED;
        public float armour;

        public float manaGainedOnKill;

        private PImage sprite;

        private App app; // if declare type as PApplet App, then wontwork bc PApplet doesn't have App's variables

        //system vars
        private boolean lastMoveWasInX;
        private float lastMoveAmount; // number of pixels, can be + or - depending on direction

        /**
         * Initializes a Monster object with the specified attributes.
         *
         * @param SPAWN_POINT_INDEX The index of the spawn point for the monster.
         * @param app App instance.
         * @param type The type of the monster.
         * @param maxHP The maximum health points of the monster.
         * @param NORMAL_SPEED The normal movement speed of the monster.
         * @param armour The armour value (damage multiplier) of the monster.
         * @param manaGainedOnKill The raw amount of mana gained when the monster is killed.
         */
        public Monster(int SPAWN_POINT_INDEX, App app, String type, float maxHP, float NORMAL_SPEED, float armour, float manaGainedOnKill) { // initMazeX and initMazeY are indexes in the 'maze' 2D array 
            this.SPAWN_POINT_INDEX = SPAWN_POINT_INDEX;
            this.app = app;
            this.type = type;

            try {
                this.x = app.spawnPoints.get(SPAWN_POINT_INDEX)[0];
                this.y = app.spawnPoints.get(SPAWN_POINT_INDEX)[1];
            } catch (Exception e) {
                // if testing, spawnPoints will be empty List
                // set arbitrary coords
                this.x = 0;
                this.y = 0;
                this.centreX = x + spriteDiameter/2 + 6;
                this.centreY = y + spriteDiameter/2 + 6;

            }
            
            
            sprite = app.gremlinSprite;
            if (type.equals("worm")) {
                sprite = app.wormSprite;
            } else if (type.equals("beetle")) {
                sprite = app.beetleSprite;
                sprite.resize(0,20);
            }
            
            this.maxHP = maxHP;
            this.currentHP = maxHP;
            this.NORMAL_SPEED = NORMAL_SPEED;
            this.armour = armour;
            this.manaGainedOnKill = manaGainedOnKill;
        }

        /**
         * Checks whether the monster instance is currently alive (i.e. current health greater than 0)
         *
         * @return true if the monster is alive, false otherwise.
         */
        public boolean isAlive() {
            return (this.currentHP > 0);
        }

        /**
         * Applies damage to the monster instance and takes into account armour multiplier.
         *
         * @param rawDamage The amount of damage to be applied to the object, without considering armour yet.
         */
        public void takeDamage(double rawDamage) {
            this.currentHP -= (this.armour * rawDamage);
        }

        /**
         * Makes the monster navigate to next point in path
         *
         * @param pathPoints The list of path points the monster follows.
         */
        private void nextPoint(ArrayList<int[]> pathPoints) {
            if (this.pathPointIndex < (pathPoints.size() - 1)) {
                    this.pathPointIndex += 1;
                } else { // reached wizard house
                    app.loseMana(this.currentHP);
                    this.x = app.spawnPoints.get(SPAWN_POINT_INDEX)[0];
                    this.y = app.spawnPoints.get(SPAWN_POINT_INDEX)[1];
                    this.pathPointIndex = 0;
            }
        }

        /**
         * Handles logic (update the state of the monster during each game tick)
         *
         * @return true if the monster moved in the x-direction (ie horizontally); false otherwise.
         */
        @Override
        public boolean tick() {
            
            float speed = NORMAL_SPEED;
            if (app.fastForward) {
                speed *= 2;
            }

            centreX = x + (spriteDiameter/2) + app.monsterController.GREMLIN_X_OFFSET;
            centreY = y + (spriteDiameter/2) + app.monsterController.GREMLIN_Y_OFFSET;
            
            ArrayList<int[]> pathPoints = app.allPathsPoints.get(SPAWN_POINT_INDEX);
            
            float targetX = pathPoints.get(pathPointIndex)[0];
            float targetY = pathPoints.get(pathPointIndex)[1];

            if (x != targetX) {
                
                float xDiff = targetX - x; // distance between current x and target x
                float dx = sign(targetX - x) * speed; // number of pixels to move by
                if (Math.abs(dx) > Math.abs(xDiff)) {
                    dx = xDiff;
                }

                x += dx;
                lastMoveWasInX = true;
                lastMoveAmount = dx;

            } else if (y != targetY) {
                float yDiff = targetY - y;
                float dy = sign(targetY - y) * speed; // number of pixels to move by
                if (Math.abs(dy) > Math.abs(yDiff)) {
                    dy = yDiff;
                }

                y += dy;
                
                lastMoveWasInX = false;
                lastMoveAmount = dy;
            } else {
                nextPoint(pathPoints);
            }
            this.currentHP = Math.max(0, this.currentHP);

            return lastMoveWasInX;
        }

        /**
         * Draws the monster's sprite and health bar on the screen.
         */
        @Override
        public void draw() {

            float drawX = x + app.monsterController.GREMLIN_X_OFFSET; //consistent With Tiles' Top Left Corners
            float drawY = y + app.monsterController.GREMLIN_Y_OFFSET; //consistent With Tiles' Top Left Corners

            app.image(sprite, drawX, drawY);

            app.strokeWeight(0);
            app.fill(255, 0, 0);
            app.rect(drawX + HEALTH_BAR_X_OFFSET, drawY + HEALTH_BAR_Y_OFFSET, this.HEALTH_BAR_LENGTH, this.HEALTH_BAR_WIDTH);
            app.fill(0, 255, 0);
            app.rect(drawX + HEALTH_BAR_X_OFFSET, drawY + HEALTH_BAR_Y_OFFSET, (this.HEALTH_BAR_LENGTH * this.currentHP/this.maxHP), this.HEALTH_BAR_WIDTH);
        }
        
    }