package Ozone.Patch;

import Atom.Reflect.Reflect;
import Ozone.Settings;
import arc.Core;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ui.dialogs.SettingsMenuDialog;

import java.lang.reflect.Field;

public class SettingsDialog extends SettingsMenuDialog {
    public SettingsDialog() {
        super();
        SettingsTable table = Reflect.getField(SettingsMenuDialog.class, "game", this);
        if (table == null) {
            Log.errTag("Ozone", "Can't patch settings");
            return;
        }
        Field[] set = Settings.class.getDeclaredFields();
        for (Field f : set) {
            try {
                if (boolean.class.equals(f.getType())) {
                    table.checkPref("ozone." + f.getName(), f.getBoolean(null), s -> {
                        try {
                            f.set(null, s);
                            Core.settings.put("ozone." + f.getName(), s);
                        } catch (IllegalAccessException e) {
                            Vars.ui.showException(e);
                        }
                    });
                    table.row();
                } else if (String.class.equals(f.getType())) {
                    table.label(() -> Core.bundle.get("String") + " " + Core.bundle.get("setting.ozone." + f.getName() + ".name"));
                    table.field(Core.settings.getString("ozone." + f.getName(), (String) f.get(null)), s -> {
                        try {
                            f.set(null, s);
                            Core.settings.put("ozone." + f.getName(), s);
                        } catch (IllegalAccessException e) {
                            Vars.ui.showException(e);
                        }
                    });
                    table.row();
                } else if (int.class.equals(f.getType())) {
                    table.label(() -> Core.bundle.get("Integer") + " " + Core.bundle.get("setting.ozone." + f.getName() + ".name"));
                    table.field(String.valueOf(Core.settings.getInt("ozone." + f.getName(), f.getInt(null))), s -> {
                        try {
                            f.setInt(null, Integer.parseInt(s));
                            Core.settings.put("ozone." + f.getName(), s);
                        } catch (IllegalAccessException e) {
                            Vars.ui.showException(e);
                        }
                    });
                    table.row();
                } else if (long.class.equals(f.getType())) {
                    table.label(() -> Core.bundle.get("Long") + " " + Core.bundle.get("setting.ozone." + f.getName() + ".name"));
                    table.field(String.valueOf(Core.settings.getLong("ozone." + f.getName(), f.getLong(null))), s -> {
                        try {
                            f.setLong(null, Long.parseLong(s));
                            Core.settings.put("ozone." + f.getName(), s);
                        } catch (IllegalAccessException e) {
                            Vars.ui.showException(e);
                        }
                    });
                    table.row();
                } else if (float.class.equals(f.getType())) {
                    table.label(() -> Core.bundle.get("Float") + " " + Core.bundle.get("setting.ozone." + f.getName() + ".name"));
                    table.field(String.valueOf(Core.settings.getFloat("ozone." + f.getName(), f.getFloat(null))), s -> {
                        try {
                            f.setFloat(null, Float.parseFloat(s));
                            Core.settings.put("ozone." + f.getName(), s);
                        } catch (IllegalAccessException e) {
                            Vars.ui.showException(e);
                        }
                    });
                    table.row();
                }
            } catch (Throwable t) {
                Log.errTag("Ozone-Settings", "Couldn't create settings for: ozone." + f.getName());
                Log.err(t);
            }
        }


    }

}
