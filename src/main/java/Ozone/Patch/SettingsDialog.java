package Ozone.Patch;

import Atom.Reflect.Reflect;
import arc.util.Log;
import mindustry.ui.dialogs.SettingsMenuDialog;

public class SettingsDialog extends SettingsMenuDialog {
    public SettingsDialog() {
        super();
        SettingsTable table = Reflect.getField(SettingsMenuDialog.class, "game", this);
        if(table == null) {
            Log.errTag("Ozone", "Can't patch settings");
            return;
        }
        table.checkPref("ozone.colorPatch", false);

    }

}
