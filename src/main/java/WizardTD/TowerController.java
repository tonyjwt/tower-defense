package WizardTD;

import processing.data.JSONObject;
import processing.data.JSONArray;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;


import java.util.LinkedList;

/**
 * Class responsible for managing and controlling towers in the game.  Extends the Controller class.
 */
public class TowerController extends Controller {
    public int initTowerRange;
    public float initTowerFiringSpeed;
    public int initTowerDamage;
    public int currentHoverTowerIndex;
    public LinkedList<Tower> towers;

    private JSONObject configJson;

    private Tower tempTower;
    

    /**
     * Initializes the TowerController with the provided application context.
     *
     * @param app Instance of App
     */
    public TowerController(App app) {

        this.app = app;

        this.towers = new LinkedList<>();

        this.configJson = app.configJson;
        try {
            this.initTowerDamage = configJson.getInt("initial_tower_damage");
            this.initTowerFiringSpeed = configJson.getFloat("initial_tower_firing_speed");
            this.initTowerRange = configJson.getInt("initial_tower_range");
        } catch (Exception e) {
            // if testing, will get RuntimeException, can ignore. 
        }
        
    }

    /**
     * Draws all towers and their attack ranges (if hovered)
     */
    @Override
    public void draw() {
        for (int i = 0; i < towers.size(); i++) {
            try {
                tempTower = towers.get(i);
                if (i == currentHoverTowerIndex) {
                    tempTower.drawRange = true;
                } else {
                    tempTower.drawRange = false;
                }
                tempTower.draw();
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }
    }

    /**
     * Updates the state of all towers by calling update() on each tower
     */
    @Override
    public void tick() {
        for (int i = 0; i < towers.size(); i++) {
            try {
                tempTower = towers.get(i);
                tempTower.tick();
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }
    }

    /**
     * Adds a tower to the list of managed towers.
     *
     * @param tower The Tower instance to be added.
     */
    public void addTower(Tower tower) {
        towers.add(tower);
    }
}