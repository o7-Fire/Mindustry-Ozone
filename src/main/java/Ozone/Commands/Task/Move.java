package Ozone.Commands.Task;

import Ozone.Commands.PlayerInterface;
import arc.math.geom.Vec2;
import arc.util.Log;
import mindustry.Vars;
import mindustry.world.Tile;

public class Move extends Task {
    private final Vec2 destPos, destTilePos;
    private final Tile destTile;
    private double lastDist = 20;

    public Move(float x, float y) {
        this(new Vec2(x, y));
    }

    public Move(Vec2 dest) {
        destPos = new Vec2(dest.x * 8, dest.y * 8);
        destTile = new Tile(Math.round(dest.x), Math.round(dest.y));
        destTilePos = dest;
        setTick(30);
        Vars.player.unit().moveAt(destPos);
    }

    @Override
    public void taskCompleted() {
        Vars.player.reset();
        super.taskCompleted();
    }

    @Override
    public boolean isCompleted() {
        return distanceTo(PlayerInterface.getCurrentPos(), destPos) < 3;
    }

    @Override
    public void update() {
        if (tick()) return;
        Vars.player.unit().moveAt(destTilePos);
        Log.infoTag("Ozone-AI", String.valueOf(lastDist));
        lastDist = distanceTo(PlayerInterface.getCurrentTilePos(), PlayerInterface.getCurrentTilePos(destPos));
    }

    public double distanceTo(Vec2 source, Vec2 target) {
        double dx = source.x - target.x;
        double dy = source.y - target.y;
        return Math.sqrt(dx*dx + dy*dy);
    }

}
