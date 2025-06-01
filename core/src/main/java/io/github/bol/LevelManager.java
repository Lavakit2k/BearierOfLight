package io.github.bol;

public class LevelManager {

    private static AssetLoader assetLoader;

    public static void setAssetLoader(AssetLoader loader) {
        assetLoader = loader;
    }

    public static int[][][] loadWorld(String filename) {
        if (assetLoader != null) {
            return assetLoader.loadWorldFromAssets(filename);
        }
        return null;
    }
}
