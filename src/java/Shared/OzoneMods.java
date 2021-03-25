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

package Shared;

import Ozone.Bootstrap.OzoneBootstrap;
import Ozone.Main;
import Ozone.Manifest;
import Ozone.Settings.BaseSettings;
import Ozone.Settings.SettingsManifest;
import arc.Events;
import arc.files.Fi;
import arc.util.Log;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.mod.Mod;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class OzoneMods extends Mod {
	static {
		if (SharedBoot.hardDebug) Log.debug("Static Ctor");
		if (!WarningHandler.isLoaded()) {
			if (SharedBoot.isCore()) {
				try {
					SettingsManifest.changeFile(new File(Vars.dataDirectory.file(), SettingsManifest.settingsFile.getName()));
				}catch (Throwable ignored) {}
				try {
					if (BaseSettings.debugMode) SharedBoot.debug = true;
				}catch (Throwable ignored) {}
				try {
					LoggerMode.loadLogger();
					Log.info("Ozone Standalone");
					OzoneBootstrap.init();
				}catch (Throwable t) {
					catchs(t, true);
				}
			}
			@NotNull ITransaction s = null;
			try { s = Sentry.startTransaction("Init", "Early Init"); }catch (Throwable ignored) {}
			try {
				Main.earlyInit();
				if (s != null) s.finish();
			}catch (Throwable t) {
				if (s != null) {
					s.setThrowable(t);
					s.setStatus(SpanStatus.INTERNAL_ERROR);
				}
				catchs(t, true);
			}
		}
	}
	public OzoneMods() {
		if (SharedBoot.hardDebug) Log.debug("Ctor");
		if (WarningHandler.isLoaded()) return;
		@NotNull ITransaction s = null;
		try { s = Sentry.startTransaction("Init", "Pre Init"); }catch (Throwable ignored) {}
		try {
			Manifest.ozone = this;
			Main.preInit();
			if (s != null) s.finish();
		}catch (Throwable t) {
			if (s != null) {
				s.setThrowable(t);
				s.setStatus(SpanStatus.INTERNAL_ERROR);
			}
			catchs(t, true);
		}
	}
	
	public static void catchs(Throwable t) {
		catchs(t, false);
	}
	
	public static void catchs(Throwable t, boolean allowToCrash) {
		WarningHandler.handleMindustry(t);
		while (t.getCause() != null) t = t.getCause();
		Log.err(t);
		Throwable finalT = t;
		Events.on(EventType.ClientLoadEvent.class, s -> Vars.ui.showException(finalT));
		if (t.getClass() == RuntimeException.class) {
			throw (RuntimeException) t;//epic
		}
		if (allowToCrash) throw new RuntimeException(t);
	}
	
	@Override
	public void init() {
		if (SharedBoot.hardDebug) Log.debug("init");
		if (WarningHandler.isLoaded()) return;
		@NotNull ITransaction s = null;
		try { s = Sentry.startTransaction("Init", "Init"); }catch (Throwable ignored) {}
		try {
			Main.init();
			if (s != null) s.finish();
		}catch (Throwable t) {
			if (s != null) {
				s.setThrowable(t);
				s.setStatus(SpanStatus.INTERNAL_ERROR);
			}
			catchs(t);
		}
	}
	
	@Override
	public void loadContent() {
		if (WarningHandler.isLoaded()) return;
		@NotNull ITransaction s = null;
		try { s = Sentry.startTransaction("Init", "Init"); }catch (Throwable ignored) {}
		try {
			Main.loadContent();
			if (s != null) s.finish();
		}catch (Throwable t) {
			if (s != null) {
				s.setThrowable(t);
				s.setStatus(SpanStatus.INTERNAL_ERROR);
			}
			catchs(t);
		}
	}
	
	@Override
	public Fi getConfig() {
		if (SharedBoot.hardDebug) Log.debug("getConfig");
		return super.getConfig();
	}
	
}
