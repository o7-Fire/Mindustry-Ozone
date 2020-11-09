package Main;

import Ozone.Desktop.Pre.PreInstall;
import Ozone.Desktop.Swing.Main;

import java.io.File;

/**
 * @author Itzbenz
 */
public class OzoneInstaller {
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
        try {
            m.label1.setText("Mindustry " + new String(OzoneInstaller.class.getClassLoader().getResourceAsStream("Manifest.properties").readAllBytes()).split("\n")[3].split("=")[1]);
        } catch (Throwable ignored) {

        }
        PreInstall.install(m);
    }

}
