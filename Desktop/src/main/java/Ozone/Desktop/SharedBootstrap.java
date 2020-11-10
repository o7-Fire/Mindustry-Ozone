package Ozone.Desktop;

import Ozone.Watcher.Version;
import io.sentry.Scope;
import io.sentry.Sentry;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


public class SharedBootstrap {
    public static final String jitpack = "https://jitpack.io/com/github/o7-Fire/Atomic-Library/";
    protected static final ArrayList<String> StandaloneLibrary = new ArrayList<>(
            Arrays.asList(
                    "https://repo1.maven.org/maven2/com/miglayout/miglayout-core/5.2/miglayout-core-5.2.jar",
                    "https://repo1.maven.org/maven2/com/miglayout/miglayout-swing/5.2/miglayout-swing-5.2.jar",
                    "https://repo1.maven.org/maven2/com/formdev/flatlaf/0.43/flatlaf-0.43.jar")
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
            options.setRelease("Ozone." + Version.semantic + ":" + "Desktop." + Premain.Version.semantic);
        });
        Sentry.configureScope(SharedBootstrap::registerSentry);

    }

    public static void classloaderNoParent() {
        SharedBootstrap.libraryLoader = new LibraryLoader(new URL[]{SharedBootstrap.class.getProtectionDomain().getCodeSource().getLocation()}, null);
    }

    public static Scope registerSentry(Scope scope) {
        scope.setContexts("Ozone.Version", Premain.Version.semantic);
        scope.setContexts("Ozone.Desktop.Version", Version.semantic);
        scope.setContexts("Ozone.Mindustry.Version", Propertied.h.getOrDefault("MindustryVersion", "Idk"));
        scope.setContexts("Atomic.Version", Propertied.h.getOrDefault("AtomHash", "Snapshot"));
        scope.setContexts("Operating.System", System.getProperty("os.name") + " x" + System.getProperty("sun.arch.data.model"));
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
            libraryLoader.addURL(new File(s));
    }

    public static void loadMain(String classpath, String[] arg) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SharedBootstrap.libraryLoader.loadClass(classpath).getMethod("main", String[].class).invoke(null, (Object) arg);

    }


}
