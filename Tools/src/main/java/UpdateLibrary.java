import Ozone.Desktop.Pre.DownloadSwing;
import Ozone.Manifest;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdateLibrary {
    public static void main(String[] args) throws MalformedURLException {
        File atomic = new File("libs/Atomic.jar");
        atomic.delete();
        DownloadSwing download = new DownloadSwing(new URL(Manifest.atomDownloadLink), atomic);
        download.display();
        download.run();
    }
}
