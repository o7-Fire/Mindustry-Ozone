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

package Ozone;

import Atom.Utility.Random;
import Ozone.Commands.Commands;
import Ozone.Commands.Pathfinding;
import Ozone.Commands.TaskInterface;
import Ozone.Event.EventExtended;
import Ozone.Event.Internal;
import Ozone.Patch.SettingsDialog;
import Ozone.Patch.Translation;
import Ozone.UI.CommandsListFrag;
import Ozone.UI.OzoneMenu;
import Settings.Core;
import arc.Events;
import arc.scene.ui.Dialog;
import arc.struct.ObjectMap;
import arc.util.Log;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.game.EventType;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;

import java.lang.reflect.Field;
import java.util.Map;

public class Main {
	private static boolean init = false;
	
	public static void init() {
		if (init) return;
		init = true;
		Log.infoTag("Ozone", "Hail o7");
		loadSettings();
		patch();
		Commands.init();
		patchTranslation();
		initUI();
		TaskInterface.init();
		initEvent();
		Pathfinding.init();
	}
	
	
	public static void loadContent() {
	
	}
	
	private static void patchLast() {
	
	}
	
	private static void initEvent() {
		Vars.loadLogger();
		Events.on(EventType.ClientLoadEvent.class, s -> {
			arc.Core.settings.getBoolOnce("ozoneEpilepsyWarning", () -> {
				Vars.ui.showCustomConfirm("[royal]Ozone[white]-[red]Warning", "A very small percentage of people may experience a seizure when exposed to certain visual images, " + "including flashing lights or patterns that may appear on certain UI element in the game.", "Accept", "Decline", () -> {
				}, () -> {
					Core.colorPatch = false;
					arc.Core.settings.put("ozone.colorPatch", false);
					arc.Core.settings.forceSave();
				});
			});
			// setOzoneLogger();
		});
		Events.run(EventExtended.Connect.Disconnected, () -> {
			TaskInterface.reset();
			Commands.commandsQueue.clear();
		});
		Events.on(EventType.ClientPreConnectEvent.class, s -> {
			Log.debug("Ozone-Event-@: @:@ = @", s.getClass().getSimpleName(), s.host.address, s.host.port, s.host.name);
			Events.fire(EventExtended.Connect.Connected);
		});
		Events.on(EventType.CommandIssueEvent.class, s -> {
			Log.debug("Ozone-Event-@: new issue @ at @", s.getClass().getSimpleName(), s.command.toString(), s.tile.tile.toString());
		});
		Events.on(EventType.DepositEvent.class, s -> {
			if (s.player == null) return;
			Log.debug("Ozone-Event-@: @ deposited @ @ at @", s.getClass().getSimpleName(), s.player.name(), s.amount, s.item.toString(), s.tile.tile().toString());
		});
		Events.on(EventType.WithdrawEvent.class, s -> {
			if (s.player == null) return;
			Log.debug("Ozone-Event-@: @ withdrawn @ @ at @", s.getClass().getSimpleName(), s.player.name(), s.amount, s.item.toString(), s.tile.tile().toString());
		});
		Events.on(EventType.StateChangeEvent.class, s -> {
			Log.debug("Ozone-Event-@: State changed from @ to @", s.getClass().getSimpleName(), s.from, s.to);
			if (s.from.equals(GameState.State.playing) && s.to.equals(GameState.State.menu))
				Events.fire(EventExtended.Connect.Disconnected);
		});
		Events.on(EventType.UnitCreateEvent.class, s -> {
			Log.debug("Ozone-Event-@: A @ created at @,@", s.getClass().getSimpleName(), s.unit.getClass().getSimpleName(), s.unit.x(), s.unit.y());
		});
		Events.on(EventType.UnitChangeEvent.class, s -> {
			if (Vars.state.getState().equals(GameState.State.menu)) return;//spammy af
			Log.debug("Ozone-Event-@: Player \"@\" changing into @ at @,@", s.getClass().getSimpleName(), s.player.name(), s.unit.getClass().getSimpleName(), s.unit.x(), s.unit.y());
		});
		Events.on(EventType.UnitDestroyEvent.class, s -> {
			if (s.unit.getPlayer() != null)
				Log.debug("Ozone-Event-@: Player \"@\" destroyed with @ at @,@", s.getClass().getSimpleName(), s.unit.getPlayer().name(), s.unit.getClass().getSimpleName(), s.unit.x(), s.unit.y());
			else
				Log.debug("Ozone-Event-@: A @ destroyed at @,@", s.getClass().getSimpleName(), s.unit.getClass().getSimpleName(), s.unit.x(), s.unit.y());
		});
		Events.on(EventType.UnitDrownEvent.class, s -> {
			if (s.unit.getPlayer() != null)
				Log.debug("Ozone-Event-@: Player \"@\" drowned with @ at @,@", s.getClass().getSimpleName(), s.unit.getPlayer().name(), s.unit.getClass().getSimpleName(), s.unit.x(), s.unit.y());
			else
				Log.debug("Ozone-Event-@: A @ drowned at @,@", s.getClass().getSimpleName(), s.unit.getClass().getSimpleName(), s.unit.x(), s.unit.y());
		});
		
		Events.on(EventType.BlockBuildEndEvent.class, s -> {
			if (s.unit.getPlayer() == null) return;//boring
			if (s.breaking)
				Log.debug("Ozone-Event-@: \"@\" successfully deconstructed @ with configuration @ on team @", s.getClass().getSimpleName(), s.unit.getPlayer().name(), s.tile.toString(), s.config, s.team);
			else
				Log.debug("Ozone-Event-@: \"@\" successfully constructed @ with configuration @ on team @", s.getClass().getSimpleName(), s.unit.getPlayer().name(), s.tile.toString(), s.config, s.team);
		});
		Events.on(EventType.PlayerJoin.class, s -> {
			if (s.player == null) return;
			Log.debug("Ozone-Event-@: \"@\" join", s.getClass().getSimpleName(), s.player.name());
		});
		Events.on(EventType.BlockDestroyEvent.class, s -> {
			Log.debug("Ozone-Event-@: @", s.getClass().getSimpleName(), s.tile.toString());
		});
		Events.on(EventType.BlockBuildBeginEvent.class, s -> {
			if (s.breaking)
				Log.debug("Ozone-Event-@: someone begin breaking at @ on team @", s.getClass().getSimpleName(), s.tile.toString(), s.team);
			else
				Log.debug("Ozone-Event-@: Someone begin building at @ on team @", s.getClass().getSimpleName(), s.tile.toString(), s.team);
		});
		Events.on(EventType.PlayerLeave.class, s -> {
			if (s.player == null) return;//boring
			Log.debug("Ozone-Event-@: \"@\" leave", s.getClass().getSimpleName(), s.player.name());
		});
		Events.on(EventType.ConfigEvent.class, s -> {
			if (s.player != null)
				Log.debug("Ozone-Event-@: @ has been changed from @ to @ by player \"@\"", s.getClass().getSimpleName(), s.tile.tile.toString(), s.tile.block().lastConfig, s.value, s.player.name());
			else
				Log.debug("Ozone-Event-@: @ has been changed from @ to @ by unknown", s.getClass().getSimpleName(), s.tile.tile.toString(), s.tile.block(), s.value);
		});
	}
	
	
	protected static void loadSettings() {
		Manifest.settings.add(Core.class);
		Events.fire(Internal.Init.SettingsRegister);
		arc.Core.settings.put("crashreport", false);
		for (Field f : Manifest.getSettings()) {
			try {
				if (boolean.class.equals(f.getType())) {
					f.setBoolean(null, arc.Core.settings.getBool("ozone." + f.getName(), f.getBoolean(null)));
				}else if (String.class.equals(f.getType())) {
					f.set(null, arc.Core.settings.getString("ozone." + f.getName(), (String) f.get(null)));
				}else if (int.class.equals(f.getType())) {
					f.setInt(null, arc.Core.settings.getInt("ozone." + f.getName(), f.getInt(null)));
				}else if (long.class.equals(f.getType())) {
					f.setLong(null, arc.Core.settings.getLong("ozone." + f.getName()));
				}else if (float.class.equals(f.getType())) {
					f.setFloat(null, arc.Core.settings.getFloat("ozone." + f.getName(), f.getFloat(null)));
				}
			}catch (Throwable t) {
				Log.errTag("Ozone-Settings", "Couldn't load settings for: ozone." + f.getName());
				Log.err(t);
			}
		}
		
	}
	
