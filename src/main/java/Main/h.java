package Main;

import Ozone.Pre.Download;
import Ozone.Pre.PreInstall;
import Ozone.Swing.Main;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static Main.Ozone.*;

public class h {
    private static Main m;
    public static File mindustry;

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(WindowsLookAndFeel.class.getTypeName());
        if (System.getProperty("os.name").toUpperCase().contains("WIN"))
            mindustry = new File(System.getenv("AppData") + "/Mindustry");
        else
            mindustry = new File(System.getenv("user.home"));
        m = new Main();
        PreInstall.install(m);
    }

}
