package io.github.bol;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.*;
import java.util.stream.Collectors;

public class WorldGen {

    // TITLE: ---------------- Konstanten ----------------
    public static int WORLD_WIDTH = 10;
    public static int WORLD_DEPTH = 10;
    public static int WORLD_HEIGHT = 10;

    public static final float BLOCK_SIZE = 2f;

    public static List<Block3D> worldBlocks = new ArrayList<>();
    public static int[][][] worldData = new int[WORLD_WIDTH][WORLD_DEPTH][WORLD_HEIGHT];
    private static Random random = new Random();
    private static boolean firstPlayer = true;

    // Highlight-Block
    public static Block3D blockPointer;

    // TITLE: ---------------- Hilfsfunktionen ----------------

    private static void setSize(String worldName) {
        worldData = LevelManager.loadWorld(worldName);
        WORLD_WIDTH = worldData.length;
        WORLD_DEPTH = worldData[0].length;
        WORLD_HEIGHT = worldData[0][0].length;
    }

    public static void DrawWorld(ModelBatch batch, Environment env) {
        for (Block3D b : worldBlocks) {
            b.zeichne(batch, env);
        }
    }

    // TITLE: ---------------- Hauptmethode ----------------

    public static void LoadWorld(String worldName) {
        setSize(worldName);
        worldBlocks.clear();
        firstPlayer = true;

        // Block-Mapping vorbereiten
        Map<Integer, Block3D> blockMap = Block3D.BlockList.stream()
            .collect(Collectors.toMap(Block3D::getID, block -> block));

        // Blockplatzierung
        for (int z = 0; z < WORLD_HEIGHT; z++) {
            for (int y = 0; y < WORLD_DEPTH; y++) {
                for (int x = 0; x < WORLD_WIDTH; x++) {
                    int blockID = worldData[x][z][y];
                    if (blockID < 2) continue;

                    Block3D prototype = blockMap.get(blockID);

                    // neue Blockinstanz
                    Vector3 worldPos = new Vector3(x * BLOCK_SIZE, y * BLOCK_SIZE, z * BLOCK_SIZE);
                    Block3D block = new Block3D(prototype.asset, prototype.getID(), worldPos, x, y, z);
                    worldBlocks.add(block);

                    // Spieler nur einmal setzen
                }
            }
        }
    }

    // TITLE: ---------------- Player-Spawn ----------------

    // TITLE: ---------------- Suche / Zugriff ----------------

    public static Block3D getBlockAtGrid(int gx, int gy, int gz) {
        for (Block3D b : worldBlocks) {
            if (b.gridX == gx && b.gridY == gy && (int) b.gridZ == gz) {
                return b;
            }
        }
        return null;
    }

    // TITLE: ---------------- Highlighting ----------------

    public static void setBlockPointer(Block3D target) {
        if (blockPointer != null) blockPointer.deHighLight();

        blockPointer = target;
        if (blockPointer != null) blockPointer.highLight();
    }

    public static void deBlockPointer() {
        if (blockPointer != null) blockPointer.deHighLight();
        blockPointer = null;
    }

    // TITLE: ---------------- Dispose ----------------

    public static void disposeAll() {
        for (Block3D block : worldBlocks) {
            block.dispose();
        }
        worldBlocks.clear();
    }
}
