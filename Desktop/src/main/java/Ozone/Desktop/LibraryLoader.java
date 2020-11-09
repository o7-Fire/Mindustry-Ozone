package Ozone.Desktop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class LibraryLoader extends URLClassLoader {

    static {
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
        super.addURL(url);
    }

    public void addURL(File file) throws MalformedURLException {
        if (file.exists())
            addURL(file.toURI().toURL());
        //else Log.errTag("Ozone-LibraryLoader", file.getAbsolutePath() + " doesn't exist");
    }


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.startsWith("java"))
            return ClassLoader.getSystemClassLoader().loadClass(name);
        //Log.infoTag("Ozone-LibraryLoader", name);
        return super.loadClass(name);
    }
}
