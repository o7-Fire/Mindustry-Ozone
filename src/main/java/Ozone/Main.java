package Ozone;

import Ozone.Patch.DesktopInput;
import Ozone.UI.OzoneMenu;
import arc.Core;
import arc.Events;
import arc.scene.ui.Dialog;
import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType;

public class Main {

    public static void init(){
        Log.infoTag("Ozone", "Hail o7");
        patch();
        initUI();
    }


    public static void loadContent() {

    }

    public static void registerClientCommands(CommandHandler handler) {

    }

    private static void patch(){
        Log.infoTag("Ozone","Patching");
        if(Vars.control != null && Vars.control.input != null) {
            Vars.control.input = new DesktopInput();
            Log.infoTag("Ozone", "control.input patched");
        }


    }
    public static void initUI(){
        Manifest.menu = new OzoneMenu("Ozone Menu", Core.scene.getStyle(Dialog.DialogStyle.class));
    }
}
