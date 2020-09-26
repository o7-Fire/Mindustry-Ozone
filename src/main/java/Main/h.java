package Main;

import Ozone.Pre.PreInstall;
import Ozone.Swing.Main;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import javax.swing.*;
import java.io.File;

public class h {
    public static File mindustry;
    private static Main m;

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
