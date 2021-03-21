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

package Ozone.Desktop.Patch;

import Atom.Struct.Stream;
import Atom.Utility.Pool;
import Atom.Utility.Utility;
import Ozone.Commands.Commands;
import Ozone.Internal.Module;
import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.world.Tile;

import java.util.Arrays;
import java.util.List;

import static Ozone.Commands.Commands.*;

public class CommandsDesktop implements Module {

	
	public void init() {
		register("javac", new Command(CommandsDesktop::javac));
		
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
	
	
	public static void javac(List<String> arg) {
		
		
		String code = Utility.joiner(arg, " ");
		if (code.isEmpty()) {
			tellUser("no input");
			return;
		}
		if (!code.endsWith(";")) {
			tellUser("should end with semicolon ;");
			return;
		}
		
		Pool.daemon(() -> {
			try {
				Atom.Runtime.Compiler.runLine(code, Stream.getReader(Commands::tellUser));
			}catch (Throwable t) {
				t.printStackTrace();
				Log.errTag("Compiler", t.toString());
				tellUser(t.getMessage());
			}
		}).start();
		
	}
	
	
}
