package io.github.bol;

public enum GameState {
    MENU,        // Das Spiel befindet sich im Hauptmenü
    PLAYING,     // Das Spiel läuft
    BUILDING,
    EDITING,
    PAUSED,      // Das Spiel ist pausiert
    GAME_OVER;   // Das Spiel ist vorbei


    public boolean isGameActive() {
        return this == PLAYING || this == PAUSED;
    }
}
