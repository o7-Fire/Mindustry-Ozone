package Ozone.Commands.Task;

import Atom.Meth;
import Ozone.Commands.BotInterface;
import Ozone.Commands.Pathfinding;
import Ozone.Patch.DesktopInput;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ai.Astar;
import mindustry.content.Blocks;
import mindustry.gen.Call;
import mindustry.world.Tile;

import static Ozone.Commands.Pathfinding.distanceTo;

//TODO relocate these method
public class Move extends Task {
    private final Vec2 destPos, destTilePos;
    private final float airTolerance = 1.2f, landTolerance = 0.04f;
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
            pathfindingCache = Astar.pathfind(Vars.player.tileOn(), destTile, Pathfinding::isSafe, s -> {
                return  s != null&&s.passable() && s.floor() != Blocks.deepwater.asFloor() && s.build == null;
            });

        }
    }

    @Override
    public void taskCompleted() {
        Vars.player.reset();
        if (!pathfindingCache.isEmpty()) Call.sendChatMessage("/sync");
        setMov(new Vec2(0, 0));
        super.taskCompleted();
    }

    @Override
    public boolean isCompleted() {
        if (Vars.player.unit().isFlying())
            return distanceTo(BotInterface.getCurrentPos(), destPos) < airTolerance * 1.2f;
        else
            return distanceTo(BotInterface.getCurrentPos(), destPos) < landTolerance || pathfindingCache.isEmpty();
    }

    @Override
    public void update() {
        if (tick()) return;
        if (Vars.player.unit().isFlying()) {
            float xx = destTilePos.x - BotInterface.getCurrentTilePos().x;
            float yy = destTilePos.y - BotInterface.getCurrentTilePos().y;
            Log.debug("Ozone-AI @", "X: " + xx);
            Log.debug("Ozone-AI @", "Y: " + yy);
            if (Meth.positive(yy) < airTolerance) yy = 0;
            else if (yy < 0) yy = -1;
            else if (yy > airTolerance) yy = 1;

            if (Meth.positive(xx) < airTolerance) xx = 0;
            else if (xx < 0) xx = -1;
            else if (xx > airTolerance) xx = 1;
            setMov(new Vec2(xx, yy));
            float lastDist = getCurrentDistance();
            Log.debug("Ozone-AI @", "Dist: " + lastDist);
            Log.debug("Ozone-AI @", "DriveX: " + xx);
            Log.debug("Ozone-AI @", "DriveY: " + yy);
        } else {
            if (pathfindingCache.isEmpty()) return;
            for (Tile t : pathfindingCache) {
                if (t.block() == null)
                    tellUser("Null block: " + t.toString());
                else if (t.block().isFloor())
                    t.setOverlay(Blocks.magmarock);
                else if (t.block().isStatic())
                    t.setOverlay(Blocks.dirtWall);
            }
            if (destTile != null) {
                if (distanceTo(BotInterface.getCurrentTilePos(), new Vec2(destTile.x, destTile.y)) <= landTolerance) {
                    pathfindingCache.remove(0).clearOverlay();
                }
            }
            if (pathfindingCache.isEmpty()) return;
            destTile = pathfindingCache.get(0);
            destTile.setOverlay(Blocks.dirt);
            setMov(destTile);
            /*
            float xx = destTile.x - BotInterface.getCurrentTilePos().x;
            float yy = destTile.y - BotInterface.getCurrentTilePos().y;
            Log.debug("Ozone-AI @", "X: " + xx);
            Log.debug("Ozone-AI @", "Y: " + yy);
            if (Meth.positive(yy) < landTolerance) yy = 0;
            else if (yy < 0) yy = -1;
            else if (yy > landTolerance) yy = 1;

            if (Meth.positive(xx) < landTolerance) xx = 0;
            else if (xx < 0) xx = -1;
            else if (xx > landTolerance) xx = 1;
            float lastDist = getCurrentDistance();
            setMov(new Vec2(xx, yy));

            Log.debug("Ozone-AI @", "Dist: " + lastDist);
            Log.debug("Ozone-AI @", "DriveX: " + xx);
            Log.debug("Ozone-AI @", "DriveY: " + yy);

           */
        }
    }

    public void setMov(Tile targetTile){
        Vec2 vec = new Vec2();
        Vars.player.unit().moveAt(vec.trns(Vars.player.unit().angleTo(targetTile), Vars.player.unit().type().speed));
    }

    public void setMov(Vec2 mov) {
        if (Vars.control.input instanceof DesktopInput) ((DesktopInput) Vars.control.input).setMove(mov);
        else Log.infoTag("Ozone", "Can't control movement, DesktopInput not patched");
    }

    public float getCurrentDistance() {
        return (float) distanceTo(BotInterface.getCurrentTilePos(), destTilePos);
    }



}
