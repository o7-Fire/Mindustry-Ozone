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
        Events.on(EventType.ClientLoadEvent.class, s->initUI());
    }


    public static void loadContent() {
        patch();
    }

    public static void registerClientCommands(CommandHandler handler) {

    }

    private static void patch(){
        if(Vars.control != null && Vars.control.input != null) {
            Vars.control.input = new DesktopInput();
        } else{
            Log.warn("Can't patch control.input right now, we get em next time");
            Events.on(EventType.ClientLoadEvent.class, s -> {
                Vars.control.input = new DesktopInput();
                Log.infoTag("Ozone", "control.input patched");
            });
        }
    }
    public static void initUI(){
        Manifest.menu = new OzoneMenu("Ozone Menu", Core.scene.getStyle(Dialog.DialogStyle.class));
    }
}
