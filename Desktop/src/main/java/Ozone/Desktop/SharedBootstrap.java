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

import Ozone.Watcher.Version;
import io.sentry.Scope;
import io.sentry.Sentry;
import io.sentry.protocol.User;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class SharedBootstrap {
    public static final String jitpack = "https://jitpack.io/com/github/o7-Fire/Atomic-Library/";
    protected static final ArrayList<String> StandaloneLibrary = new ArrayList<>(
            Arrays.asList(//TODO don't
                    "https://repo1.maven.org/maven2/com/miglayout/miglayout-core/5.2/miglayout-core-5.2.jar",
                    "https://repo1.maven.org/maven2/com/miglayout/miglayout-swing/5.2/miglayout-swing-5.2.jar",
                    "https://repo1.maven.org/maven2/com/formdev/flatlaf/0.43/flatlaf-0.43.jar"
            )
    ), ModsLibrary = new ArrayList<>();
    public static LibraryLoader libraryLoader;
    public static boolean customBootstrap;
    private static boolean mods, standalone, classpath, atomic;

    static {
        ModsLibrary.add(getAtom("Desktop", Propertied.h.getOrDefault("AtomHash", "-SNAPSHOT")));
        ModsLibrary.add(getAtom("Atomic", Propertied.h.getOrDefault("AtomHash", "-SNAPSHOT")));
    }

    static {
        Sentry.init(options -> {
            options.setDsn("https://cd76eb6bd6614c499808176eaaf02b0b@o473752.ingest.sentry.io/5509036");
            options.setRelease("Ozone." + Version.semantic + ":" + "Desktop." + Settings.Version.semantic);
        });
        Sentry.configureScope(SharedBootstrap::registerSentry);
    }

    public static void classloaderNoParent() {
        SharedBootstrap.libraryLoader = new LibraryLoader(new URL[]{SharedBootstrap.class.getProtectionDomain().getCodeSource().getLocation()}, null);
    }

    public static Scope registerSentry(Scope scope) {
        User user = new User();
        String id = "null";
        try {
            String usr = System.getProperty("user.name");
            id = String.valueOf(ByteBuffer.wrap(MessageDigest.getInstance("SHA-256").digest(usr.getBytes())).getLong());//one way hash
        } catch (Throwable e) {
            Sentry.captureException(e);
        }
        user.setId(id);
        scope.setUser(user);
        scope.setTag("Ozone.Desktop.Version", Settings.Version.semantic);
        scope.setTag("Ozone.Core.Version", Version.semantic);
        //scope.setContexts("Ozone.Mindustry.Version", Propertied.h.getOrDefault("MindustryVersion", "Idk"));
        //scope.setContexts("Atomic.Hash", Propertied.h.getOrDefault("AtomHash", "Snapshot"));
        scope.setTag("Operating.System", System.getProperty("os.name") + " x" + System.getProperty("sun.arch.data.model"));
        scope.setTag("Java.Version", System.getProperty("java.version"));
        for (Map.Entry<String, String> e : Propertied.h.entrySet())
            scope.setTag(e.getKey(), e.getValue());
        //StringBuilder s = new StringBuilder();
        //for (Map.Entry<String, String> e : Propertied.h.entrySet())
        //    s.append(e.getKey()).append("=").append(e.getValue()).append("\n");
        ///scope.setContexts("Manifest", s.toString());
        StringBuilder sb = new StringBuilder("Library List:\n");
        for (URL u : libraryLoader.getURLs()) sb.append("-").append(u.toString()).append("\n");
        scope.setContexts("Loaded.Library", sb.toString());
        //for (Map.Entry<String, String> s : Propertied.h.entrySet())
        return scope;
    }

    protected static void loadAtomic() throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (atomic) throw new IllegalStateException("Atom dependency already loaded");
        atomic = true;
        ArrayList<String> se = (ArrayList<String>) libraryLoader.loadClass("Main.LoadAtom").getMethod("main", String[].class).invoke(null, (Object) new String[0]);
        for (String s : se)
            libraryLoader.addURL(new URL(s));
    }

    public static void loadMods() throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (mods) throw new IllegalStateException("Mods dependency already loaded");
        mods = true;
        for (String s : ModsLibrary)
            libraryLoader.addURL(new URL(s));
        loadAtomic();
        //libraryLoader.addURL(new URL(Manifest.atomDownloadLink));//Atomic Ozone.Core
        //libraryLoader.addURL(new URL(desktopAtomicURL));//Atomic Ozone.Core

    }

    public static String getAtom(String type, String hash) {
        return jitpack + type + "/" + hash + "/" + type + "-" + hash + ".jar";
    }

    public static void loadStandalone() throws MalformedURLException {
        if (standalone) throw new IllegalStateException("Standalone dependency already loaded");
        standalone = true;
        for (String s : StandaloneLibrary)
            libraryLoader.addURL(new URL(s));
    }

    public static void loadClasspath() throws MalformedURLException {
        if (classpath) throw new IllegalStateException("Classpath dependency already loaded");
        classpath = true;
        for (String s : System.getProperty("java.class.path").split(System.getProperty("os.name").toUpperCase().contains("WIN") ? ";" : ":"))
            //if(s.contains("gson"))continue;else
            libraryLoader.addURL(new File(s));
    }

    public static void loadMain(String classpath, String[] arg) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SharedBootstrap.libraryLoader.loadClass(classpath).getMethod("main", String[].class).invoke(null, (Object) arg);

    }


}
