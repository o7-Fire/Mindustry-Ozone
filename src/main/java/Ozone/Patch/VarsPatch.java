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

package Ozone.Patch;

import Ozone.Event.Internal;
import Ozone.Internal.Module;
import Ozone.Settings.BaseSettings;
import arc.Events;
import arc.util.Log;
import mindustry.Vars;

public class VarsPatch implements Module {
	@Override
	public void init() throws Throwable {
		try {
			mindustry.Vars.ui.chatfrag.addMessage("gay", "no");
			Log.infoTag("Ozone", "Patching");
			Events.fire(Internal.Init.PatchRegister);
			Vars.enableConsole = true;
			Log.infoTag("Ozone", "Patching Complete");
			if (BaseSettings.debugMode) Log.level = (Log.LogLevel.debug);
			Log.debug("Ozone-Debug: @", "Debugs, peoples, debugs");
		}catch (Throwable t) {
			Log.infoTag("Ozone", "Patch failed");
			Log.err(t);
		}
	}
}
