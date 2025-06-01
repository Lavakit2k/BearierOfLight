package io.github.bol;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class WorldGen {

    // TITLE: ---------------------- ATTRIBUTES ----------------------
    public static int WORLD_WIDTH = 10;
    public static int WORLD_HEIGHT = 10;
    public static int WORLD_DEPTH = 10;
    public static final int BLOCK_SIZE = 256;

    public static List<Block> worldBlocks = new ArrayList<>();
    public static List<Polygon> colliders = new ArrayList<>();
    public static int[][][] worldData = new int[WORLD_WIDTH][WORLD_HEIGHT][WORLD_DEPTH];
    private static Random random = new Random();
    private static boolean player = true;

    // TITLE: -------------------- GENERATE WORLD --------------------

    public static void GenerateWorldData() {
        // Schritt 1: Welt mit Erde und Gras auffüllen
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                int groundHeight = 3 + random.nextInt(3); // Basis-Höhe (3) + Hügelhöhe (0-2)

                // 3 Schichten Erde
                for (int z = 0; z < 3; z++) {
                    worldData[x][y][z] = 2; // Erde
                }

                // 3 Schichten Gras (mit variabler Höhe)
                for (int z = 3; z < groundHeight + 3; z++) {
                    worldData[x][y][z] = 1; // Gras
                }
            }
        }

        // Schritt 2: Bäume platzieren
        for (int x = 2; x < WORLD_WIDTH - 2; x++) {
            for (int y = 2; y < WORLD_HEIGHT - 2; y++) {
                if (random.nextInt(15) == 0) { // 1/15 Chance für Baum
                    int baseZ = getHighestBlock(x, y);
                    if (baseZ >= 3) { // Baum nur auf Gras platzieren
                        placeTree(x, y, baseZ + 1);
                    }
                }
            }
        }
    }

    private static void placeTree(int x, int y, int z) {
        int[][][] treeStructure = new int[][][]{
            { // Stamm (3 Blöcke hoch)
                {0, 0, 0},
                {0, 3, 0},
                {0, 0, 0}
            },
            { // Blätter (3x3 Fläche)
                {4, 4, 4},
                {4, 3, 4},
                {4, 4, 4}
            }
        };

        for (int dz = 0; dz < treeStructure.length; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int newX = x + dx;
                    int newY = y + dy;
                    int newZ = z + dz;

                    if (newX >= 0 && newX < WORLD_WIDTH && newY >= 0 && newY < WORLD_HEIGHT && newZ < WORLD_DEPTH) {
                        int blockType = treeStructure[dz][dx + 1][dy + 1];
                        if (blockType != 0) {
                            worldData[newX][newY][newZ] = blockType;
                        }
                    }
                }
            }
        }
    }

    // TITLE: -------------------- LOAD WORLD --------------------

    private static int getHighestBlock(int x, int y) {
        for (int z = WORLD_DEPTH - 1; z >= 0; z--) {
            if (worldData[x][y][z] != 0) {
                return z;
            }
        }
        return 0;
    }

    private static void setSize(String worldName){
        worldData = LevelManager.loadWorld(worldName);
        WORLD_WIDTH = worldData.length;
        WORLD_HEIGHT = worldData[0].length;
        WORLD_DEPTH = worldData[0][0].length;
    }

    private static void flipWorldData() {
        int mid = WORLD_DEPTH / 2;
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                for (int z = 0; z < mid; z++) {
                    int oppositeZ = WORLD_DEPTH - 1 - z;

                    // Werte tauschen
                    int temp = worldData[x][y][z];
                    worldData[x][y][z] = worldData[x][y][oppositeZ];
                    worldData[x][y][oppositeZ] = temp;
                }
            }
        }
    }

    public static void LoadWorld(String worldName) {
        setSize(worldName);
        worldData = new int[WORLD_WIDTH][WORLD_HEIGHT][WORLD_DEPTH];
        worldBlocks.clear();

        worldData = LevelManager.loadWorld(worldName);
        flipWorldData();

        float yOffsetAmount = -10f;
        float xOffsetAmount = -8.5f;
        float zOffsetAmount = BLOCK_SIZE / 2f + 16f; // Verbesserte Höhe für isometrische Darstellung

        // Map für schnellen Zugriff auf Blöcke
        Map<Integer, Block> blockMap = Block.BlockList.stream()
            .collect(Collectors.toMap(Block::getID, block -> block));

        for (int z = 0; z < WORLD_DEPTH; z++) { // Von oben nach unten generieren
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                for (int x = 0; x < WORLD_WIDTH; x++) {
                    int blockID = worldData[x][y][z];
                    if (blockID == 0) continue; // Kein Block an dieser Stelle


                    float isoX = (x - y) * (BLOCK_SIZE / 2f);
                    float isoY = (x + y) * (BLOCK_SIZE / 4f) + y * yOffsetAmount + x * xOffsetAmount - z * zOffsetAmount;

                    Block prototype = blockMap.get(blockID);
                    if (prototype != null) {
                        worldBlocks.add(new Block(prototype.texture, prototype.getID(), new Vector3(isoX, isoY, z)));
                    }
                    if (blockID == 1 && player && random.nextInt(0, 20) == 1) {
                        Entity.Player.setPosition(new Vector3(isoX - 500, isoY, z + 1));
                        player = false;
                    }
                }
            }
        }
        worldBlocks.sort((a, b) -> {
            int compareZ = Integer.compare((int) b.getPosition().z, (int) a.getPosition().z); // Höherer Z-Wert zuerst
            if (compareZ != 0) return compareZ;

            return Float.compare(b.getPosition().y, a.getPosition().y); // Dann nach Y absteigend
        });
        Entity.colliderOwnerMap = new HashMap<>();
        for (Block i : worldBlocks) {
            colliders.add(i.getHitbox());
            Entity.colliderOwnerMap.put(i.getHitbox(), i); // Damit die Entity weiß, welcher Block zu welcher Hitbox gehört
        }
    }

    // TITLE: ---------------------- METHODES ----------------------

    //for highlight
    private static Block pointer = new Block(Textures.Dirt, 0, Vector3.Zero);

    public static void DrawWorld(SpriteBatch s) {
        for (Block block : worldBlocks) {
            block.zeichne(s);
        }
    }

    public static void highLightBlock() {
        int bestScore = Integer.MAX_VALUE;
        Block bestBlock = null;

        for (Block block : WorldGen.worldBlocks) {

            if (block.isHit(MainScreen.worldInputPosition)) {

                int score = block.getZ() * 1000000 + block.getY() * 1000 + block.getX();

                if (score < bestScore) {
                    bestScore = score;
                    bestBlock = block;
                }
            }
        }

        if(bestBlock != null){
            pointer = bestBlock;
            pointer.highLight();
        }
    }

    public static void deHighLightBlock() {
        pointer.deHighLight();
        pointer = new Block(Textures.Dirt, 0, Vector3.Zero);
    }

    public static void disposeAll() {
        for (Block block : worldBlocks) {
            block.dispose();
        }
        worldBlocks.clear();
    }
}
