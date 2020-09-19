import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class h {
    public static String AtomHash = "f802965617";
    public static String AtomDownload = "https://jitpack.io/com/github/o7-Fire/Atomic-Library/Atomic/" + AtomHash + "/Atomic-" + AtomHash + ".jar";

    @org.junit.Test
    public void name() throws Throwable {
        File atom = new File("Atomic-" + AtomHash + ".jar");
        System.out.println(atom.getAbsolutePath());
        if (atom.exists()) atom.delete();
        System.out.println(AtomDownload);
        URL jitpack = new URL(AtomDownload);
        ReadableByteChannel rbc = Channels.newChannel(jitpack.openStream());
        FileOutputStream fos = new FileOutputStream(atom);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        ClassLoader loader = h.class.getClassLoader();
        if (!(loader instanceof URLClassLoader)) {
            System.out.println("Classloader is not URLClassLoader");
            return;
        }
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(loader, atom.toURI().toURL());
        Class<?> scl = loader.loadClass("Atom.Classloader.SystemURLClassLoader");
        Class<?> alr = loader.loadClass("Atom.Random");
        Method sclm = scl.getDeclaredMethod("loadJar", URLClassLoader.class, File.class);
        Method alrm = alr.getDeclaredMethod("getRandomHexColor");
        Object sclo = scl.getDeclaredConstructor().newInstance();
        Object alro = alr.getDeclaredConstructor().newInstance();
        sclm.invoke(sclo, (URLClassLoader) loader, atom);
        System.out.println(alrm.invoke(alro));
    }
}
