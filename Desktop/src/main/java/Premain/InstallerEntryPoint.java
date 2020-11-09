package Premain;

import Ozone.Desktop.SharedBootstrap;
import io.sentry.Sentry;


public class InstallerEntryPoint {

    //Standalone only
    public static void main(String[] args) {
        try {
            SharedBootstrap.loadStandalone();
            SharedBootstrap.loadMain("Main.OzoneInstaller", args);
        } catch (Throwable t) {
            Sentry.captureException(t);
        }
    }
}
