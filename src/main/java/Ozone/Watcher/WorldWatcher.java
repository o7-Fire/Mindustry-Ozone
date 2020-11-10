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

package Ozone.Watcher;

import Atom.Struct.History;
import Ozone.Event.EventExtended;
import Ozone.Patch.TileLog;
import Settings.Core;
import arc.Events;
import arc.util.Log;
import mindustry.game.EventType;

import java.util.LinkedHashMap;

public class WorldWatcher {
    public static final LinkedHashMap<Long, History<TileLog>> tilesLog = new LinkedHashMap<>();
    private static boolean init = false;
    private static int currentTilesUpdate = 0;
    private static boolean pause = true;

    public static void init() {
        if (init) return;
        init = true;
        if (!Core.worldLog) return;
        Log.infoTag("Ozone-WorldWatcher", "Online");
        Events.on(EventType.WorldLoadEvent.class, WorldWatcher::onWorldLoad);
        Events.run(EventExtended.Connect.Disconnected, WorldWatcher::onDisconnect);
        Events.run(EventType.Trigger.update, WorldWatcher::update);
    }

    protected static void update() {
        if (pause) return;
        if (tilesLog.size() < currentTilesUpdate) {
            currentTilesUpdate = 0;
            return;
        }

    }

    protected static void onWorldLoad(EventType.WorldLoadEvent e) {
        pause = false;
        clear();
    }

    protected static void onDisconnect() {
        pause = true;
        clear();
    }

    protected static void clear() {
        tilesLog.clear();
    }
}
