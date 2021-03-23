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

package Ozone.Desktop.Patch;

import Ozone.Patch.Translation;

import java.util.HashMap;
import java.util.Map;

import static Ozone.Internal.Interface.registerWords;

public class TranslationDesktop extends Translation {
	static HashMap<String, String> deskUI = new HashMap<>();
	
	public static void register() {
		deskUI.put("EnvironmentInformation", "Environment Information");
		deskUI.put("DebugMenuDialog", "Debug Menu");
		deskUI.put("ModuleFrag", "AbstractModule List");
		commands.put("javac", "run single line of java code");
		commands.put("library", "manage runtime library");
		commands.put("debug", TranslationDesktop.class.getClassLoader().toString());
		generalSettings.put("BotController.serverPort", "Base Port");
		generalSettings.put("BotController.serverName", "Base Port");
		generalSettings.put("showPlayerID", "Show Player ID");
		generalSettings.put("showPlayerTyping", "Show Player Typing Status");
		generalSettings.put("showPlayerShooting", "Show Player Shooting Status");
		registerWords("load.mods", "Ozone");
		registerWords("BotsController", "Bots Controller");
		for (Map.Entry<String, String> s : deskUI.entrySet()) {
			registerWords("Ozone.Desktop.UI." + s.getKey(), s.getValue());
		}
	}
	
	public void init() {
		register();
	}
}
