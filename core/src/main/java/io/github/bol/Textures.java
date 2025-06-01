package io.github.bol;

import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;
import java.util.Map;

public class Textures {
    private static final Map<String, Texture> textureMap = new HashMap<>();

    public static final Texture Blob = new Texture("Textures/Blob.png");
    public static final Texture BuiltButton = new Texture("Textures/BuiltButton.png");
    public static final Texture GrassBlock = new Texture("Textures/Grass.png");
    public static final Texture Grass2 = new Texture("Textures/Grass2.png");
    public static final Texture OakTrunk = new Texture("Textures/OakTrunk.png");
    public static final Texture Dirt = new Texture("Textures/Dirt.png");
    public static final Texture HitOverlay = new Texture("Textures/HitOverlay.png");
    public static final Texture Leaves = new Texture("Textures/Leaves.png");
    public static final Texture Player = new Texture("Textures/Player.png");
    public static final Texture OptionsButton = new Texture("Textures/OptionsButton.png");
    public static final Texture TextBox = new Texture("Textures/TextBox.png");
    public static final Texture Bar = new Texture("Textures/Bar.png");
    public static final Texture BarFiller = new Texture("Textures/BarFiller.png");
    public static final Texture Glass  = new Texture("Textures/Glass.png");
    public static final Texture Slime  = new Texture("Textures/Slime.png");


    static {
        textureMap.put("Blob", Blob);
        textureMap.put("BuiltButton", BuiltButton);
        textureMap.put("GrassBlock", GrassBlock);
        textureMap.put("Grass2", Grass2);
        textureMap.put("OakTrunk", OakTrunk);
        textureMap.put("Dirt", Dirt);
        textureMap.put("HitOverlay", HitOverlay);
        textureMap.put("Leaves", Leaves);
        textureMap.put("Player", Player);
        textureMap.put("OptionsButton", OptionsButton);
        textureMap.put("TextBox", TextBox);
        textureMap.put("Bar", Bar);
        textureMap.put("BarFiller", BarFiller);
        textureMap.put("Glass", Glass);
        textureMap.put("Slime", Slime);
    }

    public static Texture get(String name) {
        Texture tex = textureMap.get(name);
        if (tex == null) throw new IllegalArgumentException("Unbekannter Textur-Name: " + name);
        return tex;
    }



    public static void dispose() {
        Blob.dispose();
        BuiltButton.dispose();
        GrassBlock.dispose();
        Grass2.dispose();
        OakTrunk.dispose();
        Dirt.dispose();
        HitOverlay.dispose();
        Leaves.dispose();
        Player.dispose();
        OptionsButton.dispose();
        TextBox.dispose();
        Bar.dispose();
        BarFiller.dispose();
        Glass.dispose();
        Slime.dispose();
    }
}
