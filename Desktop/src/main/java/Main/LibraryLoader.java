package Main;

import arc.util.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class LibraryLoader extends URLClassLoader {
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
        Log.infoTag("Ozone-LibraryLoader", "Loading: " + url.toString());
        super.addURL(url);
    }

    public void addURL(File file) throws MalformedURLException {
        addURL(file.toURI().toURL());
    }
}
