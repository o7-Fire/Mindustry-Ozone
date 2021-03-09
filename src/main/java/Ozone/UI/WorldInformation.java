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

package Ozone.UI;

import Atom.Time.Time;
import Atom.Utility.Pool;
import arc.scene.ui.Label;
import arc.struct.ObjectMap;
import arc.util.Log;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.gen.Groups;
import mindustry.world.Build;
import mindustry.world.Tile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class WorldInformation extends ScrollableDialog {
	
	private Label label = new Label("World calculation began...");
	
	public WorldInformation() {
		super("World Information");
		
	}
	
	public void setup() {
		label = new Label("World calculation began...");
		label.visible = false;
		ad("Word Name", Vars.state.map.name());
		ad("Players Count", Groups.player.size());
		ad("Drawc Count", Groups.draw.size());
		ad("Build Count", Groups.build.size());
		ad("Unit Count", Groups.unit.size());
		ad("[orange]Fire[white] Count", Groups.fire.size());
		ad("Bullet Count", Groups.bullet.size());
		ad("World height", Vars.world.height());
		ad("World width", Vars.world.width());
		ad("World square", Vars.world.width() * Vars.world.height());
		table.add(label).growX();
		table.row();
		Pool.submit(() -> {
			Time te = new Time();
			Log.debug("World calculation began");
			@NotNull ITransaction st = Sentry.startTransaction(Vars.world.width() + "x" + Vars.world.height(), "world-calculation");
			try {
				TreeMap<String, Integer> mainCount = new TreeMap<>();
				long totalOre = 0;
				int total = Vars.world.height() * Vars.world.width();
				int height = Vars.world.height();
				AtomicLong buildableTile = new AtomicLong();
				label.visible = true;
				label.setText("World calculation began...");
				ArrayList<Future<ObjectMap<String, Integer>>> futures = new ArrayList<>();
				for (int i = 0; i < height; i++) {
					int finalI = i;
					int a = i * height;
					label.setText("World indexing: " + a + "/" + total);
					futures.add(Pool.submit(() -> {//hail concurrency
						ObjectMap<String, Integer> count = new ObjectMap<>();
						//Overcautious people be like
						try {
							for (int j = 0; j < Vars.world.width(); j++) {
								try {
									Tile t = Vars.world.tileWorld(finalI, j);
									if (t == null) continue;
									if (t.staticDarkness() != 0) continue;
									try {
										count.put(t.floor().toString(), count.get(t.floor().toString(), 0) + 1);
									} catch (Throwable ignored) {
									}
									try {
										count.put(t.overlay().toString(), count.get(t.overlay().toString(), 0) + 1);
									}catch (Throwable ignored) {}
									try {
										count.put(t.block().toString(), count.get(t.block().getDisplayName(t), 0) + 1);
									}catch (Throwable ignored) {}
									try {
										if (t.build != null)
											count.put(t.build.getDisplayName(), count.get(t.build.getDisplayName(), 0) + 1);
									}catch (Throwable ignored) {}
									if (Build.validPlace(Blocks.copperWall, Vars.player.team(), t.x, t.y, 0))
										buildableTile.getAndIncrement();
								} catch (Throwable ignored) {
								}
							}
						} catch (Throwable ignored) {
						}
						return count;
					}));
				}

				long futc = futures.size();
				ArrayList<Future<?>> fut = new ArrayList<>(futures);
				for (Future<ObjectMap<String, Integer>> f : futures) {
					try {
						for (ObjectMap.Entry<String, Integer> s : f.get().entries()) {
							if (mainCount.get(s.key) == null)
								mainCount.put(s.key, 0);
							mainCount.put(s.key, mainCount.get(s.key) + s.value);
						}
						fut.remove(f);
						Pool.submit(() -> {
							String s = "";
							try {
								s = ((((float) (futc - fut.size()) / futc) * 100)) + "%";
								s = (futc - fut.size()) + "/" + futc;
								label.setText("World calculating: " + s);
							} catch (Throwable t) {
								s = (futc - fut.size()) + "/" + futc;
								label.setText("World calculating: " + s);
							}
						});
						
					}catch (Throwable ignored) { }
				}
				for (Map.Entry<String, Integer> s : mainCount.entrySet())
					if (s.getKey().startsWith("ore-")) totalOre += s.getValue();
				
				ad("The following statistic are measured in tile (1x1)", "");
				ad("Total Ores", totalOre);
				ad("Buildable Tiles", buildableTile);
				ad(mainCount);
				Log.debug("World calculation finished in @", te.elapsedS());
				if (te.elapsed().convert(TimeUnit.MILLISECONDS).getSrc() > 3000)
					Vars.ui.showInfo("World calculation finished: " + te.elapsed().convert(TimeUnit.MILLISECONDS).toString());
			}catch (Throwable i) {
				if (st != null) {
					st.setStatus(SpanStatus.INTERNAL_ERROR);
					st.setThrowable(i);
				}
				throw new RuntimeException(i);
			}finally {
				if (st != null) st.finish();
				label.visible = false;
			}
		});
		
	}
	
	@Override
	protected void init() {
		if (!Vars.state.isGame()) return;
		super.init();
	}
}
