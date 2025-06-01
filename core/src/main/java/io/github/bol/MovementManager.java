package io.github.bol;

import java.util.*;

public class MovementManager {

    private static Entity Player = Entity.Player;
    private static List<Block> BlockPath = new ArrayList<>();
    public static boolean reached;


    public static void CalcPath() {
        //Path.calcPossiblePaths();
    }

    public static void MoveToBlock() {
        //TODO::if(Player reached block) reached = true;
    }

    private static float moveCooldown = 0f; // Timer

    public static void update(float delta) {
        if (WorldGen.pointer.ID == 0 || !reached) return;

        // Timer hochzählen
        moveCooldown += delta;

        if (moveCooldown >= 0.25f) { // 0.25 Sekunden sind vergangen
            moveCooldown = 0f;       // Timer zurücksetzen
            MoveToBlock();           // Methode ausführen
        }
    }
}
