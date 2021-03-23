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
import Ozone.Commands.Pathfinding;
import Ozone.Commands.Task.Task;
import Ozone.Commands.TaskInterface;
import Ozone.Internal.AbstractModule;
import Ozone.Internal.Interface;
import Ozone.Internal.TilesOverlay;
import Ozone.UI.OzonePlaySettings;
import arc.Core;
import arc.Graphics;
import mindustry.Vars;
import mindustry.input.Binding;
import mindustry.input.DesktopInput;
import mindustry.world.Tile;

import java.util.ArrayList;
import java.util.Arrays;

//TODO figure out what todo
public class PlayTime extends AbstractModule {
	public static ArrayList<Tile> markedTiles = new ArrayList<>();
	public static boolean markTile;
	private static TilesOverlay tilesOverlay = new TilesOverlay(markedTiles);
	
	public static void markTiles() {
		markTile = !markTile;
		
		if (markTile) TaskInterface.addTask(new Task() {
			boolean valid = false;
			
			{
				name = "Marking Tiles";
				c();
				if (!TilesOverlay.overlays.contains(tilesOverlay)) TilesOverlay.overlays.add(tilesOverlay);
			}
			
			@Override
			public void onCompleted() {
				super.onCompleted();
				c();
			}
			
			@Override
			public boolean isCompleted() {
				return !markTile;
			}
			
			public void c() {
				if (Vars.control.input instanceof DesktopInput) {
					if (markTile && valid) {
						((DesktopInput) Vars.control.input).cursorType = Vars.ui.drillCursor;
					}else {
						((DesktopInput) Vars.control.input).cursorType = Graphics.Cursor.SystemCursor.arrow;
					}
				}
			}
			
			public boolean validTile(Tile tile) {
				if (tile == null) return false;
				return true;
			}
			
			@Override
			public void update() {
				
				Tile t = Interface.getMouseTile();
				if (valid = validTile(t)) {
					if (Core.input.keyDown(Binding.select)) {
						if (!markedTiles.contains(t)) markedTiles.add(t);
					}
					if (Core.input.keyDown(Binding.deselect)) {
						markedTiles.remove(t);
					}
				}
				if (OzonePlaySettings.tileMode.equals(OzonePlaySettings.MarkerTileMode.Pathfinding)) {
					if (markedTiles.size() >= 2) {
						Tile t1 = markedTiles.remove(0), t2 = markedTiles.remove(0);
						Pool.submit(() -> {
							try {
								if (Pathfinding.distanceTo(t1, t2) > 1000)
									tellUser("Calculating... " + t1.x + "," + t.y + " to " + t2.x + "," + t2.y);
								TilesOverlay.add(new ArrayList<>(Arrays.asList(Pathfinding.pathfind(t1, t2).toArray(Tile.class))));
							}catch (Throwable e) {
								tellUser("Pathfinding failed");
								tellUser(e.toString());
							}
						});
						
					}
				}
				c();
				
			}
		});
		
	}
	
	public void reset() {
		markedTiles.clear();
		if (!TilesOverlay.overlays.contains(tilesOverlay)) TilesOverlay.overlays.add(tilesOverlay);
	}
}
