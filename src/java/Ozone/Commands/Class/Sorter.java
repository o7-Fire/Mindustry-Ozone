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

package Ozone.Commands.Class;

import Atom.Utility.Random;
import Ozone.Commands.CommandsCenter;
import Ozone.Commands.Task.Completable;
import Ozone.Internal.Interface;
import arc.util.Log;
import com.beust.jcommander.Parameter;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.type.Item;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Sorter extends CommandsClass {
	@Parameter(names = "-shuffle", description = "shuffle sorter")
	public boolean shuffle = false;
	//@Parameter(names = "-all", description = "all sorter")
	//public boolean all = false;
	
	public Sorter() {
		icon = Icon.rotate;
		description = "Manage Sorter";
		supportNetwork = true;
	}
	
	@Override
	void run() throws Exception {
		if (shuffle) {
			addTask(new Completable() {
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
						shuffleSorterCall(f.get());
					}catch (IndexOutOfBoundsException gay) {
						CommandsCenter.tellUser("No item");
					}catch (InterruptedException | ExecutionException e) {
						Log.errTag("Ozone-Executor", "Failed to get tile:\n" + e.toString());
					}
				}
			});
		}
	}
	
	public void shuffleSorterCall(Building t) {
		if (t == null || t.tile == null) {
			tellUser("block can't be find");
			return;
		}
		Item target = Random.getRandom(Vars.content.items());
		t.tile.build.block.lastConfig = target;
		callable.tileConfig(null, t.tile.build, target);
	}
	
}