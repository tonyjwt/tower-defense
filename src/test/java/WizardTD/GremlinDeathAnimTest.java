package WizardTD;

import processing.core.PImage;
import processing.core.PApplet; 
import org.junit.jupiter.api.Test; 
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class GremlinDeathAnimTest {

    App app;

    @BeforeEach
    public void setup() {
        app = new App();
        app.monsterController = new MonsterController(app);
    }

    @Test
    public void testGremlinDeathAnimConstructor() {
        int x = 50;
        int y = 60;
        GremlinDeathAnim gremlinDeathAnim = new GremlinDeathAnim(x,y,app);
        assertEquals(gremlinDeathAnim.drawX, x + app.monsterController.GREMLIN_X_OFFSET, 0.01);
        assertEquals(gremlinDeathAnim.drawY, y + app.monsterController.GREMLIN_Y_OFFSET, 0.01);
    }
}