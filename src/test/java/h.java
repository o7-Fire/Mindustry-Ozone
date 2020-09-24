import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class h {
    URLClassLoader url;
    Runnable r;

    @org.junit.Test
    public void name() throws Throwable {
        File atom = new File("libs/Atomic.jar");
        System.out.println(atom.getAbsolutePath());
        url = new URLClassLoader(new URL[]{atom.toURI().toURL()}, ClassLoader.getSystemClassLoader());
        Class<? extends Runnable> cl = (Class<? extends Runnable>) Class.forName("b", true, url);
        r = cl.getConstructor().newInstance();
        r.run();
    }

}
