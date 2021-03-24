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


import Atom.Reflect.Reflect;
import Atom.Struct.Filter;
import Atom.Utility.Pool;
import Atom.Utility.Random;
import Ozone.Manifest;
import Ozone.Settings.BaseSettings;
import Shared.SharedBoot;
import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.scene.actions.Actions;
import arc.scene.event.Touchable;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.net.ValidateException;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Sorter;
import mindustry.world.blocks.sandbox.ItemSource;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Future;

import static Ozone.Patch.Translation.getRandomHexColor;
import static mindustry.Vars.player;
import static mindustry.Vars.ui;

public class Interface extends AbstractModule {
	public static final ObjectMap<String, String> bundle = new ObjectMap<>();
	public static Map<Integer, ArrayList<Building>> buildingCache = Collections.synchronizedMap(new WeakHashMap<>());
	private static long lastToast = 0;
	
	public static void openLink(String url) {
		Vars.ui.showConfirm("Open URL", "Are you sure want to open\n \"" + url + "\"", () -> {
			Pool.submit(() -> {
				if (!Core.app.openURI(url)) {
					ui.showErrorMessage("@linkfail");
					Core.app.setClipboardText(url);
				}
			});
		});
	}
	
	
	public static void showInput(String key, String title, String about, Cons<String> s) {
		Vars.ui.showTextInput(title, about, 1000, Core.settings.getString(key, ""), se -> {
			Core.settings.put(key, se);
			s.get(se);
		});
	}
	
	public static void showInput(String about, Cons<String> s) {
		showInput(Reflect.getCallerClassStackTrace().toString(), about, s);
		
	}
	
	public static void showInput(String key, String about, Cons<String> s) {
		showInput(key, about, about, s);
	}
	
	public static Drawable getDrawableSized(String path, Drawable def) {
		Drawable d = getDrawable(path, def);
		d.setBottomHeight(def.getBottomHeight());
		d.setLeftWidth(def.getLeftWidth());
		d.setRightWidth(def.getRightWidth());
		d.setTopHeight(def.getTopHeight());
		d.setMinHeight(def.getMinHeight());
		d.setMinWidth(def.getMinWidth());
		return d;
	}
	
