package Ozone.Commands.Task;

import Ozone.Commands.BotInterface;
import Ozone.Commands.Pathfinding;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.ai.Astar;
import mindustry.content.Blocks;
import mindustry.world.Tile;

import static Ozone.Commands.BotInterface.setMov;
import static Ozone.Commands.Pathfinding.distanceTo;

//TODO relocate these method
public class Move extends Task {
    private final Vec2 destPos, destTilePos;
    private final float airTolerance = 1.2f, landTolerance = 0.04f;
    private Tile destTile = null;
    private Seq<Tile> pathfindingCache = new Seq<>();
    private Vec2 vec = new Vec2();
    private boolean alreadyOverlay = false;
    public Move(float x, float y) {
        this(new Vec2(x, y));
    }

    public Move(Vec2 dest) {
        destPos = new Vec2(dest.x * 8, dest.y * 8);
        destTilePos = dest;
        destTile = Vars.world.tile(Math.round(dest.x), Math.round(dest.y));
        setTick(10);
        if (!Vars.player.unit().isFlying()) {
              pathfindingCache = Astar.pathfind(Vars.player.tileOn(), destTile, Pathfinding::isSafe, Pathfinding::passable);
        }
    }

    @Override
    public void taskCompleted() {
        if(Vars.net.active())
            Vars.player.reset();
        for(Tile t : pathfindingCache)
            t.clearOverlay();

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
        if(!tick())
        if (!Vars.player.unit().isFlying()) {
            if (pathfindingCache.isEmpty()) return;
            if(!alreadyOverlay)
            for (Tile t : pathfindingCache) {
                if(t.x  + t.y == destTile.x+destTile.y)
                    alreadyOverlay = true;
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
        }
        setMov(destTile);
    }

    public float getCurrentDistance() {
        return (float) distanceTo(BotInterface.getCurrentTilePos(), destTilePos);
    }



}
