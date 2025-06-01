package io.github.bol;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;

import java.util.*;
import java.util.stream.Collectors;

public class WorldGen {

    // TITLE:---------------------- Konstanten & Felder ----------------------
    public static int WORLD_WIDTH = 10;
    public static int WORLD_DEPTH = 10;
    public static int WORLD_HEIGHT = 10;
    public static final int BLOCK_SIZE = 256;
    public static final float HEIGHT_PER_LAYER = BLOCK_SIZE / 2f + 16f;

    private static float yOffsetAmount = -10f;
    private static float xOffsetAmount = -8.5f;
    private static float zOffsetAmount = BLOCK_SIZE / 2f + 16f;

    public static List<Block> worldBlocks = new ArrayList<>();
    public static List<Polygon> colliders = new ArrayList<>();
    public static int[][][] worldData = new int[WORLD_WIDTH][WORLD_DEPTH][WORLD_HEIGHT];
    private static Random random = new Random();
    private static boolean playerExists = true;

    // Highlight-Block
    public static Block pointer = new Block(Textures.Dirt, 0, Vector3.Zero);

    // TITLE:---------------------- Hilfsfunktionen ----------------------

    private static void setSize(String worldName) {
        worldData = LevelManager.loadWorld(worldName);
        WORLD_WIDTH = worldData.length;
        WORLD_DEPTH = worldData[0].length;
        WORLD_HEIGHT = worldData[0][0].length;
    }

    //Flip
    /*private static void flipWorldData() {
        int[][][] flipped = new int[WORLD_WIDTH][WORLD_DEPTH][WORLD_HEIGHT];

        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_DEPTH; y++) {
                for (int z = 0; z < WORLD_HEIGHT; z++) {
                    int flippedX = WORLD_WIDTH - 1 - x;
                    int flippedY = WORLD_DEPTH - 1 - y;
                    int flippedZ = WORLD_HEIGHT - 1 - z;

                    flipped[flippedY][flippedX][flippedZ] = worldData[x][y][z];
                }
            }
        }

        worldData = flipped;
    } */

    // TITLE:---------------------- Hauptmethode ----------------------

    public static void LoadWorld(String worldName) {
        //Technical
        setSize(worldName);
        worldBlocks.clear();
        playerExists = true;
        worldData = LevelManager.loadWorld(worldName);

        // Block-Mapping vorbereiten
        Map<Integer, Block> blockMap = Block.BlockList.stream()
            .collect(Collectors.toMap(Block::getID, block -> block));


        // Blockplatzierung
        for (int z = 0; z < WORLD_HEIGHT; z++) {
            for (int y = 0; y < WORLD_DEPTH; y++) {
                for (int x = 0; x < WORLD_WIDTH; x++) {
                    int blockID = worldData[x][y][z];
                    if (blockID == 0) continue;

                    float isoX = (x - y) * (BLOCK_SIZE / 2f);
                    float isoY = (x + y) * (BLOCK_SIZE / 4f) + y * yOffsetAmount + x * xOffsetAmount - z * zOffsetAmount;

                    Block prototype = blockMap.get(blockID);
                    if (prototype == null) continue;

                    Block block = new Block(prototype.texture, prototype.getID(), new Vector3(isoX, isoY, z), x , y);
                    worldBlocks.add(block);

                    // Spielerplatzierung nur einmal
                    if (blockID == 2 && playerExists) {
                        Entity.Player.setPosition(new Vector3(isoX, isoY , z + 1));
                        playerExists = false;
                    }
                }
            }
        }
        Path.setPathArray();
        Path.calcPossiblePaths();
        //TODO: Debug if everything is covert
        /*for (int z = 0; z < WORLD_HEIGHT; z++) {
            for (int y = 0; y < WORLD_DEPTH; y++) {
                for (int x = 0; x < WORLD_WIDTH; x++) {
                    if(Path.PathArray[x][y][z] == 1){
                        WorldGen.worldBlocks.add(new Block(Textures.Glass, 7, WorldGen.gridToIso(x,y,z), x , y));
                    }
                }
            }
        }*/

        // Sortierung für korrekte Zeichenreihenfolge
        worldBlocks.sort((a, b) -> {
            int compareZ = Integer.compare((int) b.getPosition().z, (int) a.getPosition().z);
            if (compareZ != 0) return compareZ;
            return Float.compare(b.getPosition().y, a.getPosition().y);
        });

        // Collider-Map für Entity-Kollisionen
        Entity.colliderOwnerMap = new HashMap<>();
        colliders.clear();
        for (Block block : worldBlocks) {
            colliders.add(block.getHitbox());
            Entity.colliderOwnerMap.put(block.getHitbox(), block);
        }







    }

