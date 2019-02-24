package com.stefanolupo.ndngame.libgdx.components;

import com.badlogic.ashley.core.Component;
import com.stefanolupo.ndngame.names.blocks.BlockName;

/**
 * Contains the blocks remote name object and extra non game engine fields
 */
public class BlockComponent implements Component {

    private BlockName blockName;
    private boolean isRemote = false;
    private int health;

    public BlockName getBlockName() {
        return blockName;
    }

    public void setBlockName(BlockName blockName) {
        this.blockName = blockName;
    }

    public boolean isRemote() {
        return isRemote;
    }

    public void setRemote(boolean remote) {
        isRemote = remote;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
