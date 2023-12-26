package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import com.jogamp.newt.event.KeyEvent;

import WizardTD.ButtonController.ButtonAction;

/**
* App class. The bulk of the program is executed from this class.
*/
public class App extends PApplet { // extend PApplet means that App class inherits from PApplet. PApplet allows for window pop up, frame rates, etc

    // UI constants
    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;
    public static final int MANA_BAR_LENGTH = 340;
    public static final int BUTTON_LEFT_MARGIN = 10;
    public static final int BUTTON_TOP_MARGIN = 10;
    public static int WIDTH = CELLSIZE*BOARD_WIDTH + SIDEBAR;
    public static int HEIGHT = CELLSIZE*BOARD_WIDTH + TOPBAR;

    // DISPLAY CONSTANTS
    private final int WIZARD_X_OFFSET = -8; // OFFSET of -8 to centre the wizard house in its tile
    private final int WIZARD_Y_OFFSET = -8; // OFFSET of -8 to centre the wizard house in its tile
    public final int BOMB_X_OFFSET = -16;
    public final int BOMB_Y_OFFSET = -28;

    // PImage variables
    public final int NUMBER_OF_GREMLIN_DEATH_FRAMES = 5;
    private PImage grassSprite;
    public PImage gremlinSprite;
    public PImage bombSprite;
    public ArrayList<PImage> gremlinDeathSprites = new ArrayList<>();
    private PImage path0_1Sprite;
    private PImage path0_2Sprite;
    private PImage path1_1Sprite;
    private PImage path1_2Sprite;
    private PImage path1_3Sprite;
    private PImage path1_4Sprite;
    private PImage path2_1Sprite;
    private PImage path2_2Sprite;
    private PImage path2_3Sprite;
    private PImage path2_4Sprite;
    private PImage path3Sprite;
    private PImage shrubSprite;
    public PImage tower0Sprite;
    public PImage tower1Sprite;
    public PImage tower2Sprite;
    private PImage wizard_houseSprite;
    public PImage wormSprite;
    public PImage beetleSprite;

    // System
    public static final int FPS = 60;
    public String configPath;
    public JSONObject configJson;
    public JSONArray wavesArray;
    private String layoutPath;
    private boolean firstTimeDrawMap = true;
    private int wizardXCoord;
    private int wizardYCoord;
    public enum Action {
        Tower, Bomb;
    }
    public int numManaPoolUses = 0;
    private ArrayList<int[]> searchStartPoints = new ArrayList<>(); // int corresponding to tile number; not actual pixels
    public ArrayList<float[]> spawnPoints = new ArrayList<>(); // actual position in terms of pixels ; takes topbar into account
    public HashMap<Integer, ArrayList<int[]>> allPathsPoints = new HashMap<>();
    public boolean justCalledExtraDraw = false;
    private List<List<Integer>> paths = new ArrayList<List<Integer>>();
    private char[][] map;
    private int[][] maze; // values will be changed by searchPath()
    private int[][] mazeReference; // keep as reference

    // GAME VARIABLES
    public float mana;
    public int manaCap;
    public static float MANA_POOL_INIT_COST;
    public static float BOMB_COST;
    public float TOWER_COST;
    public float TOWER_UPGRADE_COST = 20;
    public float BOMB_RADIUS;
    private float BOMB_DAMAGE;
    private float MANA_POOL_COST_INCREASE_PER_USE;
    private float INIT_MANA_GAINED_PER_SECOND;
    private float INIT_MANA_GAINED_PER_FRAME;
    private float MANA_POOL_MANA_GAINED_MULTIPLIER;
    private float MANA_POOL_MANA_CAP_MULTIPLIER;
    public float manaGainedOnKillMultiplier = 1;
    private float manaTrickleMultiplier = 1;
    public Action selectedAction = null;
    public boolean rangeUpgradeSelected = false;
    public boolean speedUpgradeSelected = false;
    public boolean damageUpgradeSelected = false;
    public boolean paused = false;
    public boolean fastForward = false;
    public boolean gameOver = false;
    public boolean won = false;

    // Class instances
    public ButtonController buttonController;
    public WaveController waveController;
    public MonsterController monsterController;
    public BombController bombController;
    public TowerController towerController;
    public UIManager uiManager;

