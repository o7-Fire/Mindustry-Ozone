/*
 * Copyright 2020 Itzbenz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package Ozone.Desktop;

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


    public LibraryLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public LibraryLoader(URL[] urls) {
        super(urls);
    }

    public void defineClass(String name, InputStream is) throws IOException {
        byte[] h = is.readAllBytes();
        defineClass(name, h, 0, h.length);
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
                    loadClass("Main.Download").getMethod("main", URL.class, File.class).invoke(null, url, temp);
                }catch (Throwable t) {
                    Download d = new Download(url, temp);
                    d.run();
                }
            }
            if (temp.exists()) {
                try {
                    url = temp.toURI().toURL();
                }catch (MalformedURLException ignored) {

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
