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

package Ozone.Settings;

import Atom.Reflect.Reflect;
import Ozone.Event.EventExtended;
import Ozone.Event.Internal;
import Ozone.Internal.Module;
import Ozone.Manifest;
import Ozone.Patch.Translation;
import arc.Events;
import arc.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static Ozone.Settings.SettingsManifest.saveMap;
import static Ozone.Settings.SettingsManifest.settingsFile;

public class BaseSettings implements Module {
	
	public static boolean colorPatch = false, debugMode = false;
	public static boolean antiSpam = false, worldLog = false, blockDebug = false;
	public static String commandsPrefix = ",";
	
	static {
		HashMap<String, String> t = new HashMap<>();
		t.put("colorPatch", "Enable Colorized Text");
		t.put("antiSpam", "Enable Anti-Spam");
		t.put("debugMode", "Enable Debug Mode");
		t.put("commandsPrefix", "Commands Prefix");
		t.put("blockDebug", "Block Debug(ctrl+mouse left)");
		t.put("worldLog", "Spam your console with world interaction log");
		Translation.addSettings(t);
		SettingsManifest.readSettings(BaseSettings.class);
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
	
	@Override
	public List<Class<? extends Module>> dependOnModule() {
		return new ArrayList<>(Reflect.getExtendedClass("Ozone", BaseSettings.class));
	}
	
	public static void save() {
		try {
			SettingsManifest.save(BaseSettings.class);
		}catch (IOException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	
}
