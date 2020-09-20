package Ozone;


import Ozone.Patch.ImprovisedKeybinding;
import arc.Events;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.game.EventType;

public class Interface {
    protected static final ObjectMap<String, String> bundle = new ObjectMap<>();
    protected static final ObjectMap<ImprovisedKeybinding, Runnable> keybindings = new ObjectMap<>();

    //on load event show this stupid warning
    public static void warningUI(String title, String description) {
        if (Vars.ui == null)
            Events.on(EventType.ClientLoadEvent.class, s -> Vars.ui.showErrorMessage(title + "\n" + description));
        else
            Vars.ui.showErrorMessage(title + "\n" + description);
    }

    public synchronized static void registerKeybind(ImprovisedKeybinding b, Runnable r) {
        keybindings.put(b, r);
    }

    public synchronized static void registerWords(String key, String value) {
        bundle.put(key, value);
    }

    public synchronized static void registerWords(String key) {
        bundle.put(key, key);
    }
}





