package Ozone.Commands;

import arc.math.geom.Position;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ai.Pathfinder;
import mindustry.game.Team;
import mindustry.gen.Legsc;
import mindustry.gen.Unit;
import mindustry.gen.WaterMovec;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

public class Pathfinding {


    public static boolean passable(Tile t) {
        if (t == null) return false;
        if (!t.passable()) return false;

        return true;
    }

    public static int pathTile(int tile, Unit unit) {
        Team team = unit.team;
        int cost = -100;
        int type = 0;
        if (unit instanceof WaterMovec)
            type = Pathfinder.costWater;
        else if (unit instanceof Legsc)
            type = Pathfinder.costLegs;
        cost = mindustry.ai.hack.pathCost(team, tile, type);
        return cost;
    }

    public static float isSafe(Tile tile) {
        try {
            int i = pathTile(tile.pos(), Vars.player.unit());
            if (i == -100) throw new RuntimeException();
            return i;
        } catch (Throwable ignored) {
            float danger = 0f;
            if (tile == null) return danger;
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
    }

    public static double distanceTo(Position source, Position target) {
        double dx = source.getX() - target.getX();
        double dy = source.getY() - target.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

}
