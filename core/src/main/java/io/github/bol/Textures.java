package io.github.bol;

import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;
import java.util.Map;

public class Textures {
    private static final Map<String, Texture> textureMap = new HashMap<>();

    public static final Texture Blob = new Texture("textures/Blob.png");
    public static final Texture BuiltButton = new Texture("textures/BuiltButton.png");
    public static final Texture OptionsButton = new Texture("textures/OptionsButton.png");
    public static final Texture TextBox = new Texture("textures/TextBox.png");
    public static final Texture Bar = new Texture("textures/Bar.png");
    public static final Texture BarFiller = new Texture("textures/BarFiller.png");


    static {
        textureMap.put("Blob", Blob);
        textureMap.put("BuiltButton", BuiltButton);
        textureMap.put("OptionsButton", OptionsButton);
        textureMap.put("TextBox", TextBox);
        textureMap.put("Bar", Bar);
        textureMap.put("BarFiller", BarFiller);
    }

    public static Texture get(String name) {
        Texture tex = textureMap.get(name);
        if (tex == null) throw new IllegalArgumentException("Unbekannter Textur-Name: " + name);
        return tex;
    }



    public static void dispose() {
        Blob.dispose();
        BuiltButton.dispose();
        OptionsButton.dispose();
        TextBox.dispose();
        Bar.dispose();
        BarFiller.dispose();
    }
}