	protected static void patch() {
		try {
			mindustry.Vars.ui.chatfrag.addMessage("gay", "no");
			Log.infoTag("Ozone", "Patching");
			Events.fire(Internal.Init.PatchRegister);
			Translation.register();
			Vars.ui.settings = new SettingsDialog();
			Vars.enableConsole = true;
			Log.infoTag("Ozone", "Patching Complete");
			if (Core.debugMode) Log.level = (Log.LogLevel.debug);
			Log.debug("Ozone-Debug: @", "Debugs, peoples, debugs");
		}catch (Throwable t) {
			Log.infoTag("Ozone", "Patch failed");
			Log.err(t);
		}
	}
	
	
	public static void patchTranslation() {
		ObjectMap<String, String> modified = arc.Core.bundle.getProperties();
		for (ObjectMap.Entry<String, String> s : Interface.bundle) {
			modified.put(s.key, s.value);
		}
		if (Core.colorPatch) for (String s : arc.Core.bundle.getKeys()) {
			modified.put(s, getRandomHexColor() + modified.get(s) + "[white]");
		}
		arc.Core.bundle.setProperties(modified);
		for (Map.Entry<String, Commands.Command> c : Commands.commandsList.entrySet()) c.getValue().description = Commands.getTranslation(c.getKey());
	}
	
	protected static void initUI() {
		Dialog.DialogStyle ozoneStyle = new Dialog.DialogStyle() {
			{
				stageBackground = Styles.none;
				titleFont = Fonts.def;
				background = Tex.windowEmpty;
				titleFontColor = Pal.accent;
			}
		};
		Manifest.commFrag = new CommandsListFrag();
		Manifest.menu = new OzoneMenu(arc.Core.bundle.get("ozone.hud"), ozoneStyle);
		Manifest.commFrag.build(Vars.ui.hudGroup);
	}
	
	public static String getRandomHexColor() {
		return "[" + Random.getRandomHexColor() + "]";
	}
	
}
