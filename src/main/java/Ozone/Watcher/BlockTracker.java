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

package Ozone.Watcher;

import Atom.Reflect.FieldTool;
import arc.Core;
import arc.Events;
import arc.input.KeyCode;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.world.Tile;

public class BlockTracker {
	private static volatile boolean init;
	private static Tile target = null;

	public static void init(){
		if (init)return;
		init = true;
		Events.run(EventType.Trigger.update, BlockTracker::update);
	}

	private static void update(){
		if(Settings.Core.blockDebug)
		if(Vars.state.isPlaying()){
			if(Core.input.keyDown(KeyCode.controlLeft))
				if (Core.input.keyDown(KeyCode.mouseLeft))
					target = Vars.world.tileWorld(Vars.player.mouseX, Vars.player.mouseY);
				
			if (target != null)
				if (target.build != null)
					Vars.ui.hudfrag.setHudText(FieldTool.getFieldDetails(target.build).replace("\n", "[white]\n"));
				else
					Vars.ui.hudfrag.setHudText(FieldTool.getFieldDetails(target).replace("\n", "[white]\n"));
			
		}
	}
}
