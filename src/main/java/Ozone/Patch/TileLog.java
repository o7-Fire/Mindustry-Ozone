package Ozone.Patch;

import mindustry.gen.Player;
import mindustry.world.Tile;

public class TileLog {
    public final Tile tile;

    public Player lastPlayerInteractWith;
    public Object lastConfiguration;

    public TileLog(Tile tile) {
        this.tile = tile;
    }

    public void update() {

    }

    public Tile tile() {
        return tile;
    }


}
