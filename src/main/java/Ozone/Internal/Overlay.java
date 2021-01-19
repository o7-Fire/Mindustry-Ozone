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
import arc.math.geom.Vec2;
import mindustry.game.EventType;

import java.util.ArrayList;

public class Overlay implements Module {
	public static ArrayList<Overlay> overlay = new ArrayList<>();
	private static float f = 2f;
	public Color color = Color.valueOf(Random.getRandomHexColor());
	protected ArrayList<Vec2> overlayCoordinate = new ArrayList<>();
	
	public Overlay() {}
	
	public Overlay(ArrayList<Vec2> vec2s) {
		overlayCoordinate.addAll(vec2s);
		
	}
	
	public void init() {
		Events.run(EventType.Trigger.draw, Overlay::draw);
	}
	
	private static void draw() {
		if (overlay.isEmpty()) return;
		for (Overlay ov : overlay) {
			for (Vec2 v : ov.overlayCoordinate) {
				
				Lines.stroke(f, ov.color);
				Lines.line(v.x, v.y, v.x + f, v.y + f);
			}
		}
	}
}
