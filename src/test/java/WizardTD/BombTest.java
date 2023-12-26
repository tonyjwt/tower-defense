package WizardTD;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class BombTest {
    App app;

    @BeforeEach
    public void setup() {
        app = new App();
        app.monsterController = new MonsterController(app);
    }

    @Test
    public void testBombConstructor() {
        Bomb bomb = new Bomb(1.0f, 2.0f, 3.0f, 10.0f, app);
        assertEquals(1.0f, bomb.centreX, 0.00001);
        assertEquals(2.0f, bomb.centreY, 0.00001);
    }

    @Test
    public void testBombDamage() {
        int spawnPointIndex = 0;
        float monsterInitHP = (float) 100;
        float bombDamage = (float) 10;
        float armour = (float) 1;
        float bombRadius = 3;

        Monster monster = new Monster(spawnPointIndex, app, "gremlin", monsterInitHP, 1, armour, 50);
        app.monsterController = new MonsterController(app);
        app.monsterController.addMonster(monster);
        Bomb bomb = new Bomb(monster.centreX, monster.centreY, bombRadius, bombDamage, app);

        // Test if bomb applies correct damage to the monster 
        assertEquals(monsterInitHP-monster.armour*bombDamage, monster.currentHP, 0.01);

        // test: different armour value
        armour = (float) 0.1;
        monster = new Monster(spawnPointIndex, app, "gremlin", monsterInitHP, 1, armour, 50);
        app.monsterController.addMonster(monster);
        bomb = new Bomb(monster.centreX, monster.centreY, bombRadius, bombDamage, app);
        assertEquals(monsterInitHP-monster.armour*bombDamage, monster.currentHP, 0.01);

        // test: distance = radius
        monster = new Monster(spawnPointIndex, app, "gremlin", monsterInitHP, 1, armour, 50);
        app.monsterController.addMonster(monster);
        bomb = new Bomb(monster.centreX+bombRadius, monster.centreY, bombRadius, bombDamage, app);
        assertEquals(monsterInitHP-monster.armour*bombDamage, monster.currentHP, 0.01);

        // test: distance slightly larger than radius (edge case)
        monster = new Monster(spawnPointIndex, app, "gremlin", monsterInitHP, 1, armour, 50);
        app.monsterController.addMonster(monster);
        bomb = new Bomb((float) (monster.centreX+bombRadius+0.01f), monster.centreY, bombRadius, bombDamage, app);
        assertEquals(monsterInitHP, monster.currentHP, 0.01); // assert: no damage caused ie initial HP = current HP
    }
}