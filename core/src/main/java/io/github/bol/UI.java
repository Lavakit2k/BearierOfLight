package io.github.bol;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import java.util.ArrayList;

public class UI extends Object {
    public Color textColor = Color.WHITE;
    public ArrayList<Text> texts = new ArrayList<>();

    public static ArrayList<UI> UIList = new ArrayList<UI>();

    public UI(Texture t, int id, Vector3 pos) {
        super(t, id, pos);
        visible = false;
    }
    public UI(Texture t, int id) {
        super(t, id, Vector3.Zero);
        visible = false;
    }

    @Override
    public UI Clone() {
        return new UI(this.texture, this.ID, this.getPosition());
    }
    @Override
    protected void createHitbox(){
        createRectangleHitbox();
    }

    public void turn(){
        this.visible = !visible;
    }
    public void updateUI(Vector3 pos) {
        this.position.set(pos);
        this.updateRectangleHitbox();
    }
    public Vector3 fromCenter(Vector3 uiCam){
        float uiCenterX = uiCam.x - this.texture.getWidth() / 2;
        float uiCenterY = uiCam.y - this.texture.getHeight() / 2;
        return new Vector3(uiCenterX, uiCenterY, 0);
    }
    public Vector3 fromCenter(){
        float uiCenterX = this.position.x + this.texture.getWidth() / 2;
        float uiCenterY = this.position.y + this.texture.getHeight() / 2;
        return new Vector3(uiCenterX, uiCenterY, 0);
    }

    public void addText(Text text) {
        texts.add(text);

        GlyphLayout layout = new GlyphLayout(text.font, text.text);

        float textWidth = layout.width;
        float textHeight = layout.height;

        float uiCenterX = this.position.x + this.texture.getWidth() / 2;
        float uiCenterY = this.position.y + this.texture.getHeight() / 2;

        text.position.set(
            uiCenterX - textWidth / 2,
            uiCenterY + textHeight / 2,
            this.position.z
        );
    }
    @Override
    public void zeichne(SpriteBatch spriteBatch){
        super.zeichne(spriteBatch);
        for (Text text : texts) {
            if(this.visible){
                text.zeichne(spriteBatch);
            }
        }
    }


    public static void DrawAll(SpriteBatch s){
        for (UI ui : UIList) {
            ui.zeichne(s);
        }
    }
    public static void InitListUI(){
        UILoader.loadUI("ui_config.xml");

    }
}
