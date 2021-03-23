/*******************************************************************************
 * Copyright 2021 Itzbenz
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
 ******************************************************************************/

/* o7 Inc 2021 Copyright

  Licensed under the o7 Inc License, Version 1.0.1, ("the License");
  You may use this file but only with the License. You may obtain a
  copy of the License at
  
  https://github.com/o7-Fire/Mindustry-Ozone/Licenses
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the license for the specific language governing permissions and
  limitations under the License.
*/

package Ozone.Commands;

import Atom.Utility.Meth;
import Atom.Utility.Random;
import Ozone.Commands.Task.Move;
import Ozone.Internal.AbstractModule;
import Ozone.Patch.Hack;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ai.Astar;
import mindustry.ai.Pathfinder;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Legsc;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.gen.WaterMovec;
import mindustry.world.Tile;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Pathfinding extends AbstractModule {
	//TODO don't be stupid
	public static ArrayList<PathfindingOverlay> render = new ArrayList<>();
	
	static void draw() {
		float x = 0, y = 0;
		
		
		for (PathfindingOverlay h : render) {
			boolean b = false;
			
			for (Tile t : h.tiles) {
				
				x = t.drawx() * 1.2f;
				y = t.drawy() * 1.2f;
				Lines.stroke(h.thick, b ? h.color : h.second);
				Lines.line(x, y, t.drawx(), t.drawy());
				b = !b;
			}
		}
	}
	
	public void init() {
		Events.run(EventType.Trigger.draw, Pathfinding::draw);
	}
	
	public static Seq<Tile> pathfind(Tile src, Tile trg) {
		return Astar.pathfind(src, trg, Pathfinding::isSafe, Pathfinding::passable);
	}
	
	public static Seq<Tile> pathfind(Tile target) {
		return pathfind(Vars.player.tileOn(), target);
	}
	
	public static boolean passable(Tile t) {
		Unit unit = Vars.player.unit();
		if (t == null) return false;
		return unit.canPass(t.x, t.y);
	}
	
	public static int pathTile(Tile t, Unit unit) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Team team = unit.team;
		int tile = Hack.pathTile(t);
		int cost;
		int type = 0;
		if (unit instanceof WaterMovec) type = Pathfinder.costNaval;
		else if (unit instanceof Legsc) type = Pathfinder.costLegs;
		else if (!unit.isFlying()) type = Pathfinder.costGround;
		else if (unit.isFlying()) return 0;
		cost = Hack.pathCost(team, tile, type);
		return cost;
	}
	
	public static boolean withinPlayerTolerance(Position target) {
		return withinPlayerTolerance(target, Vars.player);
	}
	
	public static boolean withinPlayerTolerance(Position target, Player vars) {
		float tolerance = vars.unit().isFlying() ? Move.airTolerance : Move.landTolerance;
		return Pathfinding.distanceTo(vars, target) < tolerance;
	}
	
	public static float isSafe(Tile t) {
		return isSafe(t, Vars.player.tileOn());
	}
	
	public static float isSafe(Tile tile, Tile current) {
		if (tile == null) return 10f;//no fuck given
		
		double def = Meth.positive(distanceTo(current, tile)) / 10;
		try {
			def = def + pathTile(tile, Vars.player.unit());
		}catch (Throwable g) {
			Log.debug("Failed to get pathTile for: " + tile.toString() + "\n" + g.toString());
		}
		return (float) def;
	}
	
	public static double distanceTo(Position source, Position target) {
		double dx = source.getX() - target.getX();
		double dy = source.getY() - target.getY();
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public static class PathfindingOverlay {
		Seq<Tile> tiles;
		Color color, second;
		float thick;
		
		public PathfindingOverlay(Seq<Tile> tiles) {
			this.tiles = tiles;
			this.color = Color.valueOf(Random.getRandomHexColor());
			second = Color.valueOf(Random.getRandomHexColor());
			thick = Mathf.random(3f);
		}
	}
	
}
