/*
 * Copyright 2020 Itzbenz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package Ozone.Commands;

import Atom.Utility.Random;
import Ozone.Patch.Hack;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ai.Pathfinder;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Legsc;
import mindustry.gen.Unit;
import mindustry.gen.WaterMovec;
import mindustry.world.Tile;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Pathfinding {
    //TODO don't be stupid
    public static ArrayList<PathfindingOverlay> render = new ArrayList<>();
    private static boolean init = false;

    static void draw() {
        float x = 0, y = 0;
        boolean draw = true;


        for (PathfindingOverlay h : render) {
            boolean b = false;

            for (Tile t : h.tiles) {
                if (draw) {
                    x = t.drawx();
                    y = t.drawy();
                } else {
                    Lines.stroke(h.thick, b ? h.color : h.second);
                    Lines.line(x, y, t.drawx(), t.drawy());
                    b = !b;
                }
                draw = !draw;
            }
        }
    }

    public static void init() {
        if (init) return;
        init = true;
        Events.run(EventType.Trigger.draw, Pathfinding::draw);
    }

    public static class PathfindingOverlay {
        Seq<Tile> tiles;
        Color color, second;
        float thick;

        public PathfindingOverlay(Seq<Tile> tiles) {
            this.tiles = tiles;
            this.color = Color.valueOf(Random.getRandomHexColor());
            second = Color.valueOf(Random.getRandomHexColor());
            thick = Mathf.random(5f);
        }
    }

    public static boolean passable(Tile t) {
        Unit unit = Vars.player.unit();
        if (t == null) return false;
        if (unit instanceof WaterMovec) {
            if (!t.block().isFloor()) return false;
            if (!t.block().asFloor().isLiquid) return false;
        } else if (unit instanceof Legsc) {
            if (!t.passable()) return false;
        } else if (!unit.isFlying()) {
            if (!t.passable()) return false;
            if (t.build != null) return false;
        }
        return true;
    }

    public static int pathTile(int tile, Unit unit) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Team team = unit.team;
        int cost;
        int type = 0;
        if (unit instanceof WaterMovec)
            type = Pathfinder.costWater;
        else if (unit instanceof Legsc)
            type = Pathfinder.costLegs;
        else if (!unit.isFlying())
            type = Pathfinder.costGround;
        cost = Hack.pathCost(team, tile, type);
        return cost;
    }

    public static float isSafe(Tile tile) {
        try {
            if (tile == null) return 0f;//no fuck given
            return pathTile(tile.pos(), Vars.player.unit());
        }catch (Throwable g) {
            Log.debug("Failed to get pathTile for: " + tile.toString() + "\n" + g.toString());
            float danger = 0f;
            for (int i = 0; i < 4; i++) {
                for (Tile t : TaskInterface.getNearby(tile, i, 2)) {
                    float fDanger = 0f;
                    if (t == null) continue;
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
