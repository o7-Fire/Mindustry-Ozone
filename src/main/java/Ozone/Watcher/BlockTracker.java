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

package Ozone.Watcher;

import Atom.Reflect.FieldTool;
import Ozone.Commands.Pathfinding;
import Ozone.Internal.Interface;
import Ozone.Internal.Module;
import Ozone.Settings.BaseSettings;
import arc.Core;
import arc.Events;
import arc.input.KeyCode;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.world.Tile;
import mindustry.world.blocks.logic.LogicBlock;
import mindustry.world.blocks.logic.LogicDisplay;

public class BlockTracker implements Module {
	private static Tile target = null;
	
	public void init() {
		Events.run(EventType.Trigger.update, BlockTracker::update);
	}
	
	private static void update() {
		if (BaseSettings.blockDebug && Vars.state.isPlaying()) {
			if (Core.input.keyDown(KeyCode.controlLeft))
				if (Core.input.keyDown(KeyCode.mouseLeft)) target = Interface.getMouseTile();
			
			if (target != null) {
				StringBuilder sb = new StringBuilder();
				if (target.build != null) {
					sb.append(FieldTool.getFieldDetails(target.build).replace("\n", "[white]\n"));
					if (target.build instanceof LogicDisplay.LogicDisplayBuild)
						sb.append("GPUCommandsHash=").append(((LogicDisplay.LogicDisplayBuild) target.build).commands.toString().hashCode()).append("[white]\n");
					if (target.build instanceof LogicBlock.LogicBuild)
						sb.append("CodeHash=").append(((LogicBlock.LogicBuild) target.build).code.hashCode()).append("[white]\n");
				}else sb.append(FieldTool.getFieldDetails(target).replace("\n", "[white]\n"));
				sb.append("SafetyIndex:").append(Pathfinding.isSafe(target)).append("[white]\n");
				Vars.ui.hudfrag.setHudText(sb.toString());
			}
			
		}
	}
}
