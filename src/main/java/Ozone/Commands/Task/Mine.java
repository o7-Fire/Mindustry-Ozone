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

package Ozone.Commands.Task;


import mindustry.Vars;
import mindustry.gen.Minerc;
import mindustry.world.Block;
import mindustry.world.blocks.environment.OreBlock;

//TODO do this
public class Mine extends Task {
	public Block ore;

	public Mine(OreBlock block) {
		setTick(20);

	}

	public Mine() {

	}

	@Override
	public boolean isCompleted() {
		if (ore == null) return true;
		if (!(Vars.player.unit() instanceof Minerc)) return true;
		return false;
	}

	@Override
	public void update() {
		if (!(Vars.player.unit() instanceof Minerc)) return;
		if (tick()) return;
	}
}
