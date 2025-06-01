package io.github.bol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
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


public class MainScreen implements Screen, GestureListener {

    // TITLE: ---------------------- ATTRIBUTES ----------------------

    //region Attributes
    private final OrthographicCamera uiCamera;
    private final Viewport uiViewport;
    private final PerspectiveCamera Cam3D;

    private final SpriteBatch spriteBatch;
    private final ShapeRenderer shapeRenderer;

    public static final float worldWidth = Gdx.graphics.getWidth();
    public static final float worldHeight = Gdx.graphics.getHeight();

    public static Ray InputRay;
    public static Vector2 worldInputPosition;
    public static Vector2 uiInputPosition = new Vector2();
    Plane ground = new Plane(new Vector3(0, 1, 0), 0);
    Vector3 worldTouch = new Vector3();


    private static final float FIXED_TIME_STEP = 0.016f; // 16 ms = 60fps
    private float accumulatedTime = 0f;
    private float frameTimeSum = 0f;
    private int frameCount = 0;
    private static final int SAMPLE_SIZE = 100;
    private float[] frameTimes = new float[SAMPLE_SIZE];
    private int frameIndex = 0;
    private boolean filled = false;

    AssetManager assets;
    SceneAsset sceneAsset;
    Scene scene;
    ModelBatch modelBatch;
    Environment environment;
    GestureDetector gestureDetector;

    public static final InputManager inputManager = new InputManager();
    private final UIManager uiManager = new UIManager();

    private Block3D Block1;
    public static Block3D Block2;
    //endregion

    public MainScreen() {
        //TITLE: Cam
        uiCamera = new OrthographicCamera();
        uiViewport = new FitViewport(worldWidth, worldHeight, uiCamera);
        Cam3D = new PerspectiveCamera(40, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        setupCamera();

        //TITLE: 3D
        assets = new AssetManager();
        Models.load(assets);
        assets.finishLoading();
        Models.init(assets);
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f));
        environment.add(new DirectionalLight().set(1f, 1f, 1f, -1f, -0.8f, -0.2f));

