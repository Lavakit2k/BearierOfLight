package io.github.bol;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public abstract class BaseObject {

    // TITLE: ----------------- ATTRIBUTES -------------------
    protected Texture texture;
    protected int ID;
    protected Vector3 position;
    public Vector2 size;
    private Polygon hitbox;
    protected float rotation;
    public float helligkeit = 1;
    public boolean visible = true;

    // TITLE: ----------------- Constructor -------------------

    public BaseObject(Texture t, int id, Vector3 pos) {
        this.size = new Vector2(1, 1);
        this.texture = t;
        this.ID = id;
        this.position = new Vector3(pos.x, pos.y, pos.z);
        createHitbox();
    }

    // TITLE: ----------------- Hitbox -------------------

    abstract void createHitbox();
    abstract void updateHitbox();
    protected void createHexagonHitbox() {
        float w = size.x * (texture != null ? texture.getWidth() : 256);
        float h = size.y * (texture != null ? texture.getHeight() : 256);

        float centerX = position.x + w / 2;
        float centerY = position.y + h / 2;

        float r = w * 0.5f;  // Horizontalen Radius reduziert, um das Hexagon schmaler zu machen
        float h_offset = h * 0.25f; // Vertikaler Offset bleibt gleich

        float[] vertices = new float[]{
            centerX, centerY + h_offset * 2,  // Oben Mitte
            centerX + r, centerY + h_offset + 10,  // Rechts Oben
            centerX + r, centerY - h_offset - 10,  // Rechts Unten
            centerX, centerY - h_offset * 2,  // Unten Mitte
            centerX - r, centerY - h_offset - 10,  // Links Unten
            centerX - r, centerY + h_offset + 10   // Links Oben
        };

        hitbox = new Polygon(vertices);
    }
    protected void createRectangleHitbox() {
        float width = (texture != null ? texture.getWidth() : 256);
        float height = (texture != null ? texture.getHeight() : 256);

        float x = position.x;
        float y = position.y;
        float[] vertices = new float[]{
            x, y,
            x + width, y,
            x + width, y + height,
            x, y + height
        };

        hitbox = new Polygon(vertices);
    }
    public void updateHexagonHitbox() {
        float w = size.x * (texture != null ? texture.getWidth() : 32);
        float h = size.y * (texture != null ? texture.getHeight() : 32);
        float centerX = position.x + w / 2;
        float centerY = position.y + h / 2;
        hitbox.setPosition(centerX, centerY);
    }
    public void updateRectangleHitbox() {
        hitbox.setPosition(this.position.x, this.position.y);
    }
    public boolean isHit(Vector2 pos) {
        return hitbox.contains(pos.x, pos.y);
    }

    // TITLE: ----------------- Methodes -------------------

    public void zeichne(SpriteBatch s) {
        if (visible && texture != null) {
            s.setColor(helligkeit, helligkeit, helligkeit, 1);
            s.draw(texture, position.x, position.y, texture.getWidth() / 2f, texture.getHeight() / 2f,
                texture.getWidth(), texture.getHeight(), size.x, size.y, rotation, 0, 0,
                texture.getWidth(), texture.getHeight(), false, false);
        }
    }
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
    public abstract BaseObject Clone();


    //TODO: in main klasse vorrechnen?
    public void updateVisibility(OrthographicCamera camera) {
        float camLeft = camera.position.x - camera.viewportWidth / 2;
        float camRight = camera.position.x + camera.viewportWidth / 2;
        float camBottom = camera.position.y - camera.viewportHeight / 2;
        float camTop = camera.position.y + camera.viewportHeight / 2;

        // Prüfe, ob das Objekt im sichtbaren Bereich liegt
        this.visible = position.x + size.x > camLeft &&
            position.x < camRight &&
            position.y + size.y > camBottom &&
            position.y < camTop;
    }

    // TITLE: ----------------- Setter -------------------

    public void setPosition(Vector3 newPos){
        position.set(newPos);
        updateHitbox();
    }
    public void setSize(Vector2 newSize) {
        size.set(newSize);
        createHitbox();
    }
    public void setRotation(float r) {
        rotation = r;
        hitbox.setRotation(r);
    }
    public void turn(){
        this.visible = !visible;
    }
    public void on(){
        this.visible = true;
    }
    public void off(){
        this.visible = false;
    }

    // TITLE: ----------------- Getter -------------------

    public int getPosZ() {return (int)position.z;}
    public int getPosY() {return (int)position.y;}
    public int getPosX() {return (int)position.x;}
    public Vector3 getPosition(){
        return position;
    }
    public int getID(){
        return ID;
    }
    public Polygon getHitbox() {
        return hitbox;
    }

}
