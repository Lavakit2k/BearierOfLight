package io.github.bol;

import static io.github.bol.WorldGen.WORLD_DEPTH;
import static io.github.bol.WorldGen.WORLD_HEIGHT;
import static io.github.bol.WorldGen.WORLD_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

//TODO: HP System
//TODO: Enemy

public class MainScreen implements Screen, GestureListener {

    // TITLE: ---------------------- ATTRIBUTES ----------------------
    private final OrthographicCamera mainCamera;
    private final Viewport mainViewport;

    private final OrthographicCamera uiCamera;
    private final Viewport uiViewport;

    private final SpriteBatch spriteBatch;
    private final ShapeRenderer shapeRenderer;

    private static final float FIXED_TIME_STEP = 0.016f; // 16 ms = 60fps
    private float accumulatedTime = 0f;
    private final Vector2 lastTouch = new Vector2();

    public static final float worldWidth = Gdx.graphics.getWidth();
    public static final float worldHeight = Gdx.graphics.getHeight();
    public static Vector2 worldInputPosition = new Vector2();
    public static Vector2 uiInputPosition = new Vector2();

    private float frameTimeSum = 0f;
    private int frameCount = 0;

    // region OBJECTS
    private final InputManager inputManager = new InputManager();
    private final UIManager uiManager = new UIManager();
    private final Block debugBlock = new Block(Textures.Slime, 32, new Vector3(100, 1600, 0));
    // endregion

    public MainScreen() {
        // region INITIALIZATION
        mainCamera = new OrthographicCamera();
        mainViewport = new FitViewport(worldWidth, worldHeight, mainCamera);

        uiCamera = new OrthographicCamera();
        uiViewport = new FitViewport(worldWidth, worldHeight, uiCamera);

        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        setupCamera();

        // Input
        GestureDetector gestureDetector = new GestureDetector(this);
        Gdx.input.setInputProcessor(gestureDetector);
    }

    @Override
    public void show() {
        UI.InitListUI();
        Block.InitListBlock();
        Entity.InitListEntity();
        WorldGen.LoadWorld("Debug.xml");
        uiManager.startUI(uiCamera.position);
        inputManager.setManager(uiManager);


    }

    @Override
    public void render(float delta) {
        // TITLE: ---------------------- INIT ----------------------
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        updateCameras();
        accumulatedTime += delta;

        mainViewport.apply();
        handleInput(delta);
        Entity.Player.updatePlayer(delta, WorldGen.colliders);
        MovementManager.update(delta);

        mainDraw();
        //drawPerformanceStats();

        //uiViewport.apply();
        drawUI();
        drawEntityHitbox();
        //drawMainHitbox();
    }



    // TITLE: ----------------- UPDATE METHODS -------------------
    private void handleInput(float delta) {
        while (accumulatedTime >= FIXED_TIME_STEP) {
            if (Gdx.input.isTouched()) {
                //InputPositions
                Vector3 worldPos = mainCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                worldInputPosition.set(worldPos.x, worldPos.y);

                Vector3 uiPos = uiViewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                uiInputPosition.set(uiPos.x, uiPos.y);

                if (Gdx.input.justTouched()) {
                    // Kurzer Tap
                    inputManager.shortPress(delta);
                }

            } else {
                // Kein Touch -> Input loslassen
                inputManager.releaseInput();
            }

            accumulatedTime -= FIXED_TIME_STEP;
        }
    }

    private void updateCameras() {
        mainCamera.zoom = MathUtils.clamp(mainCamera.zoom, 0.8f, 2f);
        mainCamera.update();
        uiCamera.update();
    }

    private void setupCamera() {
        mainCamera.setToOrtho(false, worldWidth, worldHeight);
        uiCamera.setToOrtho(false, worldWidth, worldHeight);
        mainCamera.position.set(worldWidth / 2, worldHeight / 2, 0);
        uiCamera.position.set(worldWidth / 2, worldHeight / 2, 0);
        uiCamera.update();
        mainCamera.update();
    }

