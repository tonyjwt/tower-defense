package WizardTD;

/**
 * A class representing a button.
 */
public class Button {
        
    public float X;
    public float Y;
    public static final float WIDTH = 40;
    public static final float HEIGHT = 40;
    public static final float LABEL_X_OFFSET = 5;
    public static final float LABEL_Y_OFFSET = 30;
    public static final float DESC_X_OFFSET = 5 + WIDTH;
    public static final float DESC_Y_OFFSET = 16;

    public boolean hovered = false;
    public boolean shouldHighlight = false;

    public ButtonController.ButtonAction action;

    private String label;
    public String desc;

    private App app;
    
    /**
     * Initialise a Button object with the specified attributes.
     *
     * @param x The x-coordinate of the button's position.
     * @param y The y-coordinate of the button's position.
     * @param action The action associated with the button.
     * @param label The label text for the button.
     * @param desc The description text for the button.
     * @param app Instance of App
     */
    public Button(float x, float y, ButtonController.ButtonAction action, String label, String desc, App app) {
        this.X = x;
        this.Y = y;
        this.action = action;
        this.label = label;
        this.desc = desc;
        this.app = app;
    }

    /**
     * Draws a popup window for the button.
     */
    public void drawPopup() {
        app.strokeWeight(1);
        app.stroke(0,0,0);
        app.fill(255,255,255);
        app.rect(X-82, Y, 67, 22);
        
        app.textSize(13);
        app.fill(0,0,0);
    }

    /**
     * Draws the button on the screen, considering its state (i.e. whether hovering over it; whether it is selected) and the associated action.
     */
    public void draw() {
        if (action == ButtonController.ButtonAction.Pause) {
            if (app.paused) {
                shouldHighlight = true;
            } else {
                shouldHighlight = false;
            }
        } else if (action == ButtonController.ButtonAction.FastForward) {
            if (app.fastForward) {
                shouldHighlight = true;
            } else {
                shouldHighlight = false;
            }
        } else if (action == ButtonController.ButtonAction.Range) {
            if (app.rangeUpgradeSelected) {
                shouldHighlight = true;
            } else {
                shouldHighlight = false;
            }
        } else if (action == ButtonController.ButtonAction.Speed) {
            if (app.speedUpgradeSelected) {
                shouldHighlight = true;
            } else {
                shouldHighlight = false;
            }
        } else if (action == ButtonController.ButtonAction.Damage) {
            if (app.damageUpgradeSelected) {
                shouldHighlight = true;
            } else {
                shouldHighlight = false;
            }
        } else if (action == ButtonController.ButtonAction.Tower) {
            if (app.selectedAction == App.Action.Tower) {
                shouldHighlight = true;
            } else {
                shouldHighlight = false;
            }
        } else if (action == ButtonController.ButtonAction.Bomb) {
            if (app.selectedAction == App.Action.Bomb) {
                shouldHighlight = true;
            } else {
                shouldHighlight = false;
            }
        } 

        if ((app.mouseX >= this.X) && (app.mouseX <= this.X + WIDTH) && (app.mouseY >= this.Y) && (app.mouseY <= this.Y + HEIGHT)) {
            this.hovered = true;
            
            switch (action) {
                case Tower:
                    drawPopup();
                    app.text("Cost: " + Integer.toString((int) Math.round(app.getBuildTowerCost())), X-78, Y+17);
                    break;
                case ManaPool:
                    drawPopup();
                    app.text("Cost: " + Integer.toString((int) Math.round(app.getManaPoolCost())), X-78, Y+17);
                    break;
            }
        } else {
            this.hovered = false;
        }

        app.strokeWeight(3);
        app.stroke(0, 0, 0);
       
        if (this.shouldHighlight) {
            app.fill(255,255,0);
        } else if (this.hovered) {
            app.fill(190,190,190);
        } else {
            app.noFill();
        }
        
        app.rect(X, Y, WIDTH, HEIGHT);
        app.fill(0,0,0);
        app.textSize(23);
        app.text(label, X + LABEL_X_OFFSET, Y + LABEL_Y_OFFSET);
        app.textSize(11);

        String text = desc;
        if (action == ButtonController.ButtonAction.ManaPool) {
            text = desc + Integer.toString((int) app.getManaPoolCost());
        }
        app.text(text, X + DESC_X_OFFSET, Y + DESC_Y_OFFSET);

    }
}
