package Ozone;

import java.net.URL;
import java.net.URLClassLoader;

public interface Platform {

    default ClassLoader loadCl(URL[] urls) {
        return new URLClassLoader(urls);
    }
}
