package Ozone.Commands.Task;

import arc.math.geom.Vec2;
import mindustry.Vars;
import mindustry.gen.Nulls;

public class Move extends Task {
    private final Vec2 destPost;

    public Move(int x, int y){
        this(new Vec2(x, y));
    }
    public Move(Vec2 dest){
        destPost = dest;
    }

    @Override
    public boolean isCompleted() {
        return distanceTo(getPlayerPos(), destPost) < 2;
    }

    public Vec2 getPlayerPos(){
        if(Vars.player.unit() != Nulls.unit){
            return new Vec2(Vars.player.unit().x, Vars.player.unit().y);
        }else {
            return new Vec2(Vars.player.x, Vars.player.y);
        }
    }
    public double distanceTo(Vec2 source, Vec2 target) {
        double dx = source.x - target.x;
        double dy = source.y - target.y;
        return Math.sqrt(dx*dx + dy*dy);
    }
}
