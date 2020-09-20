package MainBackup;

import Ozone.Main;
import mindustry.mod.Mod;

//In case of classloader is not URClassloader
public class OzoneBackup extends Mod {

    @Override
    public void init() {

        Main.init();
    }

    @Override
    public void loadContent() {

        Main.loadContent();
    }
}
