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

import Ozone.Desktop.Patch.DesktopPatcher;
import Ozone.Event.DesktopEvent;
import Ozone.Main;
import arc.Core;
import arc.Events;
import arc.util.Log;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.mod.Mod;

/**
 * @author Itzbenz
 */
public class Ozone extends Mod {
	
	public Ozone() {
		Events.on(DesktopEvent.InitUI.class, s -> {
			if (!Vars.headless) {
			
				
			}
		});
		
		if (Core.settings != null) {
			Core.settings.put("crashreport", false);
			Core.settings.put("uiscalechanged", false);//shut
		}
	}
	
	
	@Override
	public void init() {
		
		try {
			DesktopPatcher.register();
			Main.init();
		}catch (Throwable t) {
			Sentry.captureException(t);
			Log.err(t);
			throw new RuntimeException(t);
		}
	}
	
	@Override
	public void loadContent() {
		try {
			DesktopPatcher.async();
			Main.loadContent();
		}catch (Throwable t) {
			Sentry.captureException(t);
			Log.err(t);
			throw new RuntimeException(t);
		}
	}
	
}
