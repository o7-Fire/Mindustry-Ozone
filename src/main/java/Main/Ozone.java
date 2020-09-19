package Main;

import Ozone.Main;
import Ozone.Pre.PreLoad;
import arc.util.Log;
import mindustry.mod.Mod;

public class Ozone extends Mod {


    @Override
    public void init() {
        if (PreLoad.init())
            Main.init();
        else
            Log.err("Ozone Error");
        if (PreLoad.atomicClassloader != null)
            Log.err("AtomicClassLoader isn't null");
    }

    @Override
    public void loadContent() {
        if (PreLoad.init())
            Main.loadContent();
        else
            Log.err("Ozone Error");
        if (PreLoad.atomicClassloader != null)
            Log.err("AtomicClassLoader isn't null");
    }


}
