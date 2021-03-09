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

/* o7 Inc 2021 Copyright

  Licensed under the o7 Inc License, Version 1.0.1, ("the License");
  You may use this file but only with the License. You may obtain a
  copy of the License at
  
  https://github.com/o7-Fire/Mindustry-Ozone/Licenses
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the license for the specific language governing permissions and
  limitations under the License.
*/

package Ozone.Commands.Task;

import mindustry.Vars;
import mindustry.gen.Player;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;

public class DestructBlock extends Task {
	protected final boolean half;
	protected final int x, y;
	protected Player player;
	
	public DestructBlock(int x, int y, boolean half) {
		this(x, y, half, Vars.player);
	}
	
	public DestructBlock(int x, int y, boolean half, Player vars) {
		this.half = half;
		this.player = vars;
		this.x = x;
		this.y = y;
		if (Vars.world.tile(x, y) == null) throw new NullPointerException("No tile on: " + x + ", " + y);
	}
	
	public DestructBlock(int x, int y) {
		this(x, y, false);
	}
	
	@Override
	public boolean isCompleted() {
		Tile t = Vars.world.tile(x, y);
		if (t == null) return true;
		if (half) return (t.block() instanceof ConstructBlock);
		return !Build.validBreak(player.team(), x, y);
		
	}
	
	@Override
	public void update() {
		if (Vars.player.unit() == null) return;
		Tile t = Vars.world.tile(x, y);
		if (t == null) return;
		if (half && t.block() instanceof ConstructBlock) return;
		int idx = player.unit().plans().indexOf((req) -> req.breaking && req.x == x && req.y == y);
		if (idx != -1) return;
		player.unit().removeBuild(x, y, true);
	}
}
