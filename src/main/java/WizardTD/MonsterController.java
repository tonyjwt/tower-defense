package WizardTD;

import java.util.LinkedList;

/**
 * Class responsible for controlling and managing monsters in the game. Extends the Controller class.
 */
public class MonsterController extends Controller {

    public final float GREMLIN_X_OFFSET = 6; // X OFFSET of 6 to centre the gremlin in its tile
    public final float GREMLIN_Y_OFFSET = 6; // Y OFFSET of 6 to centre the gremlin in its tile

    public LinkedList<Monster> monsters;
    public LinkedList<GremlinDeathAnim> gremlinDeathAnims;

    private Monster tempMonster;
    private GremlinDeathAnim tempGremlinDeathAnim;

    /**
     * Initializes a MonsterController object
     *
     * @param app Instance of App
     */
    public MonsterController(App app) {
        this.app = app;
        monsters = new LinkedList<>();
        gremlinDeathAnims = new LinkedList<>();
    }

    /**
     * Draws monsters and their death animations on the screen.
     */
    @Override
    public void draw() {

        for (int i = 0; i < gremlinDeathAnims.size(); i++) {
            try {
                tempGremlinDeathAnim = gremlinDeathAnims.get(i);
                tempGremlinDeathAnim.draw();
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }

        if (!app.gameOver) {
            for (int i = 0; i < monsters.size(); i++) {
                try {
                    tempMonster = monsters.get(i);
                    tempMonster.draw();
                } catch (ArrayIndexOutOfBoundsException e) {
                    return;
                }
            }
        }
    }

    /**
     * Updates the state of monsters and their death animations during each game tick.
     */
    @Override
    public void tick() {
        if (!app.gameOver) {
            for (int i = 0; i < monsters.size(); i++) {
                try {
                    tempMonster = monsters.get(i);
                    if (!tempMonster.isAlive()) {
                        app.gainManaForKill(tempMonster.manaGainedOnKill);

                        float x = tempMonster.x;
                        float y = tempMonster.y;

                        if (tempMonster.type.equals("gremlin")) {
                            gremlinDeathAnims.add(new GremlinDeathAnim(x, y, app));
                        }
                        
                        monsters.remove(i);

                        continue;
                    }
                    tempMonster.tick();
                } catch (ArrayIndexOutOfBoundsException e) {
                    return;
                }
            }
        }

        for (int i = 0; i < gremlinDeathAnims.size(); i++) {
            try {
                tempGremlinDeathAnim = gremlinDeathAnims.get(i);

                if ((tempGremlinDeathAnim.animSpriteIndex >= (app.NUMBER_OF_GREMLIN_DEATH_FRAMES - 1)) && (tempGremlinDeathAnim.frameCount >= 4)) {
                    gremlinDeathAnims.remove(i);

                    continue;
                }

                tempGremlinDeathAnim.tick();
                
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }
    }

    /**
     * Adds a monster to the list of monsters currently alive and spawned in game.
     *
     * @param monster The instance of Monster to be added.
     */
    public void addMonster(Monster monster) {
        monsters.add(monster);
    }

    /**
     * Removes a monster from the list of monsters being controlled.
     *
     * @param monster The instance of Monster to be removed.
     */
    public void removeMonster(Monster monster) {
        monsters.remove(monster);
    }
}