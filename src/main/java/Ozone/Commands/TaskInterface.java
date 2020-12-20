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

import Ozone.Commands.Task.Task;
import Settings.Core;
import arc.Events;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.Queue;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.world.Tile;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TaskInterface {
	public static final Queue<Task> taskQueue = new Queue<>();
	private static volatile boolean init = false;
	
	public static void init() {
		if (init) return;
		init = true;
		Events.run(EventType.Trigger.update, TaskInterface::update);
	}
	
	private static void update() {
		if (taskQueue.isEmpty()) return;
		if (!taskQueue.first().isCompleted()) taskQueue.first().update();
		else taskQueue.removeFirst().taskCompleted();
	}
	
	public static void addTask(Task task, Consumer<Object> onDone) {
		if (onDone != null) task.onTaskCompleted(onDone);
		taskQueue.addLast(task);
		Log.debug("Task: " + task.getName() + " has been added to queue : " + taskQueue.size);
	}
	
	public static void setMov(Position targetTile) {
		Vec2 vec = new Vec2();
		if (Vars.player.unit() == null) return;
		vec.trns(Vars.player.unit().angleTo(targetTile), Vars.player.unit().type().speed);
		if (Core.debugMode && !Vars.disableUI) {
			if (Vars.ui.scriptfrag.shown()) {
				Vars.ui.scriptfrag.addMessage("Ozone-AI DriveX: " + vec.x);
				Vars.ui.scriptfrag.addMessage("Ozone-AI DriveY: " + vec.y);
			}
		}
		Vars.player.unit().moveAt(vec);
	}
	
	
	public static ArrayList<Tile> getNearby(Tile tile, int rotation, int range) {
		ArrayList<Tile> tiles = new ArrayList<>();
		if (rotation == 0) {
			for (int i = 0; i < range; i++) {
				tiles.add(Vars.world.tile(tile.x + 1 + i, tile.y));
			}
		}else if (rotation == 1) {
			for (int i = 0; i < range; i++) {
				tiles.add(Vars.world.tile(tile.x, tile.y + 1 + i));
			}
		}else if (rotation == 2) {
			for (int i = 0; i < range; i++) {
				tiles.add(Vars.world.tile(tile.x - 1 - i, tile.y));
			}
		}else if (rotation == 3) {
			for (int i = 0; i < range; i++) {
				tiles.add(Vars.world.tile(tile.x, tile.y - 1 - i));
			}
		}
		return tiles;
	}
	
	public static Vec2 getCurrentPos() {
		return new Vec2(Vars.player.x, Vars.player.y);
	}
	
	public static Vec2 getCurrentPos(Tile t) {
		return new Vec2(t.x * 8, t.y * 8);
	}
	
	public static Vec2 getCurrentTilePos() {
		return new Vec2(Vars.player.tileX(), Vars.player.tileY());
	}
	
	public static Vec2 getCurrentTilePos(Vec2 ref) {
		return new Vec2(Math.round(ref.x / 8), Math.round(ref.y / 8));
	}
	
	public static boolean samePos(Position pos1, Position pos2, boolean tolerance) {
		if (tolerance)
			return Math.round(pos1.getX()) == Math.round(pos2.getX()) && Math.round(pos1.getY()) == Math.round(pos2.getY());
		else return pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY();
	}
	
	public static void reset() {
		taskQueue.clear();
	}
	
	
}
