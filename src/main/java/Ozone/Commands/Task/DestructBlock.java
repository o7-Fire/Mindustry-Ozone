package Ozone.Commands.Task;

import mindustry.Vars;
import mindustry.gen.Builderc;
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
        if (Vars.world.tile(x, y) == null) throw new NullPointerException("No tile on: " + x + ", " + y);
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
        if (!(Vars.player.unit() instanceof Builderc)) return;
        Tile t = Vars.world.tile(x, y);
        if (t == null) return;
        if (half && t.block() instanceof ConstructBlock) return;
        int idx = Vars.player.builder().plans().indexOf((req) -> req.breaking && req.x == x && req.y == y);
        if (idx != -1) return;
        Vars.player.builder().removeBuild(x, y, true);
    }
}