        //TITLE: REST
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        // Gesture
        gestureDetector = new GestureDetector(this);
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(gestureDetector);
        Gdx.input.setInputProcessor(multiplexer);
        //DEBUG
        Block1 = new Block3D(Models.get("Leaves"), 32, new Vector3(0, 0, 0));
        Block2 = new Block3D(Models.get("Leaves"), 32, new Vector3(2, 0, 0));
    }

    @Override
    public void show() {
        UI.InitListUI();
        Block3D.InitListBlock();
        Entity.InitListEntity();
        WorldGen.LoadWorld("DebugEz.xml");
        uiManager.startUI(uiCamera.position);
        inputManager.setManager(uiManager);
    }

    @Override
    public void render(float delta) {
        // TITLE: ---------------------- INIT ----------------------
        Gdx.gl.glClearColor(0.55f, 0.8f, 1f, 1);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        updateCameras();
        updateVisibility3D();

        accumulatedTime += delta;
        handleInput(delta);

        draw3D();
        //draw3DHitboxes();
        drawUI();
        //drawUiHitbox();

    }


    // TITLE: ----------------- UPDATE METHODS -------------------
    private void handleInput(float delta) {
        while (accumulatedTime >= FIXED_TIME_STEP) {
            if (Gdx.input.isTouched()) {
                // --- UI-Position für Buttons ---
                Vector3 uiPos = uiViewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                uiInputPosition.set(uiPos.x, uiPos.y);

                // --- 3D Welt ---
                InputRay = Cam3D.getPickRay(Gdx.input.getX(), Gdx.input.getY());

                // Block selektieren
                Block3D selected = null;
                float closest = Float.MAX_VALUE;
                for (Block3D block : WorldGen.worldBlocks) {
                    if (Intersector.intersectRayBoundsFast(InputRay, block.getHitbox())) {
                        float dst = InputRay.origin.dst2(block.position);
                        if (dst < closest) {
                            closest = dst;
                            selected = block;
                        }
                    }
                }
                WorldGen.setBlockPointer(selected);

                if (Gdx.input.justTouched()) {
                    inputManager.shortPress(delta);
                }

            } else {
                inputManager.releaseInput();
            }

            accumulatedTime -= FIXED_TIME_STEP;
        }
    }


    private void updateCameras() {
        uiCamera.update();
        Cam3D.update();
    }
    private void setupCamera() {
        uiCamera.setToOrtho(false, worldWidth, worldHeight);
        uiCamera.position.set(worldWidth / 2, worldHeight / 2, 0);
        Cam3D.position.set(60f, 60f, 60f);

        Cam3D.direction.set(-1f, -1f, -1f).nor(); // Blickrichtung fix (schräg nach unten)
        Cam3D.up.set(Vector3.Y);                  // oben = Z-Achse
        Cam3D.near = 0.1f;
        Cam3D.far = 500f;

        Cam3D.update();
        uiCamera.update();
    }
    private void updateVisibility3D() {
        for (Block3D block : WorldGen.worldBlocks) {
            if (block.getHitbox() != null) {
                // Check, ob BoundingBox im Sichtfeld liegt
                block.visible = Cam3D.frustum.boundsInFrustum(block.getHitbox());
            }
        }
    }

    private void draw3D(){
        modelBatch.begin(Cam3D);
        //Block1.zeichne(modelBatch, environment);
        //Block2.zeichne(modelBatch, environment);
        WorldGen.DrawWorld(modelBatch,environment);
        modelBatch.end();
    }
    private void draw3DHitboxes() {
        shapeRenderer.setProjectionMatrix(Cam3D.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        for (Block3D b : WorldGen.worldBlocks) {
            if (b.getHitbox() != null) {
                drawBox(b);
            }
        }
        shapeRenderer.end();
    }
    private void drawBox(Block3D block) {
        Vector3[] c = block.getCorners();

        // unten
        shapeRenderer.line(c[0], c[1]);
        shapeRenderer.line(c[1], c[3]);
        shapeRenderer.line(c[3], c[2]);
        shapeRenderer.line(c[2], c[0]);

        // oben
        shapeRenderer.line(c[4], c[5]);
        shapeRenderer.line(c[5], c[7]);
        shapeRenderer.line(c[7], c[6]);
        shapeRenderer.line(c[6], c[4]);

        // vertikal
        shapeRenderer.line(c[0], c[4]);
        shapeRenderer.line(c[1], c[5]);
        shapeRenderer.line(c[2], c[6]);
        shapeRenderer.line(c[3], c[7]);
    }
    private void drawUI(){
        spriteBatch.setProjectionMatrix(uiCamera.combined);
        spriteBatch.begin();
        UI.DrawAll(spriteBatch);

        //Title: For Debug
        drawPerformanceStats(spriteBatch);
        spriteBatch.end();
    }
    private void drawUiHitbox(){
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLUE);
        for (UI ui : UI.UIList) {
            if (ui.getHitbox() != null) {
                shapeRenderer.polygon(ui.getHitbox().getTransformedVertices());
            }
        }
        shapeRenderer.end();
    }
    private void drawPerformanceStats(SpriteBatch s) {
        float frameTime = Gdx.graphics.getDeltaTime() * 1000; // ms
        frameTimes[frameIndex] = frameTime;
        frameIndex = (frameIndex + 1) % SAMPLE_SIZE;
        if (frameIndex == 0) filled = true;

        int samples = filled ? SAMPLE_SIZE : frameIndex;
        float sum = 0;
        for (int i = 0; i < samples; i++) {
            sum += frameTimes[i];
        }
        float avgFrameTime = sum / samples;

        int fps = Gdx.graphics.getFramesPerSecond();

        String stats = String.format("FPS: %d | Avg Frame Time: %.2f ms", fps, avgFrameTime);
        Fonts.ultraSmallFont.draw(s, stats, 10, Gdx.graphics.getHeight() - 10);
    }
    //TODO drawEntity


    // TITLE: ---------------------- OTHER METHODS ----------------------
    @Override public void resize(int width, int height) {
        uiViewport.update(width, height);
        uiViewport.apply();
        uiCamera.position.set(worldWidth / 2, worldHeight / 2, 0);
        uiCamera.update();
    }
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        assets.dispose();
        Textures.dispose();
        Fonts.dispose();
        WorldGen.disposeAll();
        Models.dispose();
    }

    // TITLE: ---------------------- GESTURE HANDLING ----------------------
    private void enforceIsoOrientation() {
        Cam3D.direction.set(-1f, -1f, -1f).nor(); // fixierter Winkel
        Cam3D.up.set(0, 1, 0);
    }
    @Override public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        float initialDistance = initialPointer1.dst(initialPointer2);
        float currentDistance = pointer1.dst(pointer2);

        float zoomFactor = (float)Math.pow(initialDistance / currentDistance, 0.2f);

        Cam3D.fieldOfView *= zoomFactor;
        Cam3D.fieldOfView = Math.max(20f, Math.min(100f, Cam3D.fieldOfView));

        enforceIsoOrientation();
        Cam3D.update();
        return true;
    }
    @Override public boolean longPress(float x, float y) {
        return true;
    }
    @Override public boolean pan(float x, float y, float deltaX, float deltaY) {
        float speed = 0.05f;

        // Basisvektoren aus Kamerarichtung
        Vector3 forward = new Vector3(Cam3D.direction).nor();
        Vector3 right   = new Vector3(forward).crs(Vector3.Y).nor(); // Querachse
        forward.y = 0; // nur am Boden bewegen
        right.y   = 0;

        // Pan in Kamerakoordinaten übersetzen
        Cam3D.position.add(right.scl(-deltaX * speed));
        Cam3D.position.add(forward.scl(deltaY * speed));

        // Höhe fix (z. B. 10)
        if (Cam3D.position.y < 10f) Cam3D.position.y = 10f;

        enforceIsoOrientation();
        Cam3D.update();
        return true;
    }
    @Override public void pinchStop() {}
    @Override public boolean touchDown(float x, float y, int pointer, int button) { return false; }
    @Override public boolean tap(float x, float y, int count, int button) { return false; }
    @Override public boolean fling(float velocityX, float velocityY, int button) { return false; }
    @Override public boolean panStop(float x, float y, int pointer, int button) { return false; }
    @Override public boolean zoom(float initialDistance, float distance) { return false; }
}
