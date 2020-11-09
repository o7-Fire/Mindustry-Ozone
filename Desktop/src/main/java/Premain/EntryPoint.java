package Premain;

import Main.Ozone;
import Ozone.Desktop.LibraryLoader;
import Ozone.Desktop.SharedBootstrap;
import Ozone.Manifest;
import arc.Events;
import arc.util.Log;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.desktop.DesktopLauncher;
import mindustry.game.EventType;
import mindustry.mod.Mod;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URL;

import static Ozone.Desktop.SharedBootstrap.libraryLoader;

//Mindustry only
public class EntryPoint extends Mod {
    public static final String type = "Desktop";
    public static final String desktopAtomicURL = Manifest.jitpack + type + "/" + Manifest.atomHash + "/" + type + "-" + Manifest.atomHash + ".jar";
    //get location of this Ozone mods
    //public static File ozone = new File(Ozone.class.getProtectionDomain().getCodeSource().getLocation().getFile());
    //get Mods directory from mods/Ozone.jar
    //public static File parentFile = ozone.getParentFile();
    //mods/libs directory
    //public static File library = new File(parentFile, Manifest.libs);
    //public static final File desktopAtomic = new File(library, "Atomic-" + type + "-" + Manifest.atomHash + ".jar");
    //mods/libs/Atomic-AtomHash.jar
    //public static File atom = new File(library, Manifest.atomFile);
    public Mod OzoneMod = null;

    //Modlaoder only
    public EntryPoint() {
        if (!SharedBootstrap.customBootstrap)
            startTheRealOne();
        try {
            //Preload.checkAtomic(Manifest.atomDownloadLink, atom);
            //libraryLoader.addURL(atom);//Atomic Ozone.Core
            //libraryLoader.addURL(desktopAtomic);//Atomic Desktop Extension
            SharedBootstrap.libraryLoader = new LibraryLoader(new URL[]{SharedBootstrap.class.getProtectionDomain().getCodeSource().getLocation()}, ClassLoader.getSystemClassLoader());
            SharedBootstrap.loadMods();
            Class<?> ozoneClass = libraryLoader.loadClass("Main.Ozone");
            OzoneMod = (Mod) ozoneClass.getDeclaredConstructor().newInstance();//Load main mods from LibraryLoader
        } catch (Throwable t) {
            Sentry.captureException(t);
            t.printStackTrace();
            Events.on(EventType.ClientLoadEvent.class, s -> Vars.ui.showInfoText("Failed to load ozone", "See log for more information"));
            Log.errTag("Ozone-Hook", t.toString());
        }
    }

    @Override
    public void init() {
        if (OzoneMod != null)
            OzoneMod.init();
    }

    @Override
    public void loadContent() {
        if (OzoneMod != null)
            OzoneMod.loadContent();
    }

    public static void startTheRealOne() {
        StringBuilder cli = new StringBuilder();
        try {
            cli.append(System.getProperty("java.home")).append(File.separator).append("bin").append(File.separator).append("java ");
            for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
                cli.append(jvmArg).append(" ");
            }
            cli.append("-cp ");
            cli.append(Ozone.class.getProtectionDomain().getCodeSource().getLocation().getFile());
            cli.append(" ");
            cli.append("Premain.MindustryEntryPoint").append(" ").append(DesktopLauncher.class.getProtectionDomain().getCodeSource().getLocation().getFile());
            //cli.append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
            Process p = Runtime.getRuntime().exec(cli.toString());


            //System.exit(0);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
