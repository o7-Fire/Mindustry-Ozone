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

package Ozone.Internal;

import Atom.Utility.Random;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import mindustry.game.EventType;
import mindustry.world.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TilesOverlay implements Module {
	public static ArrayList<TilesOverlay> overlays = new ArrayList<>();
	public static float size = 2f;
	public static float f = 2;
	public Color color = Color.valueOf(Random.getRandomHexColor());
	protected ArrayList<Tile> tiles = new ArrayList<>();
	
	public TilesOverlay() {}
	
	public TilesOverlay(List<Tile> tiles) {
		this.tiles.addAll(tiles);
	}
	
	@Override
	public void init() {
		Events.run(EventType.Trigger.draw, TilesOverlay::draw);
	}
	
	private static void draw() {
		if (overlays.isEmpty()) return;
		for (TilesOverlay ov : overlays) {
			for (Tile v : ov.tiles) {
				Lines.stroke(f, ov.color);
				Lines.line(v.drawx(), v.drawy(), v.drawx() + f, v.drawy() + f);
			}
		}
	}
	
	public static void add(List<Tile> tiles) {
		overlays.add(new TilesOverlay(tiles));
	}
	
	public static void add(Tile... tiles) {
		add(Arrays.asList(tiles));
	}
}
