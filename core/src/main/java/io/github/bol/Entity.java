package io.github.bol;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Entity extends Object implements Health {
    public static List<Entity> EntityList = new ArrayList<>();
    public static Entity Player = new Entity(Textures.Slime, 1, Vector3.Zero);

    private float hp;
    protected Vector2 velocity = new Vector2(0, 0);
    private float gravity = -500f;
    private boolean isOnGround = false;

    // Diese Map muss z. B. in WorldGenerator oder UIManager definiert und befüllt werden:
    public static Map<Polygon, Block> colliderOwnerMap;

    public Entity(Texture t, int id, Vector3 c) {
        super(t, id, c);
    }

    public void hpChange(float h) {
        hp += h;
    }

    public void updatePlayer(float delta, List<Polygon> colliders) {
        // Schwerkraft anwenden, falls nicht auf Boden
        if (!isOnGround) {
            velocity.y += gravity * delta;
        }

        // Vertikale Bewegung ausführen
        position.y += velocity.y * delta;

        // Hitbox aktualisieren
        updateHexagonHitbox();

        // Bodenprüfung zurücksetzen
        isOnGround = false;

        // Aktuelle Z-Ebene bestimmen
        int currentZ = (int) Math.floor(position.z);

        // Alle Collider prüfen, die zu Blöcken direkt unter dem Spieler gehören
        for (Polygon collider : colliders) {
            Block block = colliderOwnerMap.get(collider);
            if (block == null) continue;

            if ((int) block.getPosition().z == currentZ - 1) {
                if (Intersector.overlapConvexPolygons(this.getHitbox(), collider)) {
                    // Landung auf Block
                    position.y = block.getPosition().y + 16; // Optional: exaktere Platzierung
                    velocity.y = 0;
                    isOnGround = true;
                    break;
                }
            }
        }
    }

    @Override
    public Entity Clone() {
        return new Entity(this.texture, this.ID, this.getPosition());
    }

    @Override
    protected void createHitbox() {
        createHexagonHitbox();
    }

    public static void InitListEntity() {
        Player.hp = 100;
        EntityList.add(Player);
    }
}
