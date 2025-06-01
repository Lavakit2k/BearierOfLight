package io.github.bol;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.Gdx;
import java.util.HashMap;
import java.util.Map;

public class Fonts {
    public static BitmapFont ultraSmallFont;
    public static BitmapFont smallFont;
    public static BitmapFont defaultFont;
    public static BitmapFont largeFont;
    public static BitmapFont titleFont;

    private static final Map<String, BitmapFont> fontMap = new HashMap<>();

    static {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-VariableFont_wdth,wght.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 10;
        ultraSmallFont = generator.generateFont(parameter);

        parameter.size = 40;
        smallFont = generator.generateFont(parameter);

        parameter.size = 50;
        defaultFont = generator.generateFont(parameter);

        parameter.size = 65;
        largeFont = generator.generateFont(parameter);

        parameter.size = 90;
        titleFont = generator.generateFont(parameter);
    }

    static {
        fontMap.put("ultraSmall", ultraSmallFont);
        fontMap.put("small", smallFont);
        fontMap.put("default", defaultFont);
        fontMap.put("large", largeFont);
        fontMap.put("title", titleFont);
    }

    public static BitmapFont get(String name) {
        BitmapFont font = fontMap.get(name);
        if (font == null) throw new IllegalArgumentException("Unbekannter Font-Name: " + name);
        return font;
    }


    public static void dispose() {
        ultraSmallFont.dispose();
        smallFont.dispose();
        defaultFont.dispose();
        largeFont.dispose();
        titleFont.dispose();
    }
}

