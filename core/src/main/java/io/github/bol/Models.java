package io.github.bol;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.assets.AssetManager;

import net.mgsx.gltf.loaders.glb.GLBAssetLoader;
import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class Models {
    private static final Map<String, SceneAsset> sceneAssets = new HashMap<>();

    public static void load(AssetManager assets) {
        assets.setLoader(SceneAsset.class, ".glb", new GLBAssetLoader());

        // Einmal pro Blocktyp laden
        assets.load("models/Oak.glb", SceneAsset.class);
        assets.load("models/Dirt.glb", SceneAsset.class);
        assets.load("models/Grass.glb", SceneAsset.class);
        assets.load("models/Leaves.glb", SceneAsset.class);
        assets.load("models/test.glb", SceneAsset.class);
    }

    public static void init(AssetManager assets) {
        sceneAssets.put("Oak", assets.get("models/Oak.glb", SceneAsset.class));
        sceneAssets.put("Dirt", assets.get("models/Dirt.glb", SceneAsset.class));
        sceneAssets.put("Grass", assets.get("models/Grass.glb", SceneAsset.class));
        sceneAssets.put("Leaves", assets.get("models/Leaves.glb", SceneAsset.class));
        sceneAssets.put("Test", assets.get("models/test.glb", SceneAsset.class));
    }

    // Neue Block-Instanz in der Welt
    public static ModelInstance newBlock(String name, float x, float y, float z) {
        SceneAsset asset = sceneAssets.get(name);
        ModelInstance inst = new ModelInstance(asset.scene.model);
        inst.transform.setToTranslation(x, y, z);
        return inst;
    }

    public static SceneAsset get(String name) {
        SceneAsset asset = sceneAssets.get(name);
        if (asset == null) throw new IllegalArgumentException("Unbekannter Model-Name: " + name);
        return asset;
    }



    public static void dispose() {
        Models.get("Oak").dispose();
        Models.get("Grass").dispose();
        Models.get("Dirt").dispose();
        Models.get("Leaves").dispose();
    }
}
