package Ozone;


import arc.Events;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.game.EventType;

public class Interface {
    protected static final ObjectMap<String, String> bundle = new ObjectMap<>();

    public static void warningUI(String title, String description) {
        if (Vars.ui == null)
            Events.on(EventType.ClientLoadEvent.class, s -> Vars.ui.showErrorMessage(title + "\n" + description));
        else
            Vars.ui.showErrorMessage(title + "\n" + description);
    }

    public synchronized static void registerWords(String key, String value) {
        bundle.put(key, value);
    }

    public synchronized static void registerWords(String key) {
        bundle.put(key, key);
    }
}





