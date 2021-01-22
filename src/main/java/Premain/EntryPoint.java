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

package Premain;

import Ozone.Bootstrap.OzoneBootstrap;
import Ozone.Main;
import arc.Core;
import arc.util.Log;
import io.sentry.Sentry;
import mindustry.mod.Mod;

public class EntryPoint extends Mod {
	
	public EntryPoint() {
		Log.info("Ozone Standalone");
		OzoneBootstrap.init();
		if (Core.settings != null) {
			Core.settings.put("crashreport", true);
			Core.settings.put("uiscalechanged", false);//shut
		}
	}
	
	@Override
	public void init() {
		try {
			Main.init();
		}catch (Throwable t) {
			Sentry.captureException(t);
			throw t;
		}
	}
	
	@Override
	public void loadContent() {
		try {
			Main.loadContent();
		}catch (Throwable t) {
			Sentry.captureException(t);
			throw t;
		}
	}
}