    private void drawUI(){
        // UI-Elemente zeichnen
        spriteBatch.setProjectionMatrix(uiCamera.combined);
        spriteBatch.begin();
        UI.DrawAll(spriteBatch);
        spriteBatch.end();

        //drawUiHitbox();
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

    private void mainDraw() {
        updateVisibility();

        spriteBatch.setProjectionMatrix(mainCamera.combined);
        spriteBatch.begin();

        if (inputManager.debug) {
            debugBlock.zeichne(spriteBatch);
        }
        WorldGen.DrawWorld(spriteBatch);
        Entity.Player.zeichne(spriteBatch);

        spriteBatch.end();
    }

    private void drawMainHitbox(){
        // Welt-Hitboxen zeichnen
        shapeRenderer.setProjectionMatrix(mainCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        for (Block block : WorldGen.worldBlocks) {
            if (block.getHitbox() != null) {
                shapeRenderer.polygon(block.getHitbox().getTransformedVertices());
            }
        }
        shapeRenderer.end();
    }

    private void drawEntityHitbox(){
        // Welt-Hitboxen zeichnen
        shapeRenderer.setProjectionMatrix(mainCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);

        for (Entity entity : Entity.EntityList) {
            if (entity.getHitbox() != null) {
                shapeRenderer.polygon(entity.getHitbox().getTransformedVertices());
            }
        }
        shapeRenderer.end();
    }

    private void updateVisibility() {
        float camLeft = mainCamera.position.x - (mainCamera.viewportWidth * mainCamera.zoom) / 2;
        float camRight = mainCamera.position.x + (mainCamera.viewportWidth * mainCamera.zoom) / 2;
        float camBottom = mainCamera.position.y - (mainCamera.viewportHeight * mainCamera.zoom) / 2;
        float camTop = mainCamera.position.y + (mainCamera.viewportHeight * mainCamera.zoom) / 2;

        for (Block block : WorldGen.worldBlocks) {
            if (block.getHitbox() != null) {
                float[] vertices = block.getHitbox().getTransformedVertices();
                boolean isVisible = false;

                // Prüfen, ob irgendein Punkt der Hitbox im Kamerabereich liegt
                for (int i = 0; i < vertices.length; i += 2) {
                    float x = vertices[i];
                    float y = vertices[i + 1];
                    if (x > camLeft && x < camRight && y > camBottom && y < camTop) {
                        isVisible = true;
                        break;
                    }
                }

                block.visible = isVisible;
            }
        }
    }

    private void drawPerformanceStats() {
        spriteBatch.begin();

        // Zeit seit dem letzten Frame
        float frameTime = Gdx.graphics.getDeltaTime() * 1000;
        frameTimeSum += frameTime;
        frameCount++;

        // Durchschnittliche Frame-Zeit
        float avgFrameTime = frameTimeSum / frameCount;

        // CPU- und GPU-Last (geschätzt)
        int fps = Gdx.graphics.getFramesPerSecond();
        float cpuLoad = 1000f / avgFrameTime * (1f / fps) * 100f;
        float gpuLoad = 100f - cpuLoad;

        // Text zeichnen
        String stats = String.format("FPS: %d | Avg Frame Time: %.2f ms | CPU Load: %.2f%% | GPU Load: %.2f%%", fps, avgFrameTime, cpuLoad, gpuLoad);
        Fonts.ultraSmallFont.draw(spriteBatch, stats, 10, Gdx.graphics.getHeight() - 10);

        spriteBatch.end();
    }

    // TITLE: ----------------------------------------------------


    // TITLE: ---------------------- OTHER METHODS ----------------------
    @Override public void resize(int width, int height) {
        mainViewport.update(width, height);
        uiViewport.update(width, height);  // <-- Das hinzufügen
        mainCamera.position.set(worldWidth / 2, worldHeight / 2, 0);
        mainCamera.update();
        uiCamera.position.set(worldWidth / 2, worldHeight / 2, 0);
        uiCamera.update();
    }
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        Textures.dispose();
        Fonts.dispose();
        WorldGen.disposeAll();
    }

    // TITLE: ---------------------- GESTURE HANDLING ----------------------
    @Override public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
                         Vector2 pointer1, Vector2 pointer2) {
        float initialDistance = initialPointer1.dst(initialPointer2);
        float currentDistance = pointer1.dst(pointer2);
        float zoomFactor = initialDistance / currentDistance;
        float zoomSpeed = 0.05f;
        mainCamera.zoom += (zoomFactor - 1) * zoomSpeed;
        mainCamera.zoom = Math.max(0.5f, Math.min(2f, mainCamera.zoom));
        return true;
    }
    public boolean touchDragged(float x, float y, int pointer) {
        // Kamera für die Welt
        Vector3 worldTouch = mainCamera.unproject(new Vector3(x, y, 0));
        float dx = lastTouch.x - worldTouch.x;
        float dy = lastTouch.y - worldTouch.y;
        mainCamera.position.add(dx * mainCamera.zoom, -dy * mainCamera.zoom, 0);
        mainCamera.update();

        // Kamera für die UI
        Vector3 uiTouch = uiCamera.unproject(new Vector3(x, y, 0));
        float uiDx = lastTouch.x - uiTouch.x;
        float uiDy = lastTouch.y - uiTouch.y;
        uiCamera.position.add(uiDx, -uiDy, 0);
        uiCamera.update();

        lastTouch.set(worldTouch.x, worldTouch.y);  // Update für nächste Bewegung
        return true;
    }
    @Override public boolean longPress(float x, float y) {
        lastTouch.set(x, y);
        return true;
    }
    @Override public boolean pan(float x, float y, float deltaX, float deltaY) {
        mainCamera.position.add(-deltaX * mainCamera.zoom, deltaY * mainCamera.zoom, 0);
        mainCamera.update();
        return true;
    }
    @Override public void pinchStop() {}
    @Override public boolean touchDown(float x, float y, int pointer, int button) { return false; }
    @Override public boolean tap(float x, float y, int count, int button) { return false; }
    @Override public boolean fling(float velocityX, float velocityY, int button) { return false; }
    @Override public boolean panStop(float x, float y, int pointer, int button) { return false; }
    @Override public boolean zoom(float initialDistance, float distance) { return false; }
}
