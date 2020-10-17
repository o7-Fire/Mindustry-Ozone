package MainBackup;

import Ozone.Desktop.Patch.DesktopPatcher;
import Ozone.Main;
import mindustry.mod.Mod;

//In case of classloader is not URClassloader
public class OzoneBackup extends Mod {

    @Override
    public void init() {
        DesktopPatcher.register();
        Main.init();
    }

    @Override
    public void loadContent() {
        Main.loadContent();
    }
}
