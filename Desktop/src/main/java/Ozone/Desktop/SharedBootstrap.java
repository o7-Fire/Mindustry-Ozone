package Ozone.Desktop;

import Ozone.Manifest;
import Ozone.Watcher.Version;
import io.sentry.Scope;
import io.sentry.Sentry;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import static Premain.EntryPoint.desktopAtomicURL;

public class SharedBootstrap {
    public static LibraryLoader libraryLoader;
    public static final String[] StandaloneLibrary = new String[]{
            "https://repo1.maven.org/maven2/com/miglayout/miglayout-core/5.2/miglayout-core-5.2.jar",
            "https://repo1.maven.org/maven2/com/miglayout/miglayout-swing/5.2/miglayout-swing-5.2.jar",
            "https://repo1.maven.org/maven2/com/formdev/flatlaf/0.43/flatlaf-0.43.jar"
    };
    public static boolean customBootstrap;
    private static boolean mods, standalone;

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
        scope.setContexts("Ozone.Watcher.Version", Version.semantic);
        scope.setContexts("Operating.System", System.getProperty("os.name") + " x" + System.getProperty("sun.arch.data.model"));
        return scope;
    }

    public static void loadMods() throws MalformedURLException {
        if (mods) throw new IllegalStateException("Mods dependency already loaded");
        mods = true;
        libraryLoader.addURL(new URL(Manifest.atomDownloadLink));//Atomic Ozone.Core
        libraryLoader.addURL(new URL(desktopAtomicURL));//Atomic Ozone.Core

    }

    public static void loadStandalone() throws MalformedURLException {
        if (standalone) throw new IllegalStateException("Standalone dependency already loaded");
        standalone = true;
        for (String s : StandaloneLibrary)
            libraryLoader.addURL(new URL(s));
    }

    public static void loadMain(String classpath, String[] arg) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SharedBootstrap.libraryLoader.loadClass(classpath).getMethod("main", String[].class).invoke(null, (Object) arg);

    }

}
