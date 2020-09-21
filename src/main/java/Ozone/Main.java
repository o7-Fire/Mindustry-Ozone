package Ozone;

import Atom.Random;
import Ozone.Commands.BotInterface;
import Ozone.Commands.Commands;
import Ozone.Patch.DesktopInput;
import Ozone.Patch.SettingsDialog;
import Ozone.Patch.Translation;
import Ozone.UI.CommandsListFrag;
import Ozone.UI.JavaEditor;
import Ozone.UI.OzoneMenu;
import arc.Core;
import arc.scene.ui.Dialog;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;

import java.lang.reflect.Field;

public class Main {

    public static void init() {
        Log.infoTag("Ozone", "Hail o7");
        loadSettings();
        patch();
        initUI();
        BotInterface.init();
        Commands.init();


    }


    public static void loadContent() {

    }

    private static void patchLast() {

    }

    private static void initEvent() {

    }

    public static void update() {

    }

    private static void loadSettings() {
        Core.settings.put("crashreport", false);
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
                    modified.put(s, getRandomHexColor() + modified.get(s) + "[white]");
                }
            Core.bundle.setProperties(modified);
            Log.infoTag("Ozone", "Patching translation done: " + Time.elapsed());
            Log.infoTag("Ozone", "Patching DesktopInput");
            Vars.control.input = new DesktopInput();
            Log.infoTag("Ozone", "Patching Settings");
            Vars.ui.settings = new SettingsDialog();
            //Log.infoTag("Ozone", "Patching ChatFragment");
            //Vars.ui.chatfrag = new ChatFragment();
            Log.infoTag("Ozone", "Patching Complete");
            if (Settings.debugMode) Log.setLogLevel(Log.LogLevel.debug);
            Log.debug("Ozone-Debug", "Debugs, peoples, debugs");
        } catch (Throwable t) {
            Log.infoTag("Ozone", "Patch failed");
            Log.err(t);
        }
    }

    public static void initUI() {
        Dialog.DialogStyle ozoneStyle = new Dialog.DialogStyle() {
            {
                stageBackground = Styles.none;
                titleFont = Fonts.def;
                background = Tex.windowEmpty;
                titleFontColor = Pal.accent;
            }
        };
        Manifest.javaEditor = new JavaEditor(Core.bundle.get("ozone.javaEditor"), Styles.defaultDialog);
        Manifest.commFrag = new CommandsListFrag();
        ozoneStyle.stageBackground = Styles.none;
        Manifest.menu = new OzoneMenu(Core.bundle.get("ozone.hud"), ozoneStyle);

    }

    public static String getRandomHexColor() {
        return "[" + Random.getRandomHexColor() + "]";
    }

}
