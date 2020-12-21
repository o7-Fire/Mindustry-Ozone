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

import Atom.Time.Countdown;
import Atom.Utility.Pool;
import Atom.Utility.Random;
import Atom.Utility.Utility;
import Ozone.Commands.Task.DestructBlock;
import Ozone.Commands.Task.Move;
import Ozone.Commands.Task.Task;
import Ozone.Event.Internal;
import Ozone.Interface;
import Ozone.Manifest;
import Settings.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.Colors;
import arc.scene.style.TextureRegionDrawable;
import arc.struct.OrderedMap;
import arc.struct.Queue;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ai.Astar;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Sorter;
import mindustry.world.blocks.sandbox.ItemSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Commands {
	
	public static final Queue<Task> commandsQueue = new Queue<>();
	public static Map<String, Command> commandsList = new TreeMap<>();
	
	private volatile static boolean falseVote = false;
	
	private static boolean init = false;
	
	private volatile static boolean chatting = false;
	public static void init() {
		if (init) return;
		init = true;
		Events.run(EventType.Trigger.update, () -> {
			if (commandsQueue.isEmpty()) return;
			commandsQueue.first().update();
			if (commandsQueue.first().isCompleted()) commandsQueue.removeFirst();
		});
		//register("message-log", new Command(Commands::messageLog, Icon.rotate));
		//register("shuffle-configurable", new Command(Commands::shuffleConfigurable, Icon.rotate));
		register("task-move", new Command(Commands::taskMove));
		register("info-pathfinding", new Command(Commands::infoPathfinding));
		register("chat-repeater", new Command(Commands::chatRepeater), "Chat Spammer by nexity");
		register("task-deconstruct", new Command(Commands::taskDeconstruct));
		register("send-colorize", new Command(Commands::sendColorize));
		
		//Commands with icon support no-argument-commands
		register("random-kick", new Command(Commands::randomKick, Icon.hammer));
		register("info-unit", new Command(Commands::infoUnit, Icon.units));
		register("force-exit", new Command(Commands::forceExit, Icon.exit));
		register("task-clear", new Command(Commands::taskClear, Icon.cancel));
		register("shuffle-sorter", new Command(Commands::shuffleSorter, Icon.rotate));
		register("clear-pathfinding-overlay", new Command(Commands::clearPathfindingOverlay, Icon.cancel));
		register("hud-frag", new Command(Commands::hudFrag, Icon.info), "HUD Test");
		register("hud-frag-toast", new Command(Commands::hudFragToast, Icon.info), "HUD Toast Test");
		register("info-pos", new Command(Commands::infoPos, Icon.move));
		register("help", new Command(Commands::help, Icon.infoCircle));
		register("chaos-kick", new Command(Commands::chaosKick, Icon.hammer));
		Events.fire(Internal.Init.CommandsRegister);
		Log.infoTag("Ozone", "Commands Center Initialized");
		Log.infoTag("Ozone", commandsList.size() + " commands loaded");
	}
	
	public static void hudFragToast(ArrayList<String> arg){
		String s = "["+Random.getRandomHexColor()+"]Test "+Random.getString(16);
		if(!arg.isEmpty()) s = Utility.joiner(arg, " ");
		Vars.ui.hudfrag.showToast(s);
	}
	
	public static void hudFrag(ArrayList<String> arg){
		String s = "["+Random.getRandomHexColor()+"]Test "+Random.getString(16);
		if(!arg.isEmpty()) s = Utility.joiner(arg, " ");
		Vars.ui.hudfrag.setHudText(s);
	}
	public static void clearPathfindingOverlay(ArrayList<String> arg) {
		tellUser("Clearing: " + Pathfinding.render.size() + " overlay");
		Pathfinding.render.clear();
	}
	
	public static void shuffleConfigurable() {
	
	}
	
	public static void messageLog() {
	
	}
	
	public static void register(String name, Command command) {
		register(name, command, null);
	}
	
	public static void register(String name, Command command, String description) {
		if (description != null) Interface.registerWords("ozone.commands." + name, description);
		if (commandsList.get(name) == null) commandsList.put(name, command);
		else commandsList.replace(name, command);
	}
	
	public static void shuffleSorter() {
		
		commandsQueue.add(new Completable() {
			Future<Building> f = null;
			{
				f = Interface.getBuild(build -> {
					if (build == null) return false;
					return build.interactable(Vars.player.team()) && (build.block() instanceof Sorter || build.block() instanceof ItemSource);
				});
				if (f == null) {
					tellUser("wtf ? shuffle sorter future is null");
					completed = true;
				}
			}
		
			@Override
			public void update() {
				if(f == null)return;
				if (!f.isDone()) return;
				if (completed) return;
				completed = true;
				try {
					Building t = f.get();
					if (t == null || t.tile == null) {
						tellUser("block can't be find");
						return;
					}
					Item target = Random.getRandom(Vars.content.items().toArray(Item.class));
					t.tile().build.configure(target);
				}catch (InterruptedException | ExecutionException e) {
					Log.errTag("Ozone-Executor", "Failed to get tile:\n" + e.toString());
				}
			}
		});
		
	}
	
	public static void taskClear() {
		TaskInterface.reset();
		tellUser("Task cleared");
	}
	
	public static void sendColorize(ArrayList<String> s) {
		if (s.isEmpty()) {
			tellUser("Empty ? gabe itch");
			return;
		}
		String text = Utility.joiner(Utility.getArray(s), " ");
		StringBuilder sb = new StringBuilder();
		if (text.length() * 10 > Vars.maxTextLength) {
			OrderedMap<String, Color> map = Colors.getColors();
			ArrayList<String> colors = new ArrayList<>();
			for (String mp : map.keys()) {
				colors.add('[' + mp + ']');
			}
			String[] colorss = new String[colors.size()];
			colorss = colors.toArray(colorss);
			for (char c : text.toCharArray()) {
				if (c != ' ') {
					sb.append(Random.getRandom(colorss)).append(c);
				}else sb.append(c);
			}
		}else {
			for (char c : text.toCharArray()) {
				if (c != ' ') sb.append("[").append(Random.getRandomHexColor()).append("]").append(c);
				else sb.append(c);
			}
		}
		Call.sendChatMessage(sb.toString());
	}
	
	public static void taskDeconstruct(ArrayList<String> s) {
		if (s.size() < 2) {
			tellUser("Not enough arguments");
			tellUser("Usage: task-deconstruct x(type: coordinate) y(type: coordinate) half(type: boolean, optional default: false)");
			return;
		}
		try {
			int x = Integer.parseInt(s.get(0));
			int y = Integer.parseInt(s.get(1));
			if (Vars.world.tile(x, y) == null) {
				tellUser("Non existent tiles");
				return;
			}
			boolean half = false;
			// i don't trust user
			if (s.size() == 3) {
				half = true;
			}
			long start = System.currentTimeMillis();
			TaskInterface.addTask(new DestructBlock(x, y, half), a -> tellUser("Completed in " + Countdown.result(start, TimeUnit.SECONDS)));
		}catch (NumberFormatException f) {
			tellUser("Failed to parse integer, are you sure that argument was integer ?");
			Vars.ui.showException(f);
		}
	}
	
	public static void forceExit(ArrayList<String> ar) {
		throw new RuntimeException("Force Exit: " + Utility.joiner(Utility.getArray(ar), ", "));
	}
	
	public static void infoUnit() {
		tellUser(Vars.player.unit().getClass().getCanonicalName());
	}
	
	public static String getTranslation(String name) {
		return arc.Core.bundle.get("ozone.commands." + name);
	}
	
	public static boolean call(String message) {
		if (!message.startsWith(Core.commandsPrefix)) return false;
		message = message.substring(Core.commandsPrefix.length());
		if (message.isEmpty()) return false;
		ArrayList<String> mesArg = new ArrayList<>(Arrays.asList(message.split(" ")));
		if (!commandsList.containsKey(mesArg.get(0).toLowerCase())) {
			tellUser("Commands not found");
			help();
			return true;
		}
		Command comm = commandsList.get(mesArg.get(0).toLowerCase());
		ArrayList<String> args;
		if (mesArg.size() > 1) {
			message = message.substring(mesArg.get(0).length() + 1);
			args = new ArrayList<>(Arrays.asList(message.split(" ")));
		}else {
			args = new ArrayList<>();
		}
		comm.method.accept(args);
		return true;
	}
	
	public static void randomKick() {
		ArrayList<Player> players = new ArrayList<>();
		for (Player p : Groups.player)
			players.add(p);
		Player p = Random.getRandom(players.toArray(new Player[0]));
		if (p == null) return;//we get em next time
		Call.sendChatMessage("/votekick " + p.name);
	}
	
	public static void infoPathfinding(ArrayList<String> s) {
		if (s.size() < 4) {
			tellUser("Not enough arguments");
			tellUser("usage: " + "info-pathfinding x(type: source-coordinate) y(type: source-coordinate) x(type: target-coordinate) y(type: target-coordinate) block(type: Blocks, optional)");
			return;
		}
		try {
			String block = "";
			int xS = Integer.parseInt(s.get(0));
			int yS = Integer.parseInt(s.get(1));
			if (Vars.world.tile(xS, yS) == null) {
				tellUser("Non existent source tiles");
				return;
			}
			int xT = Integer.parseInt(s.get(2));
			int yT = Integer.parseInt(s.get(3));
			if (s.size() == 5) block = s.get(4);
			Block pathfindingBlock = null;
			if (!block.isEmpty()) {
				pathfindingBlock = Vars.content.block(block);
				if (pathfindingBlock == null) tellUser("Nonexistent block, using default block: magmarock/dirtwall");
			}
			
			
			Tile target = Vars.world.tile(xT, yT);
			Tile source = Vars.world.tile(xS, yS);
			if (target == null) {
				tellUser("Non existent target tiles");
				return;
			}
			if (source == null) {
				tellUser("Non existent source tiles");
				return;
			}
			Pool.submit(() -> {
				Seq<Tile> tiles = new Seq<>();
				try {
					tiles.addAll(Astar.pathfind(source, target, h -> 0, Tile::passable));
					Pathfinding.render.add(new Pathfinding.PathfindingOverlay(tiles));
				}catch (Throwable e) {
					tellUser("Pathfinding failed");
					tellUser(e.toString());
				}
				return tiles;
			});
			
		}catch (NumberFormatException f) {
			tellUser("Failed to parse integer, are you sure that argument was integer ?");
			Vars.ui.showException(f);
		}
	}
	
	public static void toggleUI() {
		Manifest.menu.hide();
	}
	
	public static void infoPos() {
		tellUser("Player x,y: " + Vars.player.x + ", " + Vars.player.y);
		tellUser("TileOn x,y: " + Vars.player.tileX() + ", " + Vars.player.tileY());
		if (Vars.player.tileOn() != null && Vars.player.tileOn().build != null)
			tellUser("TileOn: Class: " + Vars.player.tileOn().build.getClass().getName());
	}
	
	
	public static void taskMove(ArrayList<String> s) {
		if (s.size() < 2) {
			tellUser("Not enough arguments");
			tellUser("usage: " + "task-move x(coordinate) y(coordinate)");
			return;
		}
		try {
			int x = Integer.parseInt(s.get(0));
			int y = Integer.parseInt(s.get(1));
			if (Vars.world.tile(x, y) == null) {
				tellUser("Non existent tiles");
				return;
			}
			long start = System.currentTimeMillis();
			TaskInterface.addTask(new Move(x, y), a -> tellUser("Reached in " + Countdown.result(start, TimeUnit.SECONDS)));
			toggleUI();
		}catch (NumberFormatException f) {
			tellUser("Failed to parse integer, are you sure that argument was integer ?");
			Vars.ui.showException(f);
		}
		
	}
	
	public static void help() {
		ArrayList<String> as = new ArrayList<>();
		as.add("Prefix: \""+Core.commandsPrefix+"\"");
		as.add("Available Commands:");
		int target = 5;
		for (Map.Entry<String, Command> s : commandsList.entrySet()) {
			if(target < s.getKey().length())
				target = s.getKey().length() + 2;
		}
		for (Map.Entry<String, Command> s : commandsList.entrySet()) {
			StringBuilder local = new StringBuilder();
			local.append(s.getKey());
			while (local.length() < target)
				local.append(" ");
			local.append(":").append(s.getValue().description);
			as.add(local.toString());
		}
		for(String s : as)
			tellUser(s);
	}

	/**
	 * @author Nexity
	 * its obvious its my code
	 */
	public static void chaosKick() {
		falseVote = !falseVote;
		if (falseVote) {
			Thread s1 = new Thread(() -> {
				while (Vars.net.active() && falseVote) for (Player target : Groups.player) {
					if (!target.name.equals(Vars.player.name)) {
						Call.sendChatMessage("/votekick " + target.name);
						try {
							Thread.sleep(200);
						}catch (Throwable ignored) {
						}
					}
				}
			});
			s1.start();
			tellUser("kicking started");
		}else {
			tellUser("kicking ended");
		}
	}

	public static void chatRepeater(ArrayList<String> arg) {
		chatting = !chatting;
		if (chatting) {
			Thread s1 = new Thread(() -> {
				while (true) {
					if (chatting && Vars.net.active()) {
						Call.sendChatMessage(Utility.joiner(arg, " ") + Math.random());
						try {
							Thread.sleep(3100);
						} catch (Throwable ignored) {
						}
					}
				}
			});
			s1.start();
			tellUser("chatRepeater started");
		}else {
			tellUser("chatRepeater ended");
		}
	}
	
	
	public static void tellUser(String s) {
		if (Vars.ui.scriptfrag.shown()) Log.infoTag("Ozone", s);
		else Vars.ui.chatfrag.addMessage("[white][[[royal]Ozone[white]]: " + s, null);
	}
	
	public static class Command {
		public final Consumer<ArrayList<String>> method;
		public final TextureRegionDrawable icon;
		public String description;
		
		
		public Command(Consumer<ArrayList<String>> method) {
			this.method = method;
			icon = null;
		}
		
		public Command(Runnable r, TextureRegionDrawable icon){
			this.method = strings -> {r.run();};
			this.icon = icon;
		}
		public Command(Consumer<ArrayList<String>> r, TextureRegionDrawable icon){
			this.method = r;
			this.icon = icon;
		}
	}
}
