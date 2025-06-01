package io.github.bol;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.mgsx.gltf.scene3d.scene.SceneAsset;

public abstract class BaseModelObject {

    protected SceneAsset asset;
    protected ModelInstance instance;
    protected BoundingBox hitbox = new BoundingBox();
    protected int ID;
    protected Vector3 position;
    protected float rotation;
    public boolean visible = true;

    public BaseModelObject(SceneAsset asset, int id, Vector3 pos) {
        this.asset = asset;
        this.ID = id;
        this.position = new Vector3(pos);
        this.instance = new ModelInstance(asset.scene.model);
        this.instance.transform.setToTranslation(pos);
    }

    public void zeichne(ModelBatch batch, Environment env) {
        if (visible && instance != null) {
            batch.render(instance, env);
        }
    }

    // TITLE: ----------------- Methodes -------------------
    protected abstract void createHitbox();
    public abstract void updateHitbox();
    public abstract BaseModelObject clone();

    // TITLE: ----------------- Setter -------------------

    public void setPosition(Vector3 newPos) {
        this.position.set(newPos);
        this.instance.transform.setToTranslation(newPos);
    }
    public void setRotation(float degrees, float axisX, float axisY, float axisZ) {
        this.rotation = degrees;
        this.instance.transform.setToRotation(axisX, axisY, axisZ, degrees);
        this.instance.transform.setTranslation(position);
    }
    public void setRotation(float degrees) {
        this.rotation = degrees;
        this.instance.transform.setToRotation(this.position.x, this.position.y, this.position.z, degrees);
        this.instance.transform.setTranslation(position);
    }
    // TITLE: ----------------- Getter -------------------
    public int getID() { return ID; }
    public Vector3 getPosition() { return position; }
    public BoundingBox getHitbox() {
        return hitbox;
    }
}
