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

package Ozone.Desktop.Patch;

import Atom.Struct.Stream;
import Atom.Utility.Utility;
import Ozone.Commands.Commands;
import Ozone.Desktop.Bootstrap.SharedBootstrap;
import Ozone.Internal.Module;
import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.world.Tile;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Ozone.Commands.Commands.*;
import static Ozone.Settings.BaseSettings.debugMode;

public class CommandsDesktop implements Module {
	private static int i = 0;
	
	public void init() {
		register("javac", new Command(CommandsDesktop::javac));
		if (SharedBootstrap.debug) register("debug", new Command(CommandsDesktop::debug, Icon.pause));
		register("info-pos", new Command(CommandsDesktop::infoPos, Icon.move));
	}
	
	@Override
	public List<Class<? extends Module>> dependOnModule() {
		return Arrays.asList(Commands.class);
	}
	
	public static void infoPos() {
		tellUser("Mouse x,y: " + Vars.player.mouseX + ", " + Vars.player.mouseY);
		Tile mouseTile = Vars.world.tileWorld(Vars.player.mouseX, Vars.player.mouseY);
		if (mouseTile != null)
			if (mouseTile.build != null) tellUser("MouseTile: Class: " + mouseTile.build.getClass().getName());
		Ozone.Commands.Commands.infoPos();
	}
	
	public static void debug() {
		if (!debugMode) {
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
	
	
	public static void javac(ArrayList<String> arg) {
		
		
		String code = Utility.joiner(arg.toArray(new String[0]), " ");
		Thread th = new Thread(() -> {
			try {
				Atom.Runtime.Compiler.runLine(code, Stream.getReader(s -> Log.infoTag("javac", s)));
			}catch (FileNotFoundException fail) {
				tellUser("Failed to compile or IOException Problem");
				fail.printStackTrace();
				Log.errTag("Compiler", fail.toString());
			}catch (Throwable t) {
				t.printStackTrace();
				Log.errTag("Compiler", t.toString());
				tellUser(t.toString());
			}
		});
		th.setDaemon(true);
		th.start();
		
	}
	
	
}
