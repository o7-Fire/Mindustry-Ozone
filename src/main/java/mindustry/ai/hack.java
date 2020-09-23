package mindustry.ai;

import mindustry.game.Team;

//hack mindustry.ai package restriction
public class hack {

    public static int pathCost(Team team, int tile, int type) {
        Pathfinder.PathCost c = Pathfinder.costTypes.get(type);
        return c.getCost(team, tile);
    }
}
