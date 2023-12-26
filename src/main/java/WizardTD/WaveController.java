package WizardTD;

import processing.data.JSONArray;
import java.util.ArrayList;
import java.util.Random;

/**
 * Class responsible for managing wave and spawn timings. Extends the Controller class.
 */
public class WaveController extends Controller {

    public JSONArray wavesArray;
    private Random random;
    public int numWaves;
    public int numSpawnPoints;
    
    public int waveNum = -1;
    public int timeUntilNextWave;

    // wave-dependent vars ("c" stands for "current wave")
    private JSONArray cMonstersArray;
    private int cNumMonsterTypes;
    private int cTotalQty;
    private int[] cQtysRemaining; // qty remaining for each monster type
    
    private float currentDuration;
    private float currentPreWavePause;
    private int framesUntilSpawn;
    private int nextWaveSpawnFrame;
    private ArrayList<Integer> spawnFrames;
    private int frame = 0; // total frames since start of current wave
    private int spawnFrameIndex = 0;

    private float nextPreWavePause;

    /**
     * Get the index of the Nth positive number in an array.
     *
     * @param N    The desired (zero-indexed) position of the positive number.
     * @param nums The array to search for positive numbers.
     * @return The index of the Nth positive number (counting from 0), or -1 if not found.
     */
    private int getIndexOfNthPositiveNum(int N, int[] nums) {
        int count = 0;
        for (int i = 0; i < nums.length; i++) {

            if (nums[i] > 0) {
                count++;
                if (count == N+1) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Get the number of positive numbers in an array of integers.
     *
     * @param nums The array to count positive numbers.
     * @return The count of positive numbers in the array.
     */
    private int countPositive(int[] nums) {
        int count = 0;
        for (int num : nums) {
            if (num > 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get the parameters of the current wave.
     *
     * @param waveNum The wave number (zero-indexed) sfor which to retrieve parameters.
     */
    private void getCurrentWaveParameters (int waveNum) {
        cMonstersArray = wavesArray.getJSONObject(waveNum).getJSONArray("monsters");
        cNumMonsterTypes = cMonstersArray.size();
        cQtysRemaining = new int[cNumMonsterTypes];
        cTotalQty = 0;
        for (int i = 0; i < cNumMonsterTypes; i++) {
            int typeQty = cMonstersArray.getJSONObject(i).getInt("quantity");
            cQtysRemaining[i] = typeQty;
            cTotalQty += typeQty;
        }

        this.currentDuration = wavesArray.getJSONObject(waveNum).getFloat("duration");
        this.currentPreWavePause = wavesArray.getJSONObject(waveNum).getFloat("pre_wave_pause");
        try {
            this.nextPreWavePause = wavesArray.getJSONObject(waveNum + 1).getFloat("pre_wave_pause");
        } catch (Exception e) {
            this.nextPreWavePause = 0;
        }
        

    }

    /**
     * Set a list of exact frames at which monsters will spawn during the current wave.
     */
    private void setSpawnFrames() {
        framesUntilSpawn = 0;
        nextWaveSpawnFrame = (int) (framesUntilSpawn + (currentDuration + nextPreWavePause) * app.FPS);

        spawnFrames = new ArrayList<>();
        int framesBetweenSpawns = (int) (currentDuration / cTotalQty * app.FPS);

        for (int i = 0; i < cTotalQty; i++) {
            spawnFrames.add(framesUntilSpawn + (i*framesBetweenSpawns));
        }
    }

    /**
     * Start the next wave of monsters.
     */
    private void nextWave() {
        if (waveNum < (numWaves-1)) {
            waveNum += 1;
            frame = 0;
            spawnFrameIndex = 0;

            getCurrentWaveParameters(waveNum);
            setSpawnFrames(); // also sets nextWaveSpawnFrame var
        }
    }

    /**
     * Calculate the sum of values in an array.
     *
     * @param nums The array of values to calculate the sum.
     * @return The sum of values in the array.
     */
    private int arraySum(int[] nums) {
        int total = 0;
        for (int n : nums) {
            total += n;
        }
        return total;
    }

    @Override
    public void draw() {

    }

    /**
     * Update the state of the wave controller and handle wave progression. This method is called every frame by the App class.
     */
    @Override
    public void tick() {
        if ((waveNum == numWaves - 1) && (arraySum(cQtysRemaining) == 0) && (app.monsterController.monsters.size() == 0)) {
            // victory
            app.won = true;
            app.gameOver = true;
        } 

        timeUntilNextWave = (int) ((nextWaveSpawnFrame - frame) / app.FPS);

        if (frame >= nextWaveSpawnFrame) {
            nextWave();
        }
        frame++;

        try {

            if (frame >= spawnFrames.get(spawnFrameIndex)) {
                int spawnPointIndex = random.nextInt(numSpawnPoints);

                String type = "";
                int typeIndex = 0;
                if (cNumMonsterTypes > 1) {
                    int numOptions = countPositive(cQtysRemaining);
                    int nonzeroIndex = random.nextInt(numOptions);
                    
                    typeIndex = getIndexOfNthPositiveNum(nonzeroIndex, cQtysRemaining);
                    
                    type = cMonstersArray.getJSONObject(typeIndex).getString("type");

                } else {
                    type = cMonstersArray.getJSONObject(0).getString("type");
                }
                cQtysRemaining[typeIndex]--;
                float hp = cMonstersArray.getJSONObject(typeIndex).getFloat("hp");
                float spd = cMonstersArray.getJSONObject(typeIndex).getFloat("speed");
                float armour = cMonstersArray.getJSONObject(typeIndex).getFloat("armour");
                float manaGained = cMonstersArray.getJSONObject(typeIndex).getFloat("mana_gained_on_kill");

                app.monsterController.addMonster(new Monster(spawnPointIndex, app, type, hp, spd, armour, manaGained));
                spawnFrameIndex++;
            }
        } catch (Exception e) {
        }
    }

    /**
     * Constructor method which initialises the WaveController instance
     *
     * @param app Instance of App
     */
    public WaveController(App app) {
        frame = 0; // total frames since start of current wave
        spawnFrameIndex = 0; // added after adding fastforward

        this.random = new Random();
        this.app = app;
        numSpawnPoints = app.spawnPoints.size();
        wavesArray = app.configJson.getJSONArray("waves");
        numWaves = wavesArray.size();
        
        getCurrentWaveParameters(0);
        nextWaveSpawnFrame = (int) currentPreWavePause * app.FPS;
        frame = 0;
    }
}