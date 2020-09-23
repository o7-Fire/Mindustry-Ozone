package Ozone.Commands;

import arc.math.geom.Vec2;
import arc.util.Log;
import mindustry.Vars;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

public class Pathfinding {
    //its expensive to compute lmao
    //TODO optimize
    public static float isSafe(Tile tile) {
        float danger = 0f;
        if(tile==null)return danger;
        Floor floor = tile.floor();
        for (int i = 0; i < 4; i++) {
            for (Tile t : BotInterface.getNearby(tile, i, 2)) {
                float fDanger = 0f;
                //such a lie, it can be null but intellj refuse to
                if (tile == null) continue;
                if (!t.passable())
                    fDanger += 0.4f;//avoid unpassable, sometime its stuck
                if (t.floor().isLiquid)
                    fDanger += 0.3f;//avoid the liquid
                if (tile.build == null) continue;
                if (tile.team() != Vars.player.team())
                    fDanger += 3f;
                if (fDanger != 0f)
                    fDanger = fDanger / Vars.player.tileOn().dst(t);
                Log.debug(t.toString() + "\nIndex: " + danger);
                danger += fDanger;
            }
        }
        if (tile.floor().isLiquid)
            danger += 2f;//avoid the liquid
        return danger;
    }

    public static double distanceTo(Vec2 source, Vec2 target) {
        double dx = source.x - target.x;
        double dy = source.y - target.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

}
