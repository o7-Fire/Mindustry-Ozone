package Main;

import Ozone.Pre.PreInstall;
import Ozone.Swing.Main;

import javax.swing.*;
import java.io.File;

public class h {
    public static File mindustry;
    private static Main m;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Throwable ignored) {
        }
        if (System.getProperty("os.name").toUpperCase().contains("WIN"))
            mindustry = new File(System.getenv("AppData") + "/Mindustry");
        else
            mindustry = new File(System.getenv("HOME"));
        m = new Main();
        PreInstall.install(m);
    }

}