	public static Drawable getDrawable(String path, Drawable def) {
		try {
			RepoCached repoCached = Manifest.getModule(RepoCached.class);
			Pixmap p = repoCached.getPixmap(path);
			if (p == null) return def;
			return new TextureRegionDrawable(new TextureRegion(new Texture(p)));
		}catch (Throwable t) {
			Log.err(t.toString());
		}
		return def;
	}
	
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
		if (Vars.ui == null) {
			Events.on(EventType.ClientLoadEvent.class, se -> showToast(icon, text, duration));
			return;
		}
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
				table.add(text).wrap().width(280.0F).get().setAlignment(1, 1);
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
			if (SharedBoot.hardDebug) registerWords(s.getName(), s.getCanonicalName());
			else registerWords(s.getName(), s.getSimpleName());
			return bundle.get(key);
		}catch (Throwable ignored) {}
		if (!key.contains(".")) {
			registerWords(key, key);
			return bundle.get(key);
		}
		return Core.bundle.get(key);
	}
	
	public static void dropItem() {
		try {
			Call.dropItem(Mathf.random(120f));
		}catch (ValidateException ignored) {}
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
			try {
				Call.transferItemTo(player.unit(), player.unit().item(), accepted, player.unit().x, player.unit().y, tile);
			}catch (ValidateException e) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	public static boolean withdrawItem(Building tile, Item item) {
		if (tile == null || !tile.isValid() || tile.items == null || !tile.items.has(item) || !tile.interactable(player.team()))
			return false;
		int amount = Math.min(1, player.unit().maxAccepted(item));
		if (amount > 0) {
			try {
				Call.requestItem(player, tile, item, amount);
			}catch (ValidateException e) {
				return false;
			}
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
	
	@SafeVarargs
	public static Future<ArrayList<Building>> getBuildingBlock(Team team, Class<? extends Block>... list) {
		return getBuildingBlock(team, false, list);
	}
	
	@SafeVarargs
	public static ArrayList<Building> getBuildingBlockSync(Team team, Class<? extends Block>... list) {
		return getBuildingBlockSync(team, true, list);
	}
	
	@SafeVarargs
	public static @NotNull ArrayList<Building> getBuildingBlockSync(Team team, boolean cache, Class<? extends Block>... list) {
		ArrayList<Building> arr = new ArrayList<>();
		try {
			int hash = Arrays.hashCode(list);
			if (cache && buildingCache.containsKey(hash)) return buildingCache.get(hash);
			ArrayList<Tile> t = Interface.getTiles(f -> {
				if (f == null) return false;
				if (!f.interactable(team)) return false;
				if (f.build == null) return false;
				for (Class<? extends Block> l : list) {
					if (l.isInstance(f.build.block)) return true;
				}
				return false;
			}).get();
			for (Tile te : t)
				arr.add(te.build);
			if (!arr.isEmpty()) buildingCache.put(hash, arr);
			return arr;
		}catch (Throwable ignored) {}
		return arr;
	}
	
	@SafeVarargs
	public static Future<ArrayList<Building>> getBuildingBlock(Team team, boolean cache, Class<? extends Block>... list) {
		if (!Vars.state.getState().equals(GameState.State.playing)) return null;
		return Pool.submit(() -> getBuildingBlockSync(team, cache, list));
	}
	
	public static Future<Building> getRandomSorterLikeShit() {
		return Interface.getBuild(build -> {
			if (build == null) return false;
			return build.interactable(Vars.player.team()) && (build.block() instanceof Sorter || build.block() instanceof ItemSource);
		});
	}
	
	public static ArrayList<Building> getBuildsSync(Filter<Building> buildingFilter) {
		ArrayList<Building> list = new ArrayList<>();
		for (Building t : Groups.build) {
			if (!buildingFilter.accept(t)) continue;
			list.add(t);
		}
		return list;
	}
	
	public static Future<ArrayList<Building>> getBuilds(Filter<Building> buildingFilter) {
		if (!Vars.state.getState().equals(GameState.State.playing)) return null;
		return Pool.submit(() -> getBuildsSync(buildingFilter));
	}
	
	public static ArrayList<Tile> getTilesSync(Filter<Tile> filter) {
		ArrayList<Tile> list = new ArrayList<>();
		for (Tile t : Vars.world.tiles) {
			if (!filter.accept(t)) continue;
			list.add(t);
		}
		return list;
	}
	
	public static Future<ArrayList<Tile>> getTiles(Filter<Tile> filter) {
		if (!Vars.state.getState().equals(GameState.State.playing)) return null;
		return Pool.submit(() -> getTilesSync(filter));
	}
	
	public static Future<Tile> getTile(Filter<Tile> filter) {
		if (!Vars.state.getState().equals(GameState.State.playing)) return null;
		return Pool.submit(() -> Random.getRandom(Objects.requireNonNull(getTiles(filter)).get()));
	}
	
	public static void copy(Object s) {
		try {
			Core.app.setClipboardText(String.valueOf(s));
			toast("Copied");
		}catch (Throwable t) {
			toast(t.getMessage());
		}
	}
	
	public static void toast(String text) {
		if (Vars.ui == null) {
			Events.on(EventType.ClientLoadEvent.class, se -> toast(text));
			return;
		}
		Table table = new Table();
		table.touchable = Touchable.disabled;
		table.setFillParent(true);
		table.actions(Actions.fadeOut(4.0F, Interp.fade), Actions.remove());
		table.bottom().add(text).style(Styles.outlineLabel).padBottom(80);
		Core.scene.add(table);
	}
	
	public void reset() {
		buildingCache.clear();
	}
}





