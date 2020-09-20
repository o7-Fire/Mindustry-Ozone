package Main;

import Ozone.Main;
import Ozone.Pre.Preload;
import mindustry.mod.Mod;

public class Ozone extends Mod {
    public final static String AtomHash = "02051bd3ca";
    public final static String AtomDownload = "https://jitpack.io/com/github/o7-Fire/Atomic-Library/Atomic/" + AtomHash + "/Atomic-" + AtomHash + ".jar";

    @Override
    public void init() {
        if (Preload.incites(AtomHash, AtomDownload, this))
            Main.init();
    }

    @Override
    public void loadContent() {
        if (Preload.incites(AtomHash, AtomDownload, this))
            Main.loadContent();
    }


}
