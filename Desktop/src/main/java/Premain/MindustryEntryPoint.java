package Premain;

import Ozone.Desktop.Pre.PrePatcher;
import Ozone.Desktop.SharedBootstrap;
import io.sentry.Sentry;

public class MindustryEntryPoint {
    //Mindustry Only
    public static void main(String[] args) {
        try {
            SharedBootstrap.classloaderNoParent();
            SharedBootstrap.loadStandalone();
            SharedBootstrap.loadMods();
            SharedBootstrap.customBootstrap = true;
            PrePatcher.init();
            SharedBootstrap.loadMain("Main.OzoneMindustry", args);
        } catch (Throwable t) {
            Sentry.captureException(t);
        }
    }
}
