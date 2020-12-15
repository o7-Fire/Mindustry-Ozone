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

package Ozone.Desktop.Patch;

import java.util.HashMap;
import java.util.Map;

import static Ozone.Interface.registerWords;
import static Ozone.Patch.Translation.*;
public class Translation {
    private static volatile boolean init = false;
    static HashMap<String, String> deskUI = new HashMap<>();
    public static void Init() {
        if (init) return;
        init = true;
        deskUI.put("EnvironmentInformation", "Environment Information");
        deskUI.put("DebugMenuDialog", "Debug Menu");
        commands.put("javac", "run single line of java code");
        commands.put("library", "manage runtime library");
        commands.put("debug", "System.out.println(\"yeet\")");
        settings.put("logMessage", "Log Every Message[Being Reworked]");
        settings.put("disableDefaultGif", "Disable default GIF list");
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
}
