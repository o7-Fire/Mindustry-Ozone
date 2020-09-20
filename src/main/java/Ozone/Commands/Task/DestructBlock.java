package Ozone.Commands.Task;

import mindustry.Vars;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;

public class DestructBlock extends Task {
    private final boolean half;
    private final int x, y;

    public DestructBlock(int x, int y, boolean half) {
        this.half = half;
        this.x = x;
        this.y = y;
    }

    public DestructBlock(int x, int y) {
        this(x, y, false);
    }

    @Override
    public boolean isCompleted() {
        Tile t = Vars.world.tile(x, y);
        if (t == null) return true;
        if (half) return (t.block() instanceof ConstructBlock);
        return !Build.validBreak(Vars.player.team(), x, y);

    }

    @Override
    public void update() {
        Tile t = Vars.world.tile(x, y);
        if (t == null) return;
        if (half) {
            //its a ConstructBlock ? then we done
            if (Vars.world.tile(x, y).block() instanceof ConstructBlock) return;
            Vars.player.builder().removeBuild(x, y, true);
        }
    }
}
