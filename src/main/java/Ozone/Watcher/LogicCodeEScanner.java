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

import Atom.Utility.Pool;
import Ozone.Internal.Interface;
import Ozone.Internal.Module;
import Ozone.Settings.BaseSettings;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.blocks.logic.LogicBlock;

public class LogicCodeEScanner implements Module {
	public static LogicCodeEScanner INSTANCE;
	protected Thread daemon = null;
	
	public static boolean enabled() {
		return BaseSettings.logicCodeScanner;
	}
	
	public static void displayScan() {
	
	
	}
	
	public static void logicScan() {
		for (Building b : Interface.getBuildingBlockSync(Vars.player.team(), false, LogicBlock.class)) {
			
			//if(b instanceof LogicBlock.LogicBuild)
			//((LogicBlock.LogicBuild) b).executor
		}
	}
	
	public static void fullScan() {
		displayScan();
		logicScan();
		
	}
	
	public static void isNSFWCode(String str) {
	
	}
	
	@Override
	public void onWorldLoad() throws Throwable {
		if (!enabled()) return;
		if (daemon != null) onWoldUnload();
		daemon = Pool.daemon(() -> {
			fullScan();
			daemon = null;
		});
		daemon.start();
	}
	
	@Override
	public void onWoldUnload() throws Throwable {
		if (daemon == null) return;
		daemon.interrupt();
		daemon = null;
	}
	
	
	@Override
	public void init() throws Throwable {
		INSTANCE = this;
	}
}
