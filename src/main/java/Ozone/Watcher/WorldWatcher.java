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
