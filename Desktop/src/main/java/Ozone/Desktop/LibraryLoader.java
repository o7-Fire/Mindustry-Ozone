package Ozone.Desktop;

import Ozone.Desktop.Pre.DownloadSwing;
import Ozone.Pre.Download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class LibraryLoader extends URLClassLoader {
    public static File cache = new File("lib/");
    static {
        cache.mkdirs();
        registerAsParallelCapable();
    }


    public void defineClass(String name, InputStream is) throws IOException {
        byte[] h = is.readAllBytes();
        defineClass(name, h, 0, h.length);
    }

    public LibraryLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public LibraryLoader(URL[] urls) {
        super(urls);
    }

    //Fuck you java reflection illegal access
    //pretty sure its a bug
    @Override
    public void addURL(URL url) {
        //if (Ozone.Core.settings.getBool("ozone.debugMode", false))
        //    Log.debug("Ozone-LibraryLoader: @", "Loading: " + url.toString());
        if (url.getProtocol().startsWith("http")) {
            File temp = new File(cache, url.getFile().substring(1).replace("/", "."));
            if (!temp.exists()) {
                try {
                    findClass("net.miginfocom.swing.MigLayout");
                    DownloadSwing d = new DownloadSwing(url, temp);
                    d.display();
                    d.run();
                } catch (Throwable ignored) {
                    Download d = new Download(url, temp);
                    d.run();
                }
            }
            if (temp.exists()) {
                try {
                    url = temp.toURI().toURL();
                } catch (MalformedURLException ignored) {

                }
            }

        }

        super.addURL(url);
    }

    public void addURL(File file) throws MalformedURLException {
        if (file.exists())
            addURL(file.toURI().toURL());
        //else Log.errTag("Ozone-LibraryLoader", file.getAbsolutePath() + " doesn't exist");
    }


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        //Note: don't mess with java.
        if (name.startsWith("java.")) return ClassLoader.getSystemClassLoader().loadClass(name);
        //Log.infoTag("Ozone-LibraryLoader", name);
        return super.loadClass(name);
    }
}
