package Main;

import Ozone.Desktop.Swing.Main;
import Ozone.Pre.PreInstall;

import java.io.File;

/**
 * @author Itzbenz
 */
public class h {
    public static File mindustry;
    public static Main m;

    public static void main(String[] args) {
        Main.setTheme();
        try {
            if (System.getProperty("os.name").toUpperCase().contains("WIN"))
                mindustry = new File(System.getenv("AppData") + "/Mindustry");//windows
            else
                mindustry = new File(System.getenv("HOME") + "/.local/share/Mindustry");//linux
        } catch (Throwable t) {
            mindustry = new File("mindustry/");//i gave up "yeet"//
        }
        m = new Main();

        PreInstall.install(m);
    }

}
