package Main;

import Ozone.Desktop.Pre.DownloadSwing;

import java.io.File;
import java.net.URL;

public class Download {
    public static void main(URL url, File file) {
        DownloadSwing d = new DownloadSwing(url, file);
        d.display();
        d.run();
    }
}
