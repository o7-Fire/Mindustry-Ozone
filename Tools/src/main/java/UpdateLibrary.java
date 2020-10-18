import Ozone.Desktop.Pre.DownloadSwing;
import Ozone.Manifest;

import java.io.File;
import java.net.URL;

public class UpdateLibrary {
    public static void main(String[] args) throws Throwable {
        File atomic = new File("libs/Atomic.jar");
        atomic.delete();
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    if (atomic.exists()) System.exit(0);
                } catch (Throwable t) {

                }
            }
        }).start();
        while (!atomic.exists()) {
            try {
                DownloadSwing download = new DownloadSwing(new URL(Manifest.atomDownloadLink), atomic);
                download.display();
                download.run();
            } catch (Throwable t) {
                t.printStackTrace();
                atomic.delete();
            }
        }
    }
}
