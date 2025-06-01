package io.github.bol;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;


public class Text {

    public boolean visible = true;
    public BitmapFont font = Fonts.defaultFont;
    public String text = "";
    public Vector3 position = new Vector3();
    public UI master;
    public Color color;

    public Text(String s) {
        text = s;
    }
    public Text(String s, Vector3 v) {
        text = s;
        position.set(v);
    }
    public Text(String s, UI vm) {
        text = s;
        master = vm;
    }

    public void write(String s) {
        text = s;
    }

    public Text Clone(){
        Text t = new Text(this.text, master);
        t.font = this.font;
        return t;
    }

    public void setCenterPosition(){
        if(master != null){
            GlyphLayout layout = new GlyphLayout(this.font, this.text);

            float textWidth = layout.width;
            float textHeight = layout.height;

            float uiCenterX = master.position.x + master.texture.getWidth() / 2;
            float uiCenterY = master.position.y + master.texture.getHeight() / 2;

            this.position.set(
                uiCenterX - textWidth / 2,
                uiCenterY + textHeight / 2,
                master.position.z
            );
        }
    }

    public void setCenterPosition(Vector3 v){
        if(master != null){
            GlyphLayout layout = new GlyphLayout(this.font, this.text);

            float textWidth = layout.width;
            float textHeight = layout.height;

            float uiCenterX = master.position.x + master.texture.getWidth() / 2;
            float uiCenterY = master.position.y + master.texture.getHeight() / 2;

            this.position.set(
                uiCenterX - textWidth / 2 + v.x,
                uiCenterY + textHeight / 2 + v.y,
                master.position.z + v.z
            );
        }
    }

    public void zeichne(SpriteBatch batch) {
        if (visible) {
            font.draw(batch, text, position.x, position.y);
        }
    }

}

