package Main;

import Atom.Manifest;
import Ozone.Desktop.SharedBootstrap;
import Ozone.Main;
import Ozone.Watcher.Version;
import Premain.EntryPoint;
import arc.Core;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.OS;
import arc.util.io.PropertiesUtils;
import io.sentry.Sentry;
import mindustry.desktop.DesktopLauncher;
import mindustry.mod.Mod;

import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.URL;

/**
 * @author Itzbenz
 */
public class Ozone extends Mod {

    public Ozone() {
        if (!SharedBootstrap.customBootstrap)
            startTheRealOne();
        Sentry.configureScope(scope -> {
            scope.setContexts("Ozone.Desktop.Version", Ozone.Desktop.Version.semantic);
            scope.setContexts("Ozone.Watcher.Version", Version.semantic);
            try {
                ObjectMap<String, String> Manifest = new ObjectMap<>();
                PropertiesUtils.load(Manifest, new InputStreamReader(Manifest.class.getResourceAsStream("/Manifest.properties")));
                scope.setContexts("Atomic.Version", Manifest.get("AtomHash").substring(0));
            } catch (Throwable ignored) {
                scope.setContexts("Atomic.Version", EntryPoint.desktopAtomicURL);
            }
            scope.setContexts("Mindustry.Version", mindustry.core.Version.combined());
            scope.setContexts("Operating.System", System.getProperty("os.name") + "x" + (OS.is64Bit ? "64" : "32") + " " + System.getProperty("sun.arch.data.model"));
        });
        Manifest.library.forEach(library -> {
            try {
                SharedBootstrap.libraryLoader.addURL(new URL(library.getDownloadURL()));
            } catch (Throwable e) {
                Log.errTag("Ozone-PreInit", "Can't load: " + library.getDownloadURL() + "\n" + e.toString());
            }
        });

        //gay spy
        //legit 100%
        if (Core.settings != null) {
            Core.settings.put("crashreport", false);
            Core.settings.put("uiscalechanged", false);//shut
        }
    }

    public static void startTheRealOne() {
        StringBuilder cli = new StringBuilder();
        boolean unix = !System.getProperty("os.name").toUpperCase().contains("WIN");
        char se = unix ? ':' : ';';
        try {
            cli.append(System.getProperty("java.home")).append(File.separator).append("bin").append(File.separator).append("java ");
            for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
                cli.append(jvmArg).append(" ");
            }
            cli.append("-cp ");
            cli.append(Ozone.class.getProtectionDomain().getCodeSource().getLocation().getFile());
            cli.append(se);
            cli.append(DesktopLauncher.class.getProtectionDomain().getCodeSource().getLocation().getFile());
            cli.append(" ");
            cli.append("Premain.MindustryEntryPoint");
            //cli.append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
            Runtime.getRuntime().exec(cli.toString());
            System.exit(0);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() {
        Main.init();
    }

    @Override
    public void loadContent() {
        Main.loadContent();
    }

    /*
    public Ozone() {


        //just in case
        library.mkdirs();
        try {
            //Check library
            Preload.incites(atom, Manifest.atomDownloadLink, this);
            //no error thrown ? good
            libraryURLLoaded = true;
        } catch (FileNotFoundException t) {
            libraryURLLoaded = false;
            Log.err("Cant download Atom library: " + t.toString());
        } catch (Throwable t) {
            //Exception just summoned
            libraryURLLoaded = false;
            t.printStackTrace();
            //this shit ain't reliable
            Log.err(t);
            Log.infoTag("Ozone", "Cant load Atom library, try using method 2");
        }
        //we already download it or not ?
        libraryExists = atom.exists();
        //oh already loaded ? no need to continue
        if (libraryURLLoaded) return;
        try {
            //oh we already download it, but its not yet loaded
            if (libraryExists) {
                //make new classloader
                classloader = new URLClassLoader(new URL[]{ozone.toURI().toURL(), atom.toURI().toURL()}, ClassLoader.getSystemClassLoader());
                Class<?> main = classloader.loadClass("MainBackup.OzoneBackup");
                mainMod = (Mod) main.getDeclaredConstructor().newInstance();
                //oh we did it nice
                return;
            } else {
                //wtf man network error ? can't download ? library removed ?
            }
        } catch (Throwable t) {
            //gabe itch
            Log.err(t);
        }
        //sike inform users

    }



    @Override
    public void init() {
        //Library loaded properly ?
        if (libraryURLLoaded) {
            DesktopPatcher.register();
            Main.init();
        } else if (libraryExists && mainMod != null) {//hmmm seem to use method 2
            //alternative
            mainMod.init();
        }
    }

    //same as above
    @Override
    public void loadContent() {
        if (libraryURLLoaded)
            Main.loadContent();
        else if (libraryExists && mainMod != null) {
            mainMod.init();
        }
    }

    */
}
