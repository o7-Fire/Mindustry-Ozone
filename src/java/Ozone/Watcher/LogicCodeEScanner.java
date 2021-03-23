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

import Atom.Reflect.UnThread;
import Atom.Utility.Digest;
import Atom.Utility.Pool;
import Ozone.Internal.AbstractModule;
import Ozone.Internal.Interface;
import Ozone.Internal.Repo;
import Ozone.Settings.BaseSettings;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.blocks.logic.LogicBlock;

import java.io.IOException;
import java.util.HashMap;

public class LogicCodeEScanner extends AbstractModule {
	public static LogicCodeEScanner INSTANCE;
	protected Thread daemon = null;
	public static HashMap<String, String> database = new HashMap<>();
	protected static volatile boolean fetched;
	
	public static void fullScan() {
		while (!fetched) UnThread.sleep(200);
		
		if (fetched) {
			for (Building b : Interface.getBuildingBlockSync(Vars.player.team(), false, LogicBlock.class)) {
				
				if (b instanceof LogicBlock.LogicBuild) {
					LogicBlock.LogicBuild lb = (LogicBlock.LogicBuild) b;
					String l = String.valueOf(Digest.longHash(lb.code));
					if (!database.containsKey(l)) continue;
					
					lb.updateCode("#" + database.get(l) + "\n" + "noop");
				}
			}
		}
		
		
	}
	
	public boolean enabled() {
		return BaseSettings.logicCodeScanner && fetched;
	}
	
	@Override
	public void loadAsync() {
		try {
			database = Repo.getRepo().readMap("src/LogicCodeDatabase.txt");
		}catch (IOException e) {
		
		}
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
