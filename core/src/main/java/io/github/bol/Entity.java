package io.github.bol;

import static io.github.bol.WorldGen.WORLD_HEIGHT;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Entity extends BaseObject implements Health {

    // TITLE: ----------------- ATTRIBUTES -------------------

    public static List<Entity> EntityList = new ArrayList<>();

    public int gridX;
    public int gridY;
    public int gridZ;

    private float hp;

    // TITLE: ----------------- Constructor -------------------
    public Entity(Texture t, int id, Vector3 pos) {
        super(t, id, pos);
        gridZ = WORLD_HEIGHT - 1 - (int)pos.z;
    }
    public Entity(Texture t, int id, Vector3 pos, int x, int y) {
        super(t, id, pos);
        gridZ = WORLD_HEIGHT - 1 - (int)pos.z;
        gridX = x;
        gridY = y;
    }

    // TITLE: ----------------- Inheritance -------------------
    @Override
    public Entity Clone() {
        return new Entity(this.texture, this.ID, this.getPosition(), this.gridX, this.gridY);
    }
    @Override
    protected void createHitbox() {
        super.createHexagonHitbox();
    }
    @Override
    public void updateHitbox(){
        super.updateHexagonHitbox();
    }

    // TITLE: ------------------ Methodes --------------------
    public void hpChange(float h) {
        hp += h;
    }
    public void updatePlayer(int x, int y, int z){
        gridX = x;
        gridY = y;
        gridZ = z;
    }
    public static void InitListEntity() {
    }
}
