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

package Ozone.Patch;

import Ozone.Event.Internal;
import arc.Events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static Ozone.Interface.registerWords;

public class Translation {
	public static final ArrayList<String> normalSinglet = new ArrayList<>(Arrays.asList("Run"));
	public static final ArrayList<String> singlet1 = new ArrayList<>(Arrays.asList("String", "Integer", "Float", "Long", "Boolean", "Commands"));
	public static final HashMap<String, String> settings = new HashMap<>(), generalSettings = new HashMap<>();
	public static final HashMap<String, String> commands = new HashMap<>();
	public static final HashMap<String, String> keyBinds = new HashMap<>();
	
	public static void register() {
		settings.put("antiSpam", "Enable Anti-Spam");
		settings.put("debugMode", "Enable Debug Mode");
		settings.put("colorPatch", "Enable Colorized Text");
		settings.put("commandsPrefix", "Commands Prefix");
		settings.put("worldLog", "Log World Tiles [red][Heavy RAM Usage]");
		registerWords("ozone.menu", "Ozone Menu");
		registerWords("ozone.hud", "Ozone HUD");
		registerWords("ozone.javaEditor", "Java Executor");
		registerWords("ozone.javaEditor.reformat", "Reformat");
		registerWords("ozone.javaEditor.run", "Run");
		registerWords("ozone.commandsUI", "Commands GUI");
		commands.put("help", "help desk");
		commands.put("chaos-kick", "vote everyone everytime everywhere");
		commands.put("task-move", "move using current unit with pathfinding algorithm");
		commands.put("info-pos", "get current info pos");
		commands.put("info-pathfinding", "get Pathfinding overlay");
		commands.put("force-exit", "you want to crash ?");
		commands.put("task-deconstruct", "deconstruct your block with AI");
		commands.put("send-colorize", "send Colorized text");
		commands.put("info-unit", "get current unit info");
		commands.put("random-kick", "random kick someone");
		commands.put("clear-pathfinding-overlay", "Clear every pathfinding overlay");
		commands.put("shuffle-sorter", "shuffle every block that look like sorter, note: item source too");
		commands.put("javac", "run single line of code, like \nVars.player.unit().moveAt(new Vec2(100, 100));");
		commands.put("task-clear", "clear all bot task");
		commands.put("message-log", "see message log, recommend to export it instead");
		commands.put("shuffle-configurable", "shuffle every configurable block, literally almost everything");
		Events.fire(Internal.Init.TranslationRegister);
		for (Map.Entry<String, String> s : commands.entrySet()) {
			registerWords("ozone.commands." + s.getKey(), s.getValue());
		}
		for (Map.Entry<String, String> s : settings.entrySet()) {
			registerWords("setting.ozone." + s.getKey() + ".name", s.getValue());
		}
		for (Map.Entry<String, String> s : generalSettings.entrySet()) {
			registerWords("setting." + s.getKey() + ".name", s.getValue());
		}
		for (Map.Entry<String, String> s : keyBinds.entrySet()) {
			registerWords("section." + s.getKey() + ".name", s.getValue());
		}
		for (String s : singlet1) registerWords(s, "[" + s + "]");
		for (String s : normalSinglet) registerWords(s);
		
	}
	
	public static String add(String text) {
		String s = Thread.currentThread().getStackTrace()[2].getClassName() + text.toLowerCase().replaceAll(" ", ".");
		registerWords(s, text);
		return s;
	}
}
