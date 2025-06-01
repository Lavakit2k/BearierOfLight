package io.github.bol;

public class InputManager {

    public boolean debug;
    private UIManager manager;

    public void setManager(UIManager m){
        manager = m;
    }

    public void releaseInput() {
        WorldGen.highLightBlock();
    }

    public void longPress() {
        pressButton();
    }

    public void shortPress(float delta) {
        WorldGen.deHighLightBlock();
        pressButton();
    }

    private void pressButton() {
        // Die Position wird relativ zum UI-Koordinatensystem berechnet
        for (UI ui : UI.UIList) {
            if (ui.visible && ui.isHit(MainScreen.uiInputPosition)) {
                // Überprüfe die UI-ID
                switch(ui.ID){
                    case 1:
                    manager.OptionsButtonPressed();
                    break;

                    case 2:
                        manager.MenuTextBoxButtonPressed();
                        break;
                    default:
                        break;
                }
            }
        }
    }

}
