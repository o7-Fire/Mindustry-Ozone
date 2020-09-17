package Ozone.Commands.Task;

import Atom.Meth;
import Ozone.Commands.PlayerInterface;
import Ozone.Patch.DesktopInput;
import arc.math.geom.Vec2;
import arc.util.Log;
import mindustry.Vars;
import mindustry.world.Tile;

public class Move extends Task {
    private final Vec2 destPos, destTilePos;
    private final Tile destTile;
    private final int tolerance = 3;

    public Move(float x, float y) {
        this(new Vec2(x, y));
    }

    public Move(Vec2 dest) {
        destPos = new Vec2(dest.x * 8, dest.y * 8);
        destTile = new Tile(Math.round(dest.x), Math.round(dest.y));
        destTilePos = dest;
        setTick(5);

    }

    @Override
    public void taskCompleted() {
        Vars.player.reset();
        super.taskCompleted();
    }

    @Override
    public boolean isCompleted() {
        return distanceTo(PlayerInterface.getCurrentPos(), destPos) < tolerance;
    }

    @Override
    public void update() {
        if (tick()) return;
        if (Vars.player.unit().isFlying()) {
            int xx = Math.round(destTilePos.x - PlayerInterface.getCurrentTilePos().x);
            int yy = Math.round(destTilePos.y - PlayerInterface.getCurrentTilePos().y);
            Log.debug("Ozone-AI @", "X: " + xx);
            Log.debug("Ozone-AI @", "Y: " + yy);
            if (Meth.positive(yy) < tolerance) yy = 0;
            else if (yy < 0) yy = -1;
            else if (yy > tolerance) yy = 1;

            if (Meth.positive(xx) < tolerance) xx = 0;
            else if (xx < 0) xx = -1;
            else if (xx > tolerance) xx = 1;
            setMov(new Vec2(xx, yy));
            double lastDist = getCurrentDistance();
            Log.debug("Ozone-AI @", "Dist: " + lastDist);
            Log.debug("Ozone-AI @", "DriveX: " + xx);
            Log.debug("Ozone-AI @", "DriveY: " + yy);
        }
    }

    public void setMov(Vec2 mov) {
        if (Vars.control.input instanceof DesktopInput) ((DesktopInput) Vars.control.input).setMove(mov);
        else Log.infoTag("Ozone", "Can't control movement, DesktopInput not patched");
    }

    public double getCurrentDistance() {
        return distanceTo(PlayerInterface.getCurrentTilePos(), destTilePos);
    }

    public double distanceTo(Vec2 source, Vec2 target) {
        double dx = source.x - target.x;
        double dy = source.y - target.y;
        return Math.sqrt(dx * dx + dy * dy);
    }


}
