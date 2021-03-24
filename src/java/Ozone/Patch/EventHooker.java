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

package Ozone.Patch;

import Ozone.Event.EventExtended;
import Ozone.Internal.AbstractModule;
import Ozone.Internal.InformationCenter;
import Ozone.Internal.ModuleInterfaced;
import Ozone.Manifest;
import Shared.SharedBoot;
import Shared.WarningHandler;
import arc.Core;
import arc.Events;
import arc.util.Log;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.game.EventType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import static Ozone.Commands.CommandsCenter.garbageCollector;

//no shit not that hooker rent
public class EventHooker extends AbstractModule {
	public static ArrayList<Runnable> drawc = new ArrayList<>();
	
	
	public static void resets() {
		for (Map.Entry<Class<? extends ModuleInterfaced>, ModuleInterfaced> m : Manifest.module.entrySet()) {
			try {
				m.getValue().reset();
			}catch (Throwable t) {
				WarningHandler.handleMindustry(t);
				Vars.ui.showException(t);
			}
		}
		garbageCollector();
	}
	
	@Override
	public void reset() throws Throwable {
		drawc.clear();
	}
	
	@Override
	public void init() throws Throwable {
		
		Vars.loadLogger();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Events.fire(EventExtended.Shutdown.class, new EventExtended.Shutdown());
		}));
		Events.on(EventType.ConfigEvent.class, s -> {
			Manifest.invokeAllModule(m -> m.onTileConfig(s.player, s.tile, s.value));
		});
		Events.run(EventType.Trigger.update, () -> {
			Manifest.invokeAllModule(ModuleInterfaced::update);
		});
		Events.on(EventType.ClientLoadEvent.class, s -> {
			arc.Core.settings.getBoolOnce("OzoneDisclaimer", () -> {
				Vars.ui.showCustomConfirm("[royal]Ozone[white]-[red]Warning", "Use this mods at your own risk", "Accept", "Accept", () -> { }, () -> { });
			});
			Core.settings.getBoolOnce("CrashReport1", () -> Vars.ui.showConfirm("Anonymous Data Reporter", "We collect your anonymous data e.g crash-log, to make your experience much worse", () -> {
			}));
			if (System.getProperty("Ozone-Foo") != null) {
				Core.settings.getBoolOnce("OzoneFoo", () -> {
					Vars.ui.showText("[royal]Ozone[white]-[red]Warning", "Foo client with ozone ????, are you a savage");
				});
			}
			SharedBoot.finishStartup();
			// setOzoneLogger();
		});
		Events.run(EventType.Trigger.draw, () -> {
			for (Iterator<Runnable> it = drawc.iterator(); it.hasNext(); ) {
				Runnable r = it.next();
				r.run();
			}
		});
		Events.run(EventExtended.Game.Start, () -> {
			Log.debug("Server: " + InformationCenter.getCurrentServerIP() + ":" + InformationCenter.getCurrentServerPort());
			resets();
			Manifest.invokeAllModule(ModuleInterfaced::onWorldLoad);
		});
		Events.run(EventExtended.Game.Stop, () -> {
			resets();
			Manifest.invokeAllModule(ModuleInterfaced::onWoldUnload);
		});
		Events.run(EventExtended.Connect.Disconnected, () -> {
		
		});
		Events.on(EventType.StateChangeEvent.class, s -> {
			Log.debug("Ozone-Event-@: State changed from @ to @", s.getClass().getSimpleName(), s.from, s.to);
			if (s.from.equals(GameState.State.playing) && s.to.equals(GameState.State.menu)) Events.fire(EventExtended.Game.Stop);
			else if (s.from.equals(GameState.State.menu) && s.to.equals(GameState.State.playing)) Events.fire(EventExtended.Game.Start);
			
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
		
		Events.on(EventType.UnitCreateEvent.class, s -> {
			Log.debug("Ozone-Event-@: A @ created at @,@", s.getClass().getSimpleName(), s.unit.getClass().getSimpleName(), s.unit.x(), s.unit.y());
		});
		Events.on(EventType.UnitChangeEvent.class, s -> {
			if (Vars.state.getState().equals(GameState.State.menu)) return;//spammy af
			Log.debug("Ozone-Event-@: Player \"@\" changing into @ at @,@", s.getClass().getSimpleName(), s.player.name(), s.unit.getClass().getSimpleName(), s.unit.x(), s.unit.y());
		});
		Events.on(EventType.UnitDestroyEvent.class, s -> {
			if (s.unit.getPlayer() != null) Log.debug("Ozone-Event-@: Player \"@\" destroyed with @ at @,@", s.getClass().getSimpleName(), s.unit.getPlayer().name(), s.unit.getClass().getSimpleName(), s.unit.x(), s.unit.y());
			else Log.debug("Ozone-Event-@: A @ destroyed at @,@", s.getClass().getSimpleName(), s.unit.getClass().getSimpleName(), s.unit.x(), s.unit.y());
		});
		Events.on(EventType.UnitDrownEvent.class, s -> {
			if (s.unit.getPlayer() != null) Log.debug("Ozone-Event-@: Player \"@\" drowned with @ at @,@", s.getClass().getSimpleName(), s.unit.getPlayer().name(), s.unit.getClass().getSimpleName(), s.unit.x(), s.unit.y());
			else Log.debug("Ozone-Event-@: A @ drowned at @,@", s.getClass().getSimpleName(), s.unit.getClass().getSimpleName(), s.unit.x(), s.unit.y());
		});
		
		Events.on(EventType.BlockBuildEndEvent.class, s -> {
			if (s.unit.getPlayer() == null) return;//boring
			if (s.breaking) Log.debug("Ozone-Event-@: \"@\" successfully deconstructed @ with configuration @ on team @", s.getClass().getSimpleName(), s.unit.getPlayer().name(), s.tile.toString(), s.config, s.team);
			else Log.debug("Ozone-Event-@: \"@\" successfully constructed @ with configuration @ on team @", s.getClass().getSimpleName(), s.unit.getPlayer().name(), s.tile.toString(), s.config, s.team);
		});
		Events.on(EventType.PlayerJoin.class, s -> {
			if (s.player == null) return;
			Log.debug("Ozone-Event-@: \"@\" join", s.getClass().getSimpleName(), s.player.name());
		});
		Events.on(EventType.BlockDestroyEvent.class, s -> {
			Log.debug("Ozone-Event-@: @", s.getClass().getSimpleName(), s.tile.toString());
		});
		Events.on(EventType.BlockBuildBeginEvent.class, s -> {
			if (s.breaking) Log.debug("Ozone-Event-@: someone begin breaking at @ on team @", s.getClass().getSimpleName(), s.tile.toString(), s.team);
			else Log.debug("Ozone-Event-@: Someone begin building at @ on team @", s.getClass().getSimpleName(), s.tile.toString(), s.team);
		});
		Events.on(EventType.PlayerLeave.class, s -> {
			if (s.player == null) return;//boring
			Log.debug("Ozone-Event-@: \"@\" leave", s.getClass().getSimpleName(), s.player.name());
		});
		Events.on(EventType.ConfigEvent.class, s -> {
			if (s.player != null) Log.debug("Ozone-Event-@: @ has been changed from @ to @ by player \"@\"", s.getClass().getSimpleName(), s.tile.tile.toString(), s.tile.block().lastConfig, s.value, s.player.name());
			else Log.debug("Ozone-Event-@: @ has been changed from @ to @ by unknown", s.getClass().getSimpleName(), s.tile.tile.toString(), s.tile.block(), s.value);
		});
	}
}
