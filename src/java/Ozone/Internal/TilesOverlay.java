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

package Ozone.Internal;

import Atom.Utility.Random;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import mindustry.game.EventType;
import mindustry.world.Tile;

import java.util.*;

public class TilesOverlay extends AbstractModule {
	public final static ArrayList<TilesOverlay> overlays = new ArrayList<>();
	public static float size = 2f;
	public static float f = 2;
	public Color color = Color.valueOf(Random.getRandomHexColor());
	protected List<Tile> tiles = new ArrayList<>();
	
	public TilesOverlay() {
	
	}
	
	public TilesOverlay(Tile... tiles) {this(Arrays.asList(tiles));}
	
	public TilesOverlay(List<Tile> tiles) {
		this.tiles = tiles;
	}
	
	private static void draw() {
		if (overlays.isEmpty()) return;
		for (TilesOverlay ov : Collections.unmodifiableList(overlays)) {
			for (Iterator<Tile> t = ov.tiles.iterator(); ov.tiles.iterator().hasNext(); ) {
				if (!t.hasNext()) break;
				Tile v = t.next();
				Lines.stroke(f, ov.color);
				Lines.line(v.drawx(), v.drawy(), v.drawx() + f, v.drawy() + f);
			}
		}
	}
	
	@Override
	public void init() {
		Events.run(EventType.Trigger.draw, TilesOverlay::draw);
	}
	
	@Override
	public void reset() throws Throwable {
		overlays.clear();
	}
	
	public static void add(List<Tile> tiles) {
		overlays.add(new TilesOverlay(tiles));
	}
	
	public static void add(Tile... tiles) {
		add(Arrays.asList(tiles));
	}
}
