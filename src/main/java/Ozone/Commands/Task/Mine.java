package Ozone.Commands.Task;


import mindustry.Vars;
import mindustry.gen.Minerc;
import mindustry.world.Block;
import mindustry.world.blocks.environment.OreBlock;

//TODO do this
public class Mine extends Task {
    public Block ore;

    public Mine(Block block) {
        setTick(20);
        if (!(block instanceof OreBlock)) {
            ore = null;
            return;
        }
    }

    public Mine() {

    }

    @Override
    public boolean isCompleted() {
        if (ore == null) return true;
        if (!(Vars.player.unit() instanceof Minerc)) return true;
        return false;
    }

    @Override
    public void update() {
        if (!(Vars.player.unit() instanceof Minerc)) return;
        if (tick()) return;
    }
}
