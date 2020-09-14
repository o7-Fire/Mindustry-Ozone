package Main;

import Ozone.Main;
import arc.util.CommandHandler;
import mindustry.mod.Mod;

public class Ozone extends Mod {


    @Override
    public void init() {
        Main.init();
    }

    @Override
    public void loadContent() {
        Main.loadContent();
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        Main.registerClientCommands(handler);
    }


}
