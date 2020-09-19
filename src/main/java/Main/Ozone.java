package Main;

import Ozone.Main;
import Ozone.Pre.PreLoad;
import mindustry.mod.Mod;

public class Ozone extends Mod {


    @Override
    public void init() {
        PreLoad.init(Main::init);
    }

    @Override
    public void loadContent() {
        PreLoad.init(Main::loadContent);
    }


}
