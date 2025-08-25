package io.github.bol;

import static io.github.bol.WorldGen.WORLD_HEIGHT;
import static io.github.bol.WorldGen.WORLD_DEPTH;
import static io.github.bol.WorldGen.WORLD_WIDTH;
import static io.github.bol.WorldGen.worldData;

import java.util.ArrayList;
import java.util.List;

public class Path {
    public int h;
    public int g;
    public int F;

    public static int[][][] PathArray;
    private static List<Path> PossiblePathTiles = new ArrayList<>();

    public static void setPathArray(){
        PathArray = new int[WORLD_WIDTH][WORLD_DEPTH][WORLD_HEIGHT];

        for (int z = 0; z < WORLD_HEIGHT; z++) {
            for (int y = 0; y < WORLD_DEPTH; y++) {
                for (int x = 0; x < WORLD_WIDTH; x++) {
                    PathArray[x][y][z] = 0;
                }
            }
        }
    }

    public static void calcPossiblePaths() {

        for (int z = 1; z < WORLD_HEIGHT; z++) {
            for (int y = 0; y < WORLD_DEPTH; y++) {
                for (int x = 0; x < WORLD_WIDTH; x++) {
                    if (worldData[x][y][z] > 1 && worldData[x][y][z - 1] < 2) {
                            PathArray[x][y][z - 1] = 1;
                    }
                }
            }
        }
    }
}
