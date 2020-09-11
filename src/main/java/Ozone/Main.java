package Ozone;

import Atom.Random;
import Ozone.Center.Commands;
import Ozone.Center.PlayerInterface;
import Ozone.Patch.DesktopInput;
import Ozone.Patch.SettingsDialog;
import Ozone.UI.OzoneMenu;
import arc.Core;
import arc.Events;
import arc.scene.ui.Dialog;
import arc.struct.ObjectMap;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType;

public class Main {

    public static void init() {
        Log.infoTag("Ozone", "Hail o7");
        loadSettings();
        patch();
        initUI();
        PlayerInterface.init();
    }


    public static void loadContent() {

    }

    public static void registerClientCommands(CommandHandler handler) {
        Commands.init(handler);
    }

    private static void loadSettings() {
        Manifest.colorPatch = Core.settings.getBool("ozone.colorPatch", Manifest.colorPatch);
    }

    private static void patch() {
        try {
            Log.infoTag("Ozone", "Patching");
            Vars.control.input = new DesktopInput();
            Log.infoTag("Ozone", "Vars.control.input patched");
            Vars.ui.settings = new SettingsDialog();

            Events.on(EventType.ClientLoadEvent.class, c -> {
                Time.mark();
                ObjectMap<String, String> modified = Core.bundle.getProperties();
                for (ObjectMap.Entry<String, String> s : Interface.bundle) {
                    modified.put(s.key, s.value);
                }
                if (Manifest.colorPatch)
                    for (String s : Core.bundle.getKeys()) {
                        modified.put(s, getRandomHexColor() + modified.get(s));
                    }
                Core.bundle.setProperties(modified);
                Log.infoTag("Ozone", "Patching translation done: " + Time.elapsed());
            });
        } catch (Throwable t) {
            Log.infoTag("Ozone", "Patch failed");
        }
    }

    public static void initUI() {
        Manifest.menu = new OzoneMenu("Ozone Menu", Core.scene.getStyle(Dialog.DialogStyle.class));

    }

    public static String getRandomHexColor() {
        return "[" + Random.getRandomHexColor() + "]";
    }

}
