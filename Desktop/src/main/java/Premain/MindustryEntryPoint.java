package Premain;

import Ozone.Desktop.SharedBootstrap;
import io.sentry.Sentry;

public class MindustryEntryPoint {
    //Mindustry Only
    public static void main(String[] args) {
        try {
            SharedBootstrap.loadStandalone();
            SharedBootstrap.loadMods();
            SharedBootstrap.customBootstrap = true;
            SharedBootstrap.loadMain("Main.OzoneMindustry", args);
        } catch (Throwable t) {
            Sentry.captureException(t);
        }
    }
}
