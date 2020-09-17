package Ozone.Commands.Task;

import Atom.Meth;
import Ozone.Commands.BotInterface;
import Ozone.Patch.DesktopInput;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ai.Astar;
import mindustry.world.Tile;

public class Move extends Task {
    private final Vec2 destPos, destTilePos;
    private final int airTolerance = 2, landTolerance = 1;
    private Tile destTile = null;
    private Seq<Tile> pathfindingCache = new Seq<>();

    public Move(float x, float y) {
        this(new Vec2(x, y));
    }

    public Move(Vec2 dest) {
        destPos = new Vec2(dest.x * 8, dest.y * 8);
        destTilePos = dest;
        setTick(5);
        if (!Vars.player.unit().isFlying()) {
            destTile = new Tile(Math.round(dest.x), Math.round(dest.y));
            pathfindingCache = Astar.pathfind(Vars.player.tileOn(), destTile, this::isSafe, Tile::passable);
        }
    }

    @Override
    public void taskCompleted() {
        Vars.player.reset();
        super.taskCompleted();
    }

    @Override
    public boolean isCompleted() {
        if (Vars.player.unit().isFlying())
            return distanceTo(BotInterface.getCurrentPos(), destPos) < airTolerance;
        else
            return distanceTo(BotInterface.getCurrentPos(), destPos) < landTolerance;
    }

    @Override
    public void update() {
        if (tick()) return;
        if (Vars.player.unit().isFlying()) {
            int xx = Math.round(destTilePos.x - BotInterface.getCurrentTilePos().x);
            int yy = Math.round(destTilePos.y - BotInterface.getCurrentTilePos().y);
            Log.debug("Ozone-AI @", "X: " + xx);
            Log.debug("Ozone-AI @", "Y: " + yy);
            if (Meth.positive(yy) < airTolerance) yy = 0;
            else if (yy < 0) yy = -1;
            else if (yy > airTolerance) yy = 1;

            if (Meth.positive(xx) < airTolerance) xx = 0;
            else if (xx < 0) xx = -1;
            else if (xx > airTolerance) xx = 1;
            setMov(new Vec2(xx, yy));
            double lastDist = getCurrentDistance();
            Log.debug("Ozone-AI @", "Dist: " + lastDist);
            Log.debug("Ozone-AI @", "DriveX: " + xx);
            Log.debug("Ozone-AI @", "DriveY: " + yy);
        } else {
            if (pathfindingCache.isEmpty()) return;
            if (destTile != null)
                if (distanceTo(BotInterface.getCurrentTilePos(), new Vec2(destTile.x, destTile.y)) <= landTolerance)
                    pathfindingCache.remove(0);
            destTile = pathfindingCache.get(0);
            int xx = Math.round(destTile.x - BotInterface.getCurrentTilePos().x);
            int yy = Math.round(destTile.y - BotInterface.getCurrentTilePos().y);
            Log.debug("Ozone-AI @", "X: " + xx);
            Log.debug("Ozone-AI @", "Y: " + yy);
            if (Meth.positive(yy) < landTolerance) yy = 0;
            else if (yy < 0) yy = -1;
            else if (yy > landTolerance) yy = 1;

            if (Meth.positive(xx) < landTolerance) xx = 0;
            else if (xx < 0) xx = -1;
            else if (xx > landTolerance) xx = 1;
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
        return distanceTo(BotInterface.getCurrentTilePos(), destTilePos);
    }

    public float isSafe(Tile tile) {
        return 0f;
    }

    public double distanceTo(Vec2 source, Vec2 target) {
        double dx = source.x - target.x;
        double dy = source.y - target.y;
        return Math.sqrt(dx * dx + dy * dy);
    }


}
