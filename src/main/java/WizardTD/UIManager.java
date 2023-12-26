package WizardTD;

/**
 * The UIManager class is responsible for drawing the user interface in the game window.
 */
public class UIManager {
    private App app;

    /**
     * Constructor for the UIManager class.
     *
     * @param app The main application object, which is an instance of the App class.
     */
    public UIManager(App app) {
        this.app = app;
    }

    /**
     * Draws the user interface elements, including the mana bar and wave information.
     */
    public void drawUI() {
        if (app.selectedAction == App.Action.Bomb) {
            app.image(app.bombSprite, app.mouseX + app.BOMB_X_OFFSET, app.mouseY + app.BOMB_Y_OFFSET);
            app.noFill();
            app.strokeWeight(2);
            app.stroke(255,255,0);
            app.ellipse(app.mouseX, app.mouseY, app.BOMB_RADIUS*2, app.BOMB_RADIUS*2);
        }
        
        app.strokeWeight(0);
        app.stroke(128, 108, 65);
        app.fill(127, 109, 64);
        app.rect(0,0, App.WIDTH, App.TOPBAR);
        app.rect(App.WIDTH-App.SIDEBAR,App.TOPBAR, App.SIDEBAR, App.HEIGHT-App.TOPBAR);

        app.strokeWeight(2);
        app.stroke(0,0,0);
        app.fill(255, 255, 255);
        app.rect(381, 9, App.MANA_BAR_LENGTH, 22);

        app.stroke(0,0,0);
        app.fill(2, 215, 225);
        int manaBarFillLength = (int) ((App.MANA_BAR_LENGTH)*(app.mana) / (app.manaCap));
        manaBarFillLength = Math.max(manaBarFillLength, 0);
        app.rect(381, 9, manaBarFillLength, 22);

        app.stroke(0,0,0); // set stroke colour to black
        app.fill(0,0,0); // set fill colour to black
        app.textSize(23);

        try {
            String timeUntilNextWaveStr = Integer.toString(app.waveController.timeUntilNextWave);
            int waveNum = app.waveController.waveNum;
            String nextWaveNumStr = Integer.toString(waveNum + 2);
            if (waveNum < app.waveController.numWaves - 1) { // dont have to worry about waveNum not being defined; because insiide try block
                app.text("Wave " + nextWaveNumStr + " starts: " + timeUntilNextWaveStr, 15, 29);
            }
        } catch (Exception e) {
        }
        
        app.textSize(16);
        app.text("MANA:", 320, 27);

        app.text(((int) app.mana) + " / " + app.manaCap, 507, 27);

        try {
            drawUpgradeCostPopup();
        } catch (NullPointerException e) {
        }

        // Draw all buttons through buttonController
        app.buttonController.draw();
    }

    /**
     *  Handles drawing of upgrade cost popup in the bottom right corner.
     */
    public void drawUpgradeCostPopup() {
        // hovering over tower and at least one upgrade selected
        if ((app.towerController.currentHoverTowerIndex != -1) && (app.rangeUpgradeSelected || app.speedUpgradeSelected || app.damageUpgradeSelected)) {
            
            int numUpgradesSelected = 0;
            if (app.rangeUpgradeSelected) {
                numUpgradesSelected++;
            }
            if (app.speedUpgradeSelected) {
                numUpgradesSelected++;
            }
            if (app.damageUpgradeSelected) {
                numUpgradesSelected++;
            }
            app.strokeWeight(1);
            app.stroke(0,0,0);
            app.fill(255,255,255);

            app.rect(App.CELLSIZE*App.BOARD_WIDTH + App.BUTTON_LEFT_MARGIN, 570, 90, 20);
            app.rect(App.CELLSIZE*App.BOARD_WIDTH + App.BUTTON_LEFT_MARGIN, 590, 90, numUpgradesSelected*20);
            app.rect(App.CELLSIZE*App.BOARD_WIDTH + App.BUTTON_LEFT_MARGIN, (590 + numUpgradesSelected*20), 90, 20);

            Tower towerToUpgrade = app.towerController.towers.get(app.towerController.currentHoverTowerIndex);
            int totalCost = 0;
            int rangeCost = (int) app.TOWER_UPGRADE_COST + 10*towerToUpgrade.rangeLevel;
            int speedCost = (int) app.TOWER_UPGRADE_COST + 10*towerToUpgrade.speedLevel;
            int damageCost = (int) app.TOWER_UPGRADE_COST + 10*towerToUpgrade.damageLevel;

            app.fill(0,0,0);
            app.textSize(12);
            app.text("Upgrade cost", App.CELLSIZE*App.BOARD_WIDTH + App.BUTTON_LEFT_MARGIN + 6, 585);

            int costsCount = 0;
            if (app.rangeUpgradeSelected) {
                app.text("Range: " + Integer.toString(rangeCost), App.CELLSIZE*App.BOARD_WIDTH + App.BUTTON_LEFT_MARGIN + 6, (605 + 20*costsCount));
                totalCost += rangeCost;
                costsCount++;
            }
            if (app.speedUpgradeSelected) {
                app.text("Speed: " + Integer.toString(speedCost), App.CELLSIZE*App.BOARD_WIDTH + App.BUTTON_LEFT_MARGIN + 6, (605 + 20*costsCount));
                totalCost += speedCost;
                costsCount++;
            }
            if (app.damageUpgradeSelected) {
                app.text("Damage: " + Integer.toString(damageCost), App.CELLSIZE*App.BOARD_WIDTH + App.BUTTON_LEFT_MARGIN + 6, (605 + 20*costsCount));
                totalCost += damageCost;
                costsCount++;
            }

            app.text("Total: " + Integer.toString(totalCost), App.CELLSIZE*App.BOARD_WIDTH + App.BUTTON_LEFT_MARGIN + 6, (605 + numUpgradesSelected*20));
            
            app.textSize(13);
            app.fill(0,0,0);
        }
    }
}