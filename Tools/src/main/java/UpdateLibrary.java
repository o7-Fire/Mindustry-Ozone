import Main.Ozone;
import Ozone.Pre.Download;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdateLibrary {
    public static void main(String[] args) throws MalformedURLException {
        File atomic = new File("libs/Atomic.jar");
        atomic.delete();
        Download download = new Download(new URL(Ozone.AtomDownload), atomic);
        download.display();
        download.run();
    }
}
