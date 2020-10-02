import Ozone.Pre.Download;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class h {
    URLClassLoader url;
    Runnable r;

    @org.junit.Test
    public void name() throws Throwable {
        File temp = new File(System.currentTimeMillis() + ".zip");
        temp.deleteOnExit();
        System.out.println(temp.getAbsolutePath());
        Download d = new Download(new URL("http://212.183.159.230/200MB.zip"), temp);
        d.run();

    }

}