    /**
     * Determines if path exists, returns true or false. Stores coordinates along shortest path in an ArrayList
     *
     * @param maze 2D array representing the map
     * @param x The x-coordinate of starting position
     * @param y The y-coordinate of starting position
     * @param path ArrayList to which coordinates along shortest path will be stored
     * @return true if path exists from given point (x, y) to the target; false otherwise
     */
    public boolean searchPath(int[][] maze, int x, int y, List<Integer> path) {
        // 9 represents wizard house
        if (maze[y][x] == 9) {
            path.add(x);
            path.add(y);
            return true;
        }

        // 0 represents path
        if (maze[y][x] == 0) {
            // change to 2 to mean that this tile has been traversed
            maze[y][x] = 2;
            
            int dx = -1;
            int dy = 0;
            try {
                if (searchPath(maze, x + dx, y + dy, path)) {
                    path.add(x);
                    path.add(y);
                    return true;
                }
            } catch (Exception e) {
                // can safely ignore exceptions here.
            }
            
            dx = 1;
            dy = 0;
            try {
                if (searchPath(maze, x + dx, y + dy, path)) {
                    path.add(x);
                    path.add(y);
                    return true;
                }
            } catch (Exception e) {
                // can safely ignore exceptions here.
            }

            dx = 0;
            dy = -1;
            try {
                if (searchPath(maze, x + dx, y + dy, path)) {
                    path.add(x);
                    path.add(y);
                    return true;
                }
            } catch (Exception e) {
                // can safely ignore exceptions here.
            }

            dx = 0;
            dy = 1;
            try {
                if (searchPath(maze, x + dx, y + dy, path)) {
                    path.add(x);
                    path.add(y);
                    return true;
                }
            } catch (Exception e) {
                // can safely ignore exceptions here.
            }
        }
        // return false if no valid path from given point to wizard house
        return false;
    }

