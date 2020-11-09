package Premain;

import Ozone.Desktop.SharedBootstrap;
import io.sentry.Sentry;

import java.io.File;

public class MindustryEntryPoint {
    //Mindustry Only
    public static void main(String[] args) {
        try {
            System.out.println("Initializing Ozone Environment");
            SharedBootstrap.classloaderNoParent();
            SharedBootstrap.loadStandalone();
            SharedBootstrap.loadClasspath();
            SharedBootstrap.loadMods();
            SharedBootstrap.libraryLoader.addURL(new File(args[0]));
            SharedBootstrap.customBootstrap = true;
            //PrePatcher.init();
            SharedBootstrap.loadMain("Main.OzoneMindustry", args);
        } catch (Throwable t) {
            t.printStackTrace();
            Sentry.captureException(t);
        }
    }
}
