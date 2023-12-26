package WizardTD;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

public class BombControllerTest {
    BombController bombController;

    @BeforeEach
    public void setup() {
        bombController = new BombController();
    }

    @Test
    public void testConstructor() {
        assertEquals(bombController.bombs.size(), 0, 0.0001);
    }

    @Test
    public void testAddBomb() {
        App app = new App();

        float bombDamage = (float) 10;
        float bombRadius = 3;

        app.monsterController = new MonsterController(app);

        bombController.bombs.add(new Bomb(5, 5, bombRadius, bombDamage, app));
        bombController.bombs.add(new Bomb(5, 6, bombRadius, bombDamage, app));
        assertEquals(bombController.bombs.size(), 2, 0.001);
    }
}