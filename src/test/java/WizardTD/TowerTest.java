package WizardTD;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

public class TowerTest {
    private Tower tower;
    private App app;
    private TowerController towerController;

    int rangeLevel = 1;
    int speedLevel = 2;
    int damageLevel = 3;

    @BeforeEach
    public void setUp() {
        app = new App();
        app.monsterController = new MonsterController(app);
        app.monsterController.monsters = new LinkedList<>();
        app.monsterController.addMonster(new Monster(0,app,"gremlin",100,1,1,10));
        tower = new Tower(5, 10, rangeLevel, speedLevel, damageLevel, app);
    }

    @Test
    public void testTowerConstructor() {

        tower = new Tower(5,10, rangeLevel, speedLevel, damageLevel, app);
        assertEquals(tower.x, 5, 0.01);
        assertEquals(tower.y, 10, 0.01);
        
        assertEquals(tower.rangeLevel, rangeLevel, 0.01);
        assertEquals(tower.speedLevel, speedLevel, 0.01);
        assertEquals(tower.damageLevel, damageLevel, 0.01);
    }

    @Test
    public void testUpdateStats() {
        tower.updateStats();
        // Assert that damage, range, and firing speed are updated correctly.
        assertEquals(tower.range, 96, 0.01);
        assertEquals(tower.fireballFrameInterval, 20, 0.01);
        assertEquals(tower.damage, 40, 0.01);
    }

    @Test
    public void testCreateFireball() {
        tower = new Tower(0,0, rangeLevel, speedLevel, damageLevel, app);
        tower.frameCount = tower.fireballFrameInterval;
        tower.tick();
        assertEquals(tower.frameCount, 0, 0.01);
        assertEquals(tower.fireballs.size(), 1, 0.01);
        assertEquals(tower.fireballs.get(0).centreX, 16, 0.01);
        assertEquals(tower.fireballs.get(0).centreY, 16, 0.01);
        app.monsterController.addMonster(new Monster(0,app,"gremlin",100,1,1,10));
        assertEquals(app.monsterController.monsters.get(0).centreX, 16, 0.01);
        assertEquals(app.monsterController.monsters.get(0).centreY, 16, 0.01);
        tower.tick();
        assertEquals(tower.fireballs.size(), 0, 0.01);

    }

    @Test
    public void testDraw() {

    }



}