    /**
     * Constructor
     */
    public App() {
        this.configPath = "config.json"; // set path to config.json file
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override // means that this method is intended to override a method with the same signature from the superclass or interface.
    public void settings() {
        // set window size.
        size(WIDTH, HEIGHT);
    }

    /**
     * Checks if the given coordinates form a corner in path.
     *
     * @param prevCoord The coordinates of the previous point.
     * @param coord The coordinates of the current point.
     * @param nextCoord The coordinates of the next point.
     * @return true if the three points form a corner, false otherwise.
     */
    public boolean isCorner(int[] prevCoord, int[] coord, int[] nextCoord) {
        // values
        int px = prevCoord[0];
        int py = prevCoord[1];
        int x = coord[0];
        int y = coord[1];
        int nx = nextCoord[0];
        int ny = nextCoord[1];

        // logic to determine if is not corner
        if ((px == x && x == nx) || (py == y && y == ny)) {
            return false;
        }
        return true;
    }

    /**
     * Handles drawing of map
     */
    private void drawMap() {

        // initialise
        map = new char[BOARD_WIDTH][BOARD_WIDTH];

        try {
            Scanner scan = new Scanner(new File(layoutPath));
            for (int i = 0; i < BOARD_WIDTH; i++) {
                String line = scan.nextLine();
                map[i] = line.toCharArray();
            }

            for (int y = 0; y < BOARD_WIDTH; y++) {
                for (int x = 0; x < BOARD_WIDTH; x++) {
                    int xCoord = x * CELLSIZE;
                    int yCoord = TOPBAR + (y * CELLSIZE);
                    char tile;
                    PImage graphic = grassSprite;
                    try {
                        tile = map[y][x];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        tile = ' ';
                    }

                    if (firstTimeDrawMap) {
                        if (tile == 'X') {
                            if (x == 0) {
                                this.searchStartPoints.add(new int[]{x, y});
                                this.spawnPoints.add(new float[]{(float) (xCoord - CELLSIZE), (float) yCoord});
                            } else if (y == 0) {
                                this.searchStartPoints.add(new int[]{x, y});
                                this.spawnPoints.add(new float[]{(float) xCoord, (float) (yCoord - CELLSIZE)});
                            } else if (x == (BOARD_WIDTH - 1)) {
                                this.searchStartPoints.add(new int[]{x, y});
                                this.spawnPoints.add(new float[]{(float) (xCoord + CELLSIZE), (float) yCoord});
                            } else if (y == (BOARD_WIDTH - 1)) {
                                this.searchStartPoints.add(new int[]{x, y});
                                this.spawnPoints.add(new float[]{(float) xCoord, (float) (yCoord + CELLSIZE)});
                            }
                        }
                    } 
                    
                    if (tile == ' ') {
                        graphic = grassSprite;
                    } else if (tile == 'S') {
                        graphic = shrubSprite;
                    } else if (tile == 'W') {
                        wizardXCoord = xCoord + WIZARD_X_OFFSET;
                        wizardYCoord = yCoord + WIZARD_Y_OFFSET;
                        graphic = grassSprite;
                    } else if (tile == 'X') {
                        char tileLeft = 'X';
                        char tileRight = 'X';
                        char tileAbove = 'X';
                        char tileBelow = 'X';
                        try {
                            tileLeft = map[y][x-1];
                        } catch (ArrayIndexOutOfBoundsException e) {
                        }

                        try {
                            tileRight = map[y][x+1];
                        } catch (ArrayIndexOutOfBoundsException e) {
                        }

                        try {
                            tileAbove = map[y-1][x];
                        } catch (ArrayIndexOutOfBoundsException e) {
                        }

                        try {
                            tileBelow = map[y+1][x];
                        } catch (ArrayIndexOutOfBoundsException e) {
                        }
                        
                        if (tileLeft == 'X' || tileRight == 'X') {
                            graphic = this.path0_1Sprite;
                        }
                        if (tileAbove == 'X' || tileBelow == 'X') {
                            graphic = this.path0_2Sprite;
                        }
                        if (tileAbove == 'X' && tileBelow == 'X' && tileLeft == 'X' && tileRight == 'X') {
                            graphic = this.path3Sprite;
                        } else if (tileAbove != 'X' && tileBelow == 'X' && tileLeft == 'X' && tileRight == 'X') {
                            graphic = this.path2_1Sprite;
                        } else if (tileAbove == 'X' && tileBelow == 'X' && tileLeft == 'X' && tileRight != 'X') {
                            graphic = this.path2_2Sprite;
                        } else if (tileAbove == 'X' && tileBelow != 'X' && tileLeft == 'X' && tileRight == 'X') {
                            graphic = this.path2_3Sprite;
                        } else if (tileAbove == 'X' && tileBelow == 'X' && tileLeft != 'X' && tileRight == 'X') {
                            graphic = this.path2_4Sprite;
                        } else if (tileAbove != 'X' && tileBelow == 'X' && tileLeft == 'X' && tileRight != 'X') {
                            graphic = this.path1_1Sprite;
                        } else if (tileAbove == 'X' && tileBelow != 'X' && tileLeft == 'X' && tileRight != 'X') {
                            graphic = this.path1_2Sprite;
                        } else if (tileAbove == 'X' && tileBelow != 'X' && tileLeft != 'X' && tileRight == 'X') {
                            graphic = this.path1_3Sprite;
                        } else if (tileAbove != 'X' && tileBelow == 'X' && tileLeft != 'X' && tileRight == 'X') {
                            graphic = this.path1_4Sprite;
                        } 
                    }
                    if (!firstTimeDrawMap) {
                        this.image(graphic, xCoord, yCoord);
                    }
                }
            }
            if (!firstTimeDrawMap) {
                this.image(wizard_houseSprite, wizardXCoord, wizardYCoord);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (firstTimeDrawMap) {
            mazeReference = new int[BOARD_WIDTH][BOARD_WIDTH];
            maze = new int[BOARD_WIDTH][BOARD_WIDTH];
            for (int j=0; j<BOARD_WIDTH;j++) {
                for (int i=0; i<BOARD_WIDTH; i++) {
                    if (map[j][i] == 'W') {
                        mazeReference[j][i] = 9;
                        maze[j][i] = 9;

                    } else if (map[j][i] == 'X') {
                        mazeReference[j][i] = 0;
                        maze[j][i] = 0;

                    } else {
                        mazeReference[j][i] = 1;
                        maze[j][i] = 1;

                    }
                    
                }

            }
            
            for (int pointIndex = 0; pointIndex < searchStartPoints.size(); pointIndex++) {

                int[] searchStartPoint = searchStartPoints.get(pointIndex);
                int startX = searchStartPoint[0];
                int startY = searchStartPoint[1];
                
                paths.add(new ArrayList<Integer>());
                searchPath(maze, startX, startY, paths.get(pointIndex));

                // Revert maze to original by overwriting its elements with those from mazeReference
                for (int j=0;j<BOARD_WIDTH;j++) {
                    for (int i=0;i<BOARD_WIDTH;i++) {
                        int n = mazeReference[j][i];
                        maze[j][i] = n;
                    }
                }
            }

            for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
                List<Integer> path = paths.get(pathIndex);

                int[][] pathPoints = new int[(int) path.size()/2][2];
                int index = 0;
                for (int i = path.size() - 1; i >= 1; i -= 2) {
                    pathPoints[index][1] = path.get(i) * CELLSIZE + TOPBAR;
                    pathPoints[index][0] = path.get(i-1) * CELLSIZE;
                    index += 1;
                }

                ArrayList<int[]> simplifiedPath = new ArrayList<>();
                for (int i = 0; i < pathPoints.length; i++) {
                    int[] coord = pathPoints[i];

                    if (i == 0 || (i == pathPoints.length-1)) {
                        simplifiedPath.add(coord);
                        continue;
                    }
                    
                    if (isCorner(pathPoints[i-1], coord, pathPoints[i+1])) {
                        simplifiedPath.add(coord);
                    }
                }

                // add simplifiedPath (ie corners only) to allPathsPoints
                allPathsPoints.put(pathIndex, simplifiedPath);
            }

            firstTimeDrawMap = false;
        }
        
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements. Also create necessary instances of classes.
     */
	@Override
    public void setup() {
        frameRate(FPS);

        // Set window title
        this.surface.setTitle("Wizard Tower Defense");

        // get info from JSON file
        configJson = loadJSONObject(this.configPath);
        this.layoutPath = configJson.getString("layout");
        this.mana = configJson.getInt("initial_mana");
        this.manaCap = configJson.getInt("initial_mana_cap");
        MANA_POOL_INIT_COST = configJson.getFloat("mana_pool_spell_initial_cost");
        TOWER_COST = configJson.getFloat("tower_cost");
        BOMB_COST = configJson.getFloat("bomb_cost");
        BOMB_RADIUS = configJson.getFloat("bomb_damage_radius");
        BOMB_DAMAGE = configJson.getFloat("bomb_damage");
        this.MANA_POOL_COST_INCREASE_PER_USE = configJson.getFloat("mana_pool_spell_cost_increase_per_use");
        this.INIT_MANA_GAINED_PER_SECOND = configJson.getFloat("initial_mana_gained_per_second");
        this.INIT_MANA_GAINED_PER_FRAME = INIT_MANA_GAINED_PER_SECOND / FPS;
        this.MANA_POOL_MANA_GAINED_MULTIPLIER = configJson.getFloat("mana_pool_spell_mana_gained_multiplier");
        this.MANA_POOL_MANA_CAP_MULTIPLIER = configJson.getFloat("mana_pool_spell_cap_multiplier");
        
        wavesArray = configJson.getJSONArray("waves");

        // Load images during setup
        this.grassSprite = this.loadImage("src/main/resources/WizardTD/grass.png");
        this.path0_1Sprite = this.loadImage("src/main/resources/WizardTD/path0.png");
        this.path0_2Sprite = this.rotateImageByDegrees(this.path0_1Sprite, 90);
        this.path1_1Sprite = this.loadImage("src/main/resources/WizardTD/path1.png");
        this.path1_2Sprite = this.rotateImageByDegrees(this.path1_1Sprite, 90);
        this.path1_3Sprite = this.rotateImageByDegrees(this.path1_1Sprite, 180);
        this.path1_4Sprite = this.rotateImageByDegrees(this.path1_1Sprite, 270);
        this.path2_1Sprite = this.loadImage("src/main/resources/WizardTD/path2.png");
        this.path2_2Sprite = this.rotateImageByDegrees(this.path2_1Sprite, 90);
        this.path2_3Sprite = this.rotateImageByDegrees(this.path2_1Sprite, 180);
        this.path2_4Sprite = this.rotateImageByDegrees(this.path2_1Sprite, 270);
        this.path3Sprite = this.loadImage("src/main/resources/WizardTD/path3.png");
        this.shrubSprite = this.loadImage("src/main/resources/WizardTD/shrub.png");
        this.wizard_houseSprite = this.loadImage("src/main/resources/WizardTD/wizard_house.png");
        this.tower0Sprite = this.loadImage("src/main/resources/WizardTD/tower0.png");
        this.tower1Sprite = this.loadImage("src/main/resources/WizardTD/tower1.png");
        this.tower2Sprite = this.loadImage("src/main/resources/WizardTD/tower2.png");
        this.gremlinSprite = this.loadImage("src/main/resources/WizardTD/gremlin.png");
        this.wormSprite = this.loadImage("src/main/resources/WizardTD/worm.png");
        this.beetleSprite = this.loadImage("src/main/resources/WizardTD/beetle.png");
        this.bombSprite = this.loadImage("src/main/resources/WizardTD/bomb.png");

        for (int i = 1; i <= NUMBER_OF_GREMLIN_DEATH_FRAMES; i++) {
            this.gremlinDeathSprites.add(this.loadImage("src/main/resources/WizardTD/gremlin" + Integer.toString(i) + ".png"));
        }

        // create instance of ButtonController
        buttonController = new ButtonController(this);
        
        float buttonX = CELLSIZE * BOARD_WIDTH + BUTTON_LEFT_MARGIN;
        for (int i = 0; i < ButtonController.ButtonAction.values().length; i++) {
            buttonController.addButton(buttonX, (TOPBAR + (i+1)*BUTTON_TOP_MARGIN + i*Button.HEIGHT), ButtonController.ButtonAction.values()[i], ButtonController.BUTTON_LABELS[i], ButtonController.BUTTON_DESCS[i]);
        }

        // call drawMap function which displays tiles etc.
        this.drawMap();

        // create required instances of controller classes
        waveController = new WaveController(this);
        monsterController = new MonsterController(this);
        bombController = new BombController();
        towerController = new TowerController(this);
        uiManager = new UIManager(this);
    }

    /**
     * Retrieves the current cost to buy mana pool spell.
     *
     * @return The current mana pool cost, represented as a floating-point value.
     */
    public float getManaPoolCost() {
        return (MANA_POOL_INIT_COST + (numManaPoolUses * MANA_POOL_COST_INCREASE_PER_USE));
    }

    /**
     * Called when mana pool spell is bought; handles required actions.
     */
    private void manaPoolAction() {
        float cost = getManaPoolCost();

        if (this.mana >= cost) {
            this.mana -= cost;

            // increment numManaPoolUses
            numManaPoolUses++;

            // update max mana limit
            this.manaCap = Math.round(this.manaCap * MANA_POOL_MANA_CAP_MULTIPLIER);

            // update mana gained multiplier
            this.manaGainedOnKillMultiplier = this.manaTrickleMultiplier = 1 + ((float) (MANA_POOL_MANA_GAINED_MULTIPLIER - 1) * numManaPoolUses);
        }
    }

    /**
     * Called when bomb button or key is pressed; handles required actions.
     */
    public void toggleBomb() {

        // un-select other options
        rangeUpgradeSelected = false;
        speedUpgradeSelected = false;
        damageUpgradeSelected = false;

        // toggle
        if (selectedAction == Action.Bomb) {
            selectedAction = null;
        } else {
            selectedAction = Action.Bomb;
        }
    }

    /**
     * Called when upgrade range button or key is pressed; handles required actions.
     */
    private void toggleRange() {
        if (selectedAction == Action.Bomb) {
                selectedAction = null;
        }
        rangeUpgradeSelected = !rangeUpgradeSelected;
    }

    /**
     * Called when upgrade speed button or key is pressed; handles required actions.
     */
    void toggleSpeed() {
        if (selectedAction == Action.Bomb) {
                selectedAction = null;
        }
        speedUpgradeSelected = !speedUpgradeSelected;
    }

    /**
     * Called when upgrade damage button or key is pressed; handles required actions.
     */
    void toggleDamage() {
        if (selectedAction == Action.Bomb) {
                selectedAction = null;
        }
        damageUpgradeSelected = !damageUpgradeSelected;
    }

    /**
     * Called when build tower button or key is pressed; handles required actions.
     */
    void toggleTower() {
        if (selectedAction == Action.Tower) {
            selectedAction = null;
        } else {
            selectedAction = Action.Tower;
        }
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(){
        if (gameOver) {
            if ((key == 'r') || (key == 'R')) {
                
                if (!won) {
                    //restart by resetting all values, objects, etc.
                    this.numManaPoolUses = 0;
                    this.mana = configJson.getInt("initial_mana");
                    this.manaCap = configJson.getInt("initial_mana_cap");

                    manaGainedOnKillMultiplier = 1;
                    manaTrickleMultiplier = 1;
                    selectedAction = null;
                    rangeUpgradeSelected = false;
                    speedUpgradeSelected = false;
                    damageUpgradeSelected = false;
                    paused = false;
                    fastForward = false;
                    gameOver = false;
                    won = false;

                    // make fresh instances
                    monsterController = new MonsterController(this);
                    towerController = new TowerController(this);
                    waveController = new WaveController(this);
                }
                
            }
        } else {
            if ((key == 'p') || (key == 'P')) {
                paused = !paused;
            } else if ((key == 'f') || (key == 'F')) {
                fastForward = !fastForward;
            } else if (key == '1') {
                toggleRange();
            } else if (key == '2') {
                toggleSpeed();
            } else if (key == '3') {
                toggleDamage();
            } else if ((key == 'm') || (key == 'M')) {
                manaPoolAction();
            } else if ((key == 'b') || (key == 'B')) {
                toggleBomb();
            } else if ((key == 't') || (key == 'T')) {
                toggleTower();
            }
        }

        
    }

    /**
     * Checks if a tower exists at the specified coordinates (x, y).
     *
     * @param x The X-coordinate of the specified location.
     * @param y The Y-coordinate of the specified location.
     * @return An integer representing the existence of a tower:
     *         - number corresponding to index of the tower instance in the List towerController.towers, if it exists
     *         - -1 if no tower exists at the given coordinates.
     */
    public int towerExists(float x, float y) {
        for (int i = 0; i < this.towerController.towers.size(); i++) {
            Tower t = this.towerController.towers.get(i);
            if ((t.x == x) && (t.y == y)) {
                return i; // tower exists; return its index
            }
        }
        return -1;  // tower does not exist; return -1 to indicate this
    }

    /**
     * Converts a given mouse X and Y position to grid coordinates corresponding to tile indexes.
     *
     * @param mouseXPos The X position of the mouse cursor as a float.
     * @param mouseYPos The Y position of the mouse cursor as a float.
     * @return 
     *         - An integer array containing the grid coordinates (i.e. tile indexes) [gridX, gridY].
     *         - null if mouse is not inside the map
     */
    private int[] getGridCoords(float mouseXPos, float mouseYPos) {
        int mapX = (int) Math.round(mouseXPos);
        int mapY = (int) Math.round(mouseYPos - TOPBAR);
        int gridX = Math.round((mapX - (mapX % CELLSIZE)) / CELLSIZE);
        int gridY = Math.round((mapY - (mapY % CELLSIZE)) / CELLSIZE);

        if ((gridX >= 0) && (gridX < BOARD_WIDTH) && (gridY >= 0) && (gridY < BOARD_WIDTH)) {
            return new int[]{gridX, gridY}; 
        }

        return null;
    }

    /**
     * Calculates the distance between two points (x1, y1) and (x2, y2).
     *
     * @param x1 The X-coordinate of the first point.
     * @param y1 The Y-coordinate of the first point.
     * @param x2 The X-coordinate of the second point.
     * @param y2 The Y-coordinate of the second point.
     * @return The distance between the two points as a double.
     */
    public double distance(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    /**
     * Calculates cost required to build a tower, given the initial upgrades selected.
     *
     * @return Total cost as a float.
     */
    public float getBuildTowerCost() { // get cost given upgrades selected; EVEN IF CANNOT AFFORD ALL (returns theoretical total cost)
        float upgradesCost = 0;
        if (rangeUpgradeSelected) {
            upgradesCost += TOWER_UPGRADE_COST;
        }
        if (speedUpgradeSelected) {
            upgradesCost += TOWER_UPGRADE_COST;
        }
        if (damageUpgradeSelected) {
            upgradesCost += TOWER_UPGRADE_COST;
        }

        return (TOWER_COST + upgradesCost);
    }

    /**
     * Invoked when a mouse button is pressed
     *
     * @param mouseEvent The MouseEvent object containing information about the event.
     */
    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        if (mouseButton == LEFT) {
            if (selectedAction == Action.Bomb) {
                if (mana >= BOMB_COST) {
                    if ((mouseX >= 0) && (mouseX <= CELLSIZE*BOARD_WIDTH) && (mouseY >= TOPBAR) && (mouseY <= (CELLSIZE*BOARD_WIDTH + TOPBAR))) {
                        selectedAction = null;

                        // deploy bomb
                        mana -= BOMB_COST;
                        bombController.addBomb(new Bomb(mouseX, mouseY, BOMB_RADIUS, BOMB_DAMAGE, this));
                        return;
                    }
                }
            }

            for (Button button : buttonController.buttons) {
                if (button.hovered) {
                    switch (button.action) {
                        case FastForward:
                            fastForward = !fastForward;
                            break;
                        case Pause:
                            paused = !paused;
                            break;
                        case Tower:
                            toggleTower();
                            break;
                        case Range:
                            toggleRange();
                            break;
                        case Speed:
                            toggleSpeed();
                            break;
                        case Damage:
                            toggleDamage();
                            break;
                        case ManaPool:
                            manaPoolAction();
                            break;
                        case Bomb:
                            toggleBomb();
                            break;
                    }

                    return;
                }
            }

            if (selectedAction == Action.Tower) {
                if (mana >= TOWER_COST) {
                    if (rangeUpgradeSelected && speedUpgradeSelected && damageUpgradeSelected && mana < (TOWER_COST + 3*TOWER_UPGRADE_COST)) {
                        damageUpgradeSelected = false;
                    }
                    if (rangeUpgradeSelected && speedUpgradeSelected && mana < (TOWER_COST + 2*TOWER_UPGRADE_COST)) {
                        speedUpgradeSelected = false;
                    }
                    if (rangeUpgradeSelected && damageUpgradeSelected && mana < (TOWER_COST + 2*TOWER_UPGRADE_COST)) {
                        damageUpgradeSelected = false;
                    }
                    if (speedUpgradeSelected && damageUpgradeSelected && mana < (TOWER_COST + 2*TOWER_UPGRADE_COST)) {
                        damageUpgradeSelected = false;
                    }
                    if (rangeUpgradeSelected && (mana < (TOWER_COST + TOWER_UPGRADE_COST))) {
                        rangeUpgradeSelected = false;

                    }
                    if (speedUpgradeSelected && (mana < (TOWER_COST + TOWER_UPGRADE_COST))) {
                        speedUpgradeSelected = false;
                    }
                    if (damageUpgradeSelected && (mana < (TOWER_COST + TOWER_UPGRADE_COST))) {
                        damageUpgradeSelected = false;
                    }

                    float upgradesCost = 0;
                    int initRangeLevel = 0;
                    int initSpeedLevel = 0;
                    int initDamageLevel = 0;
                    if (rangeUpgradeSelected) {
                        upgradesCost += TOWER_UPGRADE_COST;
                        initRangeLevel = 1;
                    }
                    if (speedUpgradeSelected) {
                        upgradesCost += TOWER_UPGRADE_COST;
                        initSpeedLevel = 1;
                    }
                    if (damageUpgradeSelected) {
                        upgradesCost += TOWER_UPGRADE_COST;
                        initDamageLevel = 1;
                    }

                    float totalCost = TOWER_COST + upgradesCost;

                    int[] gridCoords = getGridCoords(mouseX, mouseY);
                    if (gridCoords != null) {
                        int gridX = gridCoords[0];
                        int gridY = gridCoords[1];
                        
                            try {
                                char tile = map[gridY][gridX];
                                if (tile == ' ') {
                                    float placeX = gridX * CELLSIZE;
                                    float placeY = (gridY * CELLSIZE) + TOPBAR;

                                    if (towerExists(placeX, placeY) == -1) { // if no tower exists at that location
                                        // buy tower
                                        mana -= totalCost;
                                        towerController.addTower(new Tower(placeX, placeY, initRangeLevel, initSpeedLevel,initDamageLevel, this));
                                        
                                        selectedAction = null;
                                        rangeUpgradeSelected = false;
                                        speedUpgradeSelected = false;
                                        damageUpgradeSelected = false;

                                        return;
                                    }
                                }
                            } catch (Exception e) {
                                // can safely ignore exception, because exception means selected spot is outside map, so no tower will be built.
                            }
                    }

                }
            } else { // not selected Build Tower
                // if any upgrade selected:
                if (rangeUpgradeSelected || speedUpgradeSelected || damageUpgradeSelected) {
                    if (towerController.currentHoverTowerIndex != -1) { // if a tower is being hovered over (hover index of -1 means no tower being hovered)
                        Tower towerToUpgrade = towerController.towers.get(towerController.currentHoverTowerIndex);
                        float rangeCost = TOWER_UPGRADE_COST + 10*towerToUpgrade.rangeLevel;
                        float speedCost = TOWER_UPGRADE_COST + 10*towerToUpgrade.speedLevel;
                        float damageCost = TOWER_UPGRADE_COST + 10*towerToUpgrade.damageLevel;

                        // buy upgrades if can afford, in the order specified.
                        if (rangeUpgradeSelected && mana >= rangeCost) {
                            mana -= rangeCost;
                            towerToUpgrade.rangeLevel++;
                        }
                        if (speedUpgradeSelected && mana >= speedCost) {
                            mana -= speedCost;
                            towerToUpgrade.speedLevel++;
                        }
                        if (damageUpgradeSelected && mana >= damageCost) {
                            mana -= damageCost;
                            towerToUpgrade.damageLevel++;
                        }

                        towerToUpgrade.updateStats();

                        rangeUpgradeSelected = false;
                        speedUpgradeSelected = false;
                        damageUpgradeSelected = false;

                        return;
                    }
                }
            }
        }
    }

    /**
     * If mouse is hovering over a tower, set towerController.currentHoverTowerIndex to the index of the tower currently hovering over
     */
    public void setHoverTowerIndex() {
        float x = mouseX;
        float y = mouseY;

        int[] gridCoords = getGridCoords(x, y);
        if (gridCoords != null) { // if mouse is on the map i.e. not outside map
            float pixelX = gridCoords[0] * CELLSIZE;
            float pixelY = gridCoords[1] * CELLSIZE + TOPBAR;

            int towerIndex = towerExists(pixelX, pixelY);

            this.towerController.currentHoverTowerIndex = towerIndex; // works because if not hovering over tower, towerIndex == currentHoverTowerIndex == -1
        }
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() { // main loop here. if fps is 60, then draw() runs 60 times per sec

        if (gameOver) {
            this.textSize(40);
            if (won) {
                this.fill(255,0,200);
                this.text("YOU WIN",260,200);
            } else {
                this.fill(0,255,0);
                this.text("YOU LOST",250,200);
                this.textSize(20);
                this.text("Press 'r' to restart",260,235);
            }

            monsterController.tick();
            monsterController.draw();
        } else {
            setHoverTowerIndex();
        
            if (paused) {
                this.drawMap();
                monsterController.draw();
                towerController.draw();
                uiManager.drawUI();
            } else {
                waveController.tick();

                this.mana += INIT_MANA_GAINED_PER_FRAME * manaTrickleMultiplier;
                
                this.drawMap();
                
                // call tick and draw to update and display
                monsterController.tick();
                monsterController.draw();

                // call tick and draw to update and display
                towerController.tick();
                towerController.draw();

                if (this.mana > this.manaCap) {
                    this.mana = this.manaCap;
                }

                // draw the User Interface
                uiManager.drawUI();
            }
            if (fastForward) {
                if (justCalledExtraDraw) { // this check using justCalledExtraDraw prevents infinite recursion.
                    justCalledExtraDraw = false;
                } else {
                    justCalledExtraDraw = true;
                    // call draw() again if fast-forwarding.
                    this.draw();
                }
            }
        }
    }

    /**
     * Increases mana by an amount equal to the raw mana gained from a kill, multiplied by the value manaGainedOnKillMultiplier
     *
     * @param rawMana The raw mana gained from the kill as a float.
     */
    public void gainManaForKill(float rawMana) {
        this.mana += rawMana * manaGainedOnKillMultiplier;
    }

    /**
     * Decreases mana by a specified amount and checks for game over conditions.
     *
     * @param manaDecrease The amount of mana to decrease as a float.
     */
    public void loseMana(float manaDecrease) {
        this.mana -= manaDecrease;

        if (this.mana < 0) {
            this.mana = 0;
            gameOver = true;
        }
    }

    /**
     * The main entry point for the WizardTD application.
     *
     * @param args An array of command-line arguments (not used in this program).
     */
    public static void main(String[] args) {
        PApplet.main("WizardTD.App");
    }

    /**
     * Rotates a given PImage clockwise by the specified angle in degrees. Source: https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java
     * @param pimg The image to be rotated
     * @param angle between 0 and 360 degrees (Clockwise rotation)
     * @return the new rotated image
     */
    public PImage rotateImageByDegrees(PImage pimg, double angle) {
        BufferedImage img = (BufferedImage) pimg.getNative();
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        PImage result = this.createImage(newWidth, newHeight, ARGB);
        BufferedImage rotated = (BufferedImage) result.getNative();
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result.set(i, j, rotated.getRGB(i, j));
            }
        }

        return result;
    }
}