    // TITLE:---------------------- Methodes ----------------------

    public static Block getBlockAtIsoPosition(int X, int Y, int z) {

        for (Block b : worldBlocks) {
            if (b.gridX == X && b.gridY == Y && (int)b.getZ() == z) {
                return b;
            }
        }
        return null;
    }



    private static int getHighestBlock(int x, int y) {
        for (int z = WORLD_HEIGHT - 1; z >= 0; z--) {
            if (worldData[x][y][z] != 0) {
                return z;
            }
        }
        return 0;
    }

    public static Vector3 gridToIso(int x, int y, int z) {
        float halfBlock = BLOCK_SIZE / 2f;
        float quarterBlock = BLOCK_SIZE / 4f;

        float isoX = (x - y) * halfBlock;
        float isoY = (x + y) * quarterBlock
            + y * yOffsetAmount
            + x * xOffsetAmount
            - z * zOffsetAmount;

        return new Vector3(isoX, isoY, z);
    }

    public static Vector3 isoToGrid(int x, int y, float z) {
        float halfBlock = BLOCK_SIZE / 2f;
        float quarterBlock = BLOCK_SIZE / 4f;

        // Rückrechnung der Basisformeln
        float approxXplusY = (y + z * zOffsetAmount) / quarterBlock;
        float approxXminusY = x / halfBlock;

        // Näherung der Grid-Koordinaten (vor y/x Offset-Korrektur!)
        float approxX = (approxXplusY + approxXminusY) / 2f;
        float approxY = (approxXplusY - approxXminusY) / 2f;

        // Korrektur durch yOffset & xOffset
        float correctedX = (approxX - yOffsetAmount * approxY / BLOCK_SIZE) / (1 + xOffsetAmount / BLOCK_SIZE);
        float correctedY = (approxY - xOffsetAmount * approxX / BLOCK_SIZE) / (1 + yOffsetAmount / BLOCK_SIZE);

        int gx = Math.round(correctedX);
        int gy = Math.round(correctedY);
        int gz = Math.round(z);

        return new Vector3(gx, gy, gz);
    }

    public static Vector3 getPositionOnTop(int x, int y) {
        int z = getHighestBlock(x, y);
        float isoX = (x - y) * (BLOCK_SIZE / 2f);
        float isoY = (x + y) * (BLOCK_SIZE / 4f) + y * -10f + x * -8.5f - z * (BLOCK_SIZE / 2f + 16f);
        return new Vector3(isoX, isoY, z + 1); // +1 damit es *über* dem Block ist
    }



    // TITLE:---------------------- Rendering & Highlighting ----------------------

    public static void DrawWorld(SpriteBatch s) {
        for (Block block : worldBlocks) {
            block.zeichne(s);
        }
    }

    public static void highLightBlock() {
        int bestScore = Integer.MAX_VALUE;
        Block bestBlock = null;

        for (Block block : worldBlocks) {
            if (block.isHit(MainScreen.worldInputPosition)) {
                int score = block.getZ() * 1000000 + block.getY() * 1000 + block.getX();
                if (score < bestScore) {
                    bestScore = score;
                    bestBlock = block;
                }
            }
        }

        if (bestBlock != null) {
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
