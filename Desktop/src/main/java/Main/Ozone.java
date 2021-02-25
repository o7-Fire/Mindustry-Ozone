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

package Main;

import Ozone.Desktop.Manifest;
import Ozone.Desktop.Patch.DesktopPatcher;
import Ozone.Event.DesktopEvent;
import Ozone.Main;
import arc.Core;
import arc.Events;
import arc.util.Log;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import mindustry.Vars;
import mindustry.mod.Mod;
import org.jetbrains.annotations.NotNull;

/**
 * @author Itzbenz
 */
public class Ozone extends Mod {
	static {
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
			Sentry.captureException(t);
			Log.err(t);
			throw new RuntimeException(t);
		}
	}
	
	public Ozone() {
		@NotNull ITransaction s = null;
		try { s = Sentry.startTransaction("Init", "Pre Init"); }catch (Throwable ignored) {}
		Events.on(DesktopEvent.InitUI.class, se -> {
			if (!Vars.headless) {
			
				
			}
		});
		
		if (Core.settings != null) {
			Core.settings.put("crashreport", false);
			Core.settings.put("uiscalechanged", false);//shut
		}
		try {
			Manifest.ozone = this;
			Main.preInit();
			if (s != null) s.finish();
		}catch (Throwable t) {
			if (s != null) {
				s.setThrowable(t);
				s.setStatus(SpanStatus.INTERNAL_ERROR);
			}
			Sentry.captureException(t);
			Log.err(t);
			throw new RuntimeException(t);
		}
	}
	
	
	@Override
	public void init() {
		@NotNull ITransaction s = null;
		try { s = Sentry.startTransaction("Init", "Init"); }catch (Throwable ignored) {}
		try {
			DesktopPatcher.register();
			Main.init();
			if (s != null) s.finish();
		}catch (Throwable t) {
			if (s != null) {
				s.setThrowable(t);
				s.setStatus(SpanStatus.INTERNAL_ERROR);
			}
			Sentry.captureException(t);
			Log.err(t);
			throw new RuntimeException(t);
		}
	}
	
	@Override
	public void loadContent() {
		@NotNull ITransaction s = null;
		try { s = Sentry.startTransaction("Init", "Init"); }catch (Throwable ignored) {}
		try {
			DesktopPatcher.async();
			Main.loadContent();
			if (s != null) s.finish();
		}catch (Throwable t) {
			if (s != null) {
				s.setThrowable(t);
				s.setStatus(SpanStatus.INTERNAL_ERROR);
			}
			Sentry.captureException(t);
			Log.err(t);
			throw new RuntimeException(t);
		}
	}
	
}
