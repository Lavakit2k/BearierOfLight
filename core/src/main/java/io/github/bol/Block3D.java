package io.github.bol;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;


import java.util.ArrayList;
import java.util.List;

import static io.github.bol.WorldGen.WORLD_HEIGHT;

public class Block3D extends BaseModelObject {

    // TITLE: ---------------- ATTRIBUTES ----------------
    public static List<Block3D> BlockList = new ArrayList<>();
    private Array<Attribute> originalAttributes = new Array<>();
    public int gridX;
    public int gridY;
    public int gridZ;

    // TITLE: ---------------- CONSTRUCTOR ----------------

    public Block3D(SceneAsset asset, int id, Vector3 pos) {
        super(asset, id, pos);
        for (Material mat : instance.materials) {
            originalAttributes.add(mat.get(ColorAttribute.Diffuse));
        }
        createHitbox();
    }

    public Block3D(SceneAsset asset, int id, Vector3 pos, int x, int y, int z) {
        super(asset, id, pos);
        gridZ = z;
        gridX = x;
        gridY = y;
        for (Material mat : instance.materials) {
            originalAttributes.add(mat.get(ColorAttribute.Diffuse));
        }
        createHitbox();
    }

    // TITLE: ---------------- INHERITANCE ----------------
    @Override
    public BaseModelObject clone() {
        return new Block3D(this.asset, this.ID, new Vector3(this.position), this.gridX, this.gridY, this.gridZ);
    }
    @Override
    protected void createHitbox() {
        instance.calculateBoundingBox(hitbox);
        hitbox.mul(instance.transform); // ins Weltkoordinatensystem
    }

    @Override
    public void updateHitbox() {
        instance.calculateBoundingBox(hitbox);
        hitbox.mul(instance.transform);
    }

    public Vector3[] getCorners(){
        Vector3 min = new Vector3();
        Vector3 max = new Vector3();
        hitbox.getMin(min);
        hitbox.getMax(max);

        Vector3[] corners = new Vector3[8];
        corners[0] = new Vector3(min.x, min.y, min.z);
        corners[1] = new Vector3(max.x, min.y, min.z);
        corners[2] = new Vector3(min.x, max.y, min.z);
        corners[3] = new Vector3(max.x, max.y, min.z);
        corners[4] = new Vector3(min.x, min.y, max.z);
        corners[5] = new Vector3(max.x, min.y, max.z);
        corners[6] = new Vector3(min.x, max.y, max.z);
        corners[7] = new Vector3(max.x, max.y, max.z);
        return corners;
    }

    // TITLE: ---------------- METHODS ----------------
    public static void InitListBlock() {
        // Hier deine GLB-Assets einsetzen
        BlockList.add(new Block3D(Models.get("Grass"), 2, Vector3.Zero));
        BlockList.add(new Block3D(Models.get("Dirt"), 3, Vector3.Zero));
        BlockList.add(new Block3D(Models.get("Oak"), 4, Vector3.Zero));
        BlockList.add(new Block3D(Models.get("Leaves"), 5, Vector3.Zero));
        BlockList.add(new Block3D(Models.get("Test"), 6, Vector3.Zero));
    }

    /*
    * mat.set(ColorAttribute.createDiffuse(1f, 0f, 0f, 1f)); // Rot
    * mat.set(ColorAttribute.createAmbient(0.8f, 0.8f, 0.8f, 1f)); //Umgebungslichtfarbe
    * mat.set(ColorAttribute.createEmissive(Color.GREEN));  //Block leuchtet von selbst
    */

    public void highLight() {
        for (Material mat : instance.materials) {
            mat.set(ColorAttribute.createDiffuse(0.8f, 0.8f, 0.8f, 1f)); // heller, gelblich
        }
    }

    public void deHighLight() {
        for (Material mat : instance.materials) {
            mat.set(ColorAttribute.createDiffuse(1f, 1f, 1f, 1f)); // zurück auf normal (weiß)
        }
    }

    public void dispose(){
        this.asset.dispose();
    }
}
