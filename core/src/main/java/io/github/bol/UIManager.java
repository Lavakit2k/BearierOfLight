package io.github.bol;
import com.badlogic.gdx.math.Vector3;

public class UIManager {
    private UI optionsButton;
    private UI menuTextBox;

    public void startUI(Vector3 v){
        optionsButton = UI.UIList.get(1);
        menuTextBox = UI.UIList.get(2);
        optionsButton.on();

        optionsButton.updateUI(new Vector3(v.x + MainScreen.worldWidth / 2 - 128, v.y + MainScreen.worldHeight / 2 - 128, 0));
        menuTextBox.updateUI(new Vector3(menuTextBox.fromCenter(v).x, menuTextBox.fromCenter(v).y + 448, 0));

        setAllText();
    }
    public void OptionsButtonPressed(){
        menuTextBox.turn();
    }
    public void MenuTextBoxButtonPressed(){
        menuTextBox.off();
        WorldGen.LoadWorld("world3.xml");
    }


    private void setAllText(){
        menuTextBox.texts.get(0).setCenterPosition(new Vector3(0, 266, 0));
        menuTextBox.texts.get(1).setCenterPosition();
    }
}
