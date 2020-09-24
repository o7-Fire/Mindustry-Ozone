package Ozone.Patch;

import mindustry.ai.Pathfinder;
import mindustry.game.Team;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//Dirty hack
public class Hack {

    public static int pathCost(Team team, int tile, int type) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object o = Pathfinder.costTypes.get(type);
        Method m = o.getClass().getMethod("getCost", Team.class, int.class);
        m.setAccessible(true);
        return (int) m.invoke(o, team, tile);
    }
}
