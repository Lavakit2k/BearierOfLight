package io.github.bol;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class Block extends Object {
    public static List<Block> BlockList = new ArrayList<>();
    public Block(Texture t, int id, Vector3 pos) {
        super(t, id, pos);
    }

    @Override
    public Block Clone() {
        return new Block(this.texture, this.ID, this.getPosition());
    }
    @Override
    protected void createHitbox(){
        super.createHexagonHitbox();
    }

    public static void InitListBlock(){
        BlockList.add(new Block(Textures.GrassBlock, 2, Vector3.Zero));
        BlockList.add(new Block(Textures.Dirt, 3, Vector3.Zero));
        BlockList.add(new Block(Textures.OakTrunk, 4, Vector3.Zero));
        BlockList.add(new Block(Textures.Leaves, 5, Vector3.Zero));
        BlockList.add(new Block(Textures.Glass, 6, Vector3.Zero));
    }
}
