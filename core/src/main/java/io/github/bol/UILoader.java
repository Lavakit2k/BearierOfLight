package io.github.bol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class UILoader {

    public static void loadUI(String path) {
        XmlReader xml = new XmlReader();
        Element root = xml.parse(Gdx.files.internal(path));

        for (Element el : root.getChildrenByName("element")) {
            String textureName = el.getChildByName("texture").getText();
            int id = el.getInt("id");

            Element posEl = el.getChildByName("position");
            float x = posEl.getFloatAttribute("x");
            float y = posEl.getFloatAttribute("y");
            float z = posEl.getFloatAttribute("z");

            Texture texture = Textures.get(textureName);
            UI ui = new UI(texture, id, new Vector3(x, y, z));

            Element textsEl = el.getChildByName("texts");
            if (textsEl != null) {
                for (Element textEl : textsEl.getChildrenByName("text")) {
                    String font = textEl.getAttribute("font", "default");
                    String colorHex = textEl.getAttribute("color", "FFFFFF");
                    String value = textEl.getText();

                    Text text = new Text(value, ui);
                    text.font = Fonts.get(font);
                    text.color = Color.valueOf(colorHex);
                    //text.master = ui;

                    ui.addText(text);
                }
            }

            UI.UIList.add(ui);
        }
    }
}
