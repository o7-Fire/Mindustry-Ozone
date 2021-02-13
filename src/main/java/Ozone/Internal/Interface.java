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


import Atom.Struct.Filter;
import Atom.Utility.Pool;
import Atom.Utility.Random;
import Ozone.Settings.BaseSettings;
import arc.Core;
import arc.Events;
import arc.math.Interp;
import arc.math.Mathf;
import arc.scene.actions.Actions;
import arc.scene.style.Drawable;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Time;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.world.Tile;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Future;

import static Ozone.Patch.Translation.getRandomHexColor;
import static mindustry.Vars.player;

public class Interface {
	public static final ObjectMap<String, String> bundle = new ObjectMap<>();
	private static long lastToast = 0;
	
	public static Tile getMouseTile() {
		return Vars.world.tileWorld(Vars.player.mouseX, Vars.player.mouseY);
	}
	
	public static void showToast(String text) {
		showToast(Icon.ok, text, 1000);
	}
	
	public static void showToast(String text, long duration) {
		showToast(Icon.ok, text, duration);
	}
	
	public static void showToast(Drawable icon, String text, long duration) {
		if (!Vars.state.isMenu()) {
			scheduleToast(() -> {
				Sounds.message.play();
				Table table = new Table(Tex.button);
				table.update(() -> {
					if (Vars.state.isMenu() || !Vars.ui.hudfrag.shown) {
						table.remove();
					}
					
				});
				table.margin(12.0F);
				table.image(icon).pad(3.0F);
				((Label) table.add(text).wrap().width(280.0F).get()).setAlignment(1, 1);
				table.pack();
				Table container = Core.scene.table();
				container.top().add(table);
				container.setTranslation(0.0F, table.getPrefHeight());
				container.actions(Actions.translateBy(0.0F, -table.getPrefHeight(), 1.0F, Interp.fade), Actions.delay(2.5F), Actions.run(() -> {
					container.actions(Actions.translateBy(0.0F, table.getPrefHeight(), 1.0F, Interp.fade), Actions.remove());
				}));
			}, duration);
		}
	}
	
	private static void scheduleToast(Runnable run, long duration) {
		long since = Time.timeSinceMillis(lastToast);
		if (since > duration) {
			lastToast = Time.millis();
			run.run();
		}else {
			Time.runTask((float) (duration - since) / 1000.0F * 60.0F, run);
			lastToast += duration;
		}
		
	}
	
	public static void showInfo(String s) {
		if (Vars.ui == null) Events.on(EventType.ClientLoadEvent.class, se -> Vars.ui.showInfo(s));
		else Vars.ui.showInfo(s);
	}
	
	//on load event show this stupid warning
	public static void warningUI(String title, String description) {
		if (Vars.ui == null)
			Events.on(EventType.ClientLoadEvent.class, s -> Vars.ui.showErrorMessage(title + "\n" + description));
		else Vars.ui.showErrorMessage(title + "\n" + description);
	}
	
	public static String getBundle(String key) {
		if (bundle.containsKey(key)) return bundle.get(key);
		if (Core.bundle.getOrNull(key) != null) return Core.bundle.get(key);
		try {
			
			Class s = Interface.class.getClassLoader().loadClass(key);
			registerWords(s.getName(), s.getSimpleName());
			return bundle.get(key);
		}catch (Throwable ignored) {}
		return Core.bundle.get(key);
	}
	
	public static void dropItem() {
		Call.dropItem(Mathf.random(120f));
	}
	
	public static Player searchPlayer(String s) {
		Player target = null;
		if (s == null) return null;
		try {//try ID search
			int id = Integer.parseInt(s);
			target = Groups.player.find(f -> f.id == id);
		}catch (NumberFormatException ignored) {}
		if (target == null)// if still not found
			target = Groups.player.find(f -> f.name().equals(s) || f.name.startsWith(s));
		return target;
	}
	
	public static boolean depositItem(Building tile) {
		if (tile == null || !tile.isValid() || tile.items == null || !tile.interactable(player.team()) || player.unit().item() == null)
			return false;
		int amount = Math.min(1, tile.getMaximumAccepted(player.unit().item()));
		if (amount > 0) {
			int accepted = tile.acceptStack(player.unit().item(), Vars.player.unit().stack.amount, player.unit());
			Call.transferItemTo(player.unit(), player.unit().item(), accepted, player.unit().x, player.unit().y, tile);
			return true;
		}
		return false;
	}
	
	public static boolean withdrawItem(Building tile, Item item){
		if(tile == null || !tile.isValid() || tile.items == null || !tile.items.has(item) || !tile.interactable(player.team())) return false;
		int amount = Math.min(1, player.unit().maxAccepted(item));
		if(amount > 0){
			Call.requestItem(player, tile, item, amount);
			return true;
		}
		return false;
	}
	
	public synchronized static void registerWords(String key, String value) {
		if (BaseSettings.colorPatch) value = getRandomHexColor() + value + "[white]";
		
		bundle.put(key, value);
	}
	
	public synchronized static void registerWords(String key) {
		registerWords(key, key);
	}
	
	public static Future<ArrayList<Tile>> getGroupTiles(Tile main, Filter<Tile> filter) {
		if (!Vars.state.getState().equals(GameState.State.playing)) return null;
		return Pool.submit(() -> {
			ArrayList<Tile> mains = new ArrayList<>(), mainsBackup = new ArrayList<>();
			ArrayList<Tile> group = new ArrayList<>();
			mains.add(main);
			group.add(main);
			while (!mains.isEmpty()) {
				for (Tile t : mains) {
					for (int i = 0; i < 4; i++) {
						Tile h = t.nearby(i);
						if (h == null) continue;
						if (group.contains(h)) continue;
						if (filter.accept(h)) {
							group.add(h);
							mainsBackup.add(h);
						}
					}
				}
				mains.clear();
				mains.addAll(mainsBackup);
				mainsBackup.clear();
			}
			return group;
		});
	}
	
	public static Future<Building> getBuild(Filter<Building> buildFilter) {
		return Pool.submit(() -> Random.getRandom(Objects.requireNonNull(getBuilds(buildFilter)).get()));
	}
	
	public static Future<ArrayList<Building>> getBuilds(Filter<Building> buildingFilter) {
		if (!Vars.state.getState().equals(GameState.State.playing)) return null;
		return Pool.submit(() -> {
			ArrayList<Building> list = new ArrayList<>();
			for (Building t : Groups.build) {
				if (!buildingFilter.accept(t)) continue;
				list.add(t);
			}
			return list;
		});
	}
	public static Future<ArrayList<Tile>> getTiles(Filter<Tile> filter) {
		if (!Vars.state.getState().equals(GameState.State.playing)) return null;
		return Pool.submit(() -> {
			ArrayList<Tile> list = new ArrayList<>();
			for (Tile t : Vars.world.tiles) {
				if (!filter.accept(t)) continue;
				list.add(t);
			}
			return list;
		});
	}
	
	public static Future<Tile> getTile(Filter<Tile> filter) {
		if (!Vars.state.getState().equals(GameState.State.playing)) return null;
		return Pool.submit(() -> Random.getRandom(Objects.requireNonNull(getTiles(filter)).get()));
	}
}





