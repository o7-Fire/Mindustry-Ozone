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

package Ozone.Settings;

import Ozone.Event.EventExtended;
import Ozone.Event.Internal;
import Ozone.Internal.AbstractModule;
import Ozone.Main;
import Ozone.Manifest;
import Ozone.Patch.Translation;
import Shared.WarningHandler;
import arc.Events;
import arc.util.Log;

import java.io.IOException;
import java.util.HashMap;

import static Ozone.Settings.SettingsManifest.saveMap;
import static Ozone.Settings.SettingsManifest.settingsFile;

public class BaseSettings extends AbstractModule {
	
	public static boolean colorPatch = false, debugMode = false, logicCodeScanner = false;
	public static boolean worldLog = false, blockDebug = false, commandsToast = true;
	public static String commandsPrefix = ",";
	
	static {
		try {
			SettingsManifest.readSettings(BaseSettings.class);
		}catch (Throwable t) {
			WarningHandler.handleMindustry(t);
		}
		HashMap<String, String> t = new HashMap<>();
		t.put("colorPatch", "Enable Colorized Text");
		t.put("antiSpam", "Enable Anti-Spam");
		t.put("debugMode", "Enable Debug Mode");
		t.put("commandsPrefix", "Commands Prefix");
		t.put("blockDebug", "Block Debug(ctrl+mouse left)");
		t.put("commandsToast", "Commands output use Hud Toast");
		t.put("worldLog", "Spam your console with world interaction log");
		t.put("logicCodeScanner", "Enable Logic Code Scanner and jam dangerous content automatically");
		Translation.addSettings(t);
		
	}
	
	@Override
	public void init() throws Throwable {
		Manifest.settings.add(BaseSettings.class);
		Events.fire(Internal.Init.SettingsRegister);
		Log.info("Settings File for ozone: " + settingsFile.getAbsolutePath());
		Events.on(EventExtended.Shutdown.class, s -> {
			try {
				saveMap();
			}catch (Throwable e) {
				e.printStackTrace();
			}
		});
	}
	
	static {
		dependsOn.addAll(Main.getExtendedClass("Ozone", BaseSettings.class));
	}
	
	
	public static void save() {
		try {
			SettingsManifest.save(BaseSettings.class);
		}catch (IOException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	
}
