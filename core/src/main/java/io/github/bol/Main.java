package io.github.bol;

import com.badlogic.gdx.Game;

public class Main extends Game {


    private static GameState currentState = GameState.BUILDING;
    public static GameState getCurrentState() {
        return currentState;
    }
    public static void setCurrentState(GameState newState) {
        currentState = newState;
    }



    @Override
    public void create() { setScreen(new MainScreen()); }
    @Override
    public void render() {
        super.render();
    }
    @Override
    public void dispose() {
        getScreen().dispose();
    }
}

