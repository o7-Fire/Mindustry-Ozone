package Ozone;

import Atom.Random;
import Ozone.Commands.PlayerInterface;
import Ozone.Patch.ChatFragment;
import Ozone.Patch.DesktopInput;
import Ozone.Patch.SettingsDialog;
import Ozone.Patch.Translation;
import Ozone.UI.OzoneMenu;
import arc.Core;
import arc.scene.ui.Dialog;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;

import java.lang.reflect.Field;

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


    private static void loadSettings() {
        Field[] set = Settings.class.getDeclaredFields();
        for (Field f : set) {
            try {
                if (boolean.class.equals(f.getType())) {
                    f.setBoolean(null, Core.settings.getBool("ozone." + f.getName(), f.getBoolean(null)));
                } else if (String.class.equals(f.getType())) {
                    f.set(null, Core.settings.getString("ozone." + f.getName(), (String) f.get(null)));
                } else if (int.class.equals(f.getType())) {
                    f.setInt(null, Core.settings.getInt("ozone." + f.getName(), f.getInt(null)));
                } else if (long.class.equals(f.getType())) {
                    f.setLong(null, Core.settings.getLong("ozone." + f.getName()));
                } else if (float.class.equals(f.getType())) {
                    f.setFloat(null, Core.settings.getFloat("ozone." + f.getName(), f.getFloat(null)));
                }
            } catch (Throwable t) {
                Log.errTag("Ozone-Settings", "Couldn't load settings for: ozone." + f.getName());
                Log.err(t);
            }
        }
        Settings.colorPatch = Core.settings.getBool("ozone.colorPatch", Settings.colorPatch);
        Settings.antiSpam = Core.settings.getBool("ozone.antiSpam", Settings.antiSpam);
    }

    private static void patch() {
        try {
            Log.infoTag("Ozone", "Patching");
            Translation.patch();
            Time.mark();
            ObjectMap<String, String> modified = Core.bundle.getProperties();
            for (ObjectMap.Entry<String, String> s : Interface.bundle) {
                modified.put(s.key, s.value);
            }
            if (Settings.colorPatch)
                for (String s : Core.bundle.getKeys()) {
                    modified.put(s, getRandomHexColor() + modified.get(s));
                }
            Core.bundle.setProperties(modified);
            Log.infoTag("Ozone", "Patching translation done: " + Time.elapsed());
            Vars.control.input = new DesktopInput();
            Vars.ui.settings = new SettingsDialog();
            Vars.ui.chatfrag = new ChatFragment();
            Log.infoTag("Ozone", "Patching Complete");
        } catch (Throwable t) {
            Log.infoTag("Ozone", "Patch failed");
            Log.err(t);
        }
    }

    public static void initUI() {
        Manifest.menu = new OzoneMenu("Ozone Menu", Core.scene.getStyle(Dialog.DialogStyle.class));

    }

    public static String getRandomHexColor() {
        return "[" + Random.getRandomHexColor() + "]";
    }

}
