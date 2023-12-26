package WizardTD;

import processing.core.PApplet; // from SampleTest.java file given in scaffold
import processing.data.JSONObject;
import processing.core.PImage;

import org.junit.jupiter.api.Test; // from SampleTest.java file given in scaffold
import static org.junit.jupiter.api.Assertions.*; // from SampleTest.java file given in scaffold

import java.util.LinkedList;

import org.junit.jupiter.api.BeforeEach;

public class AppTest extends PApplet {
    App app;

    @BeforeEach
    public void setup() {
        app = new App();
        App.main(new String[]{});
        app.configPath = "config.json";
        app.configJson = new JSONObject();
        app.towerController = new TowerController(app);
        
        app.monsterController = new MonsterController(app);
        app.monsterController.monsters = new LinkedList<>();
    }

    @Test
    public void testConstructor() {
        assertTrue(app.configPath.equals("config.json"));
    }

    @Test
    public void testIsCorner() {
        assertTrue(app.isCorner(new int[]{1,0}, new int[]{1,1}, new int[]{2,1}));
        assertFalse(app.isCorner(new int[]{1,0}, new int[]{1,1}, new int[]{1,5}));
    }

    @Test
    public void testDraw() {
        
    }

    @Test
    public void testGainMana() {
        app.mana = 200.0f;
        app.manaGainedOnKillMultiplier = 1;
        app.gainManaForKill(100);
        assertEquals(app.mana, 300, 0.01);

        app.mana = 200.0f;
        app.manaGainedOnKillMultiplier = 0.7f;
        app.gainManaForKill(100);
        assertEquals(app.mana, 270, 0.01);
    }

    @Test
    public void testLoseMana() {
        app.mana = 200.0f;
        app.loseMana(50.0f);
        assertEquals(app.mana, 150, 0.01);
        assertTrue(!app.gameOver);

        app.loseMana(150.0f);
        assertEquals(app.mana, 0, 0.01);
        assertTrue(!app.gameOver);

        app.mana = 20.0f;
        app.loseMana(20.11f);
        assertEquals(app.mana, 0, 0.0001);
        assertTrue(app.gameOver);

    }

    @Test
    public void testSetHoverIndex() {
        
        app.towerController.addTower(new Tower(App.CELLSIZE, App.TOPBAR + App.CELLSIZE, 0, 0, 0, app));
        app.mouseX = App.CELLSIZE;
        app.mouseY = App.TOPBAR + App.CELLSIZE;
        app.setHoverTowerIndex();
        assertEquals(app.towerController.currentHoverTowerIndex, 0, 0.001);
        
        app.mouseX = 2 * App.CELLSIZE - 1;
        app.mouseY = App.TOPBAR + 2 * App.CELLSIZE - 1;
        app.setHoverTowerIndex();
        assertEquals(app.towerController.currentHoverTowerIndex, 0, 0.001);

        app.mouseX = 2 * App.CELLSIZE + 1;
        app.mouseY = App.TOPBAR + 2 * App.CELLSIZE + 1;
        app.setHoverTowerIndex();
        assertEquals(app.towerController.currentHoverTowerIndex, -1, 0.001);
    }

    @Test
    public void testDistance() {
        
        float x1 = 2f;
        float x2 = 5f;
        float y1 = -5f;
        float y2 = -2f;
        double result = app.distance(x1,y1,x2,y2);

        assertEquals(result, Math.sqrt(18), 0.001);   
    }

    @Test
    public void testGetBuildTowerCost() {
        
        app.rangeUpgradeSelected = true;
        app.speedUpgradeSelected = true;
        app.damageUpgradeSelected = true;
        app.TOWER_COST = 100;
        app.TOWER_UPGRADE_COST = 20;
        float result = app.getBuildTowerCost();
        assertEquals(160, result, 0.001);

        app.rangeUpgradeSelected = true;
        app.speedUpgradeSelected = false;
        app.damageUpgradeSelected = true;
        result = app.getBuildTowerCost();
        assertEquals(140, result, 0.001);

        app.rangeUpgradeSelected = false;
        app.speedUpgradeSelected = false;
        app.damageUpgradeSelected = false;
        result = app.getBuildTowerCost();
        assertEquals(100, result, 0.001);
        
    }

    @Test
    public void testSearchPath() {

        int[][] maze = new int[][]{
            {1,1,0,1},
            {1,0,0,1},
            {1,1,0,9}
            
        };
        LinkedList<Integer> path = new LinkedList<>();
        assertTrue(app.searchPath(maze, 2, 0, path));
        assertTrue(path.get(0) == 3);
        assertTrue(path.get(1) == 2);
        assertTrue(path.get(2) == 2);
        assertTrue(path.get(3) == 2);
        assertTrue(path.get(4) == 2);
        assertTrue(path.get(5) == 1);
        assertTrue(path.get(6) == 2);
        assertTrue(path.get(7) == 0);

        maze = new int[][]{
            {1,1,0,1},
            {1,0,1,1},
            {1,1,0,9}
        };
        path = new LinkedList<>();
        assertTrue(!app.searchPath(maze, 2, 0, path));
        
    }

    @Test
    public void testToggleBomb() {
        app.selectedAction = App.Action.Bomb;
        app.toggleBomb();
        assertTrue(!app.rangeUpgradeSelected);
        assertTrue(!app.speedUpgradeSelected);
        assertTrue(!app.damageUpgradeSelected);
        assertNull(app.selectedAction);

        app.selectedAction = App.Action.Tower;
        app.toggleBomb();
        assertTrue(!app.rangeUpgradeSelected);
        assertTrue(!app.speedUpgradeSelected);
        assertTrue(!app.damageUpgradeSelected);
        assertTrue(app.selectedAction == App.Action.Bomb);

        app.selectedAction = null;
        app.toggleBomb();
        assertTrue(!app.rangeUpgradeSelected);
        assertTrue(!app.speedUpgradeSelected);
        assertTrue(!app.damageUpgradeSelected);
        assertTrue(app.selectedAction == App.Action.Bomb);
    }
}