package Ozone.Desktop.Patch;

import Atom.Manifest;
import Atom.Utility.Utility;
import Garbage.Settings;
import Ozone.Desktop.Pre.DownloadSwing;
import arc.util.Log;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static Ozone.Commands.Commands.*;

public class Commands {
    private static final LinkedHashMap<Integer, Manifest.Library> libs = new LinkedHashMap<>();
    private static volatile boolean init = false;

    public static void Init() {
        if (init) return;
        init = true;
        //java weird shit
        final int[] i = {1};
        Manifest.library.forEach(s -> {
            libs.put(i[0], s);
            i[0]++;
        });
        register("javac", new Command(Commands::javac));
        register("library", new Command(Commands::library));
        register("debug", new Command(Commands::debug));
    }

    public static void debug(ArrayList<String> arg) {
        tellUser("The code mason, what do they mean");
    }

    public static void library(ArrayList<String> arg) {
        if (arg.isEmpty()) {
            tellUser("send help:  \"" + Settings.commandsPrefix + "library help\"");
            return;
        }
        if (arg.size() == 1) {
            if (arg.get(0).equalsIgnoreCase("help")) {
                tellUser("library help");
                tellUser("library download all");
                tellUser("library download [library-index]");
                tellUser("library list");
                tellUser("library purge");
                return;
            } else if (arg.get(0).equalsIgnoreCase("list")) {
                availableLib();
                return;
            } else if (arg.get(0).equalsIgnoreCase("purge")) {
                Manifest.library.forEach(s -> s.jar.deleteOnExit());
                tellUser("Restart to delete all library");
                return;
            }

        } else if (arg.size() == 2) {
            if (arg.get(0).equalsIgnoreCase("download")) {
                if (arg.get(1).equalsIgnoreCase("all")) {
                    Manifest.library.forEach(Commands::downloadLib);
                    return;
                } else {
                    try {
                        int index = Integer.parseInt(arg.get(1));
                        if (libs.get(index) == null) {
                            tellUser("No such index");
                            availableLib();
                            return;
                        }
                        downloadLib(libs.get(index));
                    } catch (NumberFormatException n) {
                        tellUser("Failed to parse init: " + arg.get(1));
                        return;
                    }
                }
            }
        }
        tellUser("No such commands");
        tellUser("send help:  \"" + Settings.commandsPrefix + "library help\"");
    }

    public static void downloadLib(Manifest.Library s) {
        Thread e = new Thread(() -> {
            try {
                DownloadSwing d = new DownloadSwing(new URL(s.link), s.jar);
                d.display();
                d.run();
            } catch (Throwable t) {
                tellUser("Failed to download " + s.name);
                tellUser(t.toString());
                t.printStackTrace();
            }
        });
        e.setDaemon(true);
        e.start();
    }

    public static void javac(ArrayList<String> arg) {
        String requiredLibrary = "com.github.javaparser-javaparser-core";
        if (!Manifest.javacExists()) {
            tellUser("no javac detected, are you sure using JDK ?");
            return;
        }
        if (Manifest.library.stream().anyMatch(s -> s.name.contains(requiredLibrary))) {
            if (!Manifest.library.stream().filter(s -> s.name.contains(requiredLibrary)).findFirst().get().downloaded()) {
                tellUser(requiredLibrary + " is not yet downloaded");
                tellUser("use \"" + Settings.commandsPrefix + "library download all\"");
                return;
            }
        } else {
            tellUser("Cant find " + requiredLibrary + " in Manifest.library");
            availableLib();
        }
        String code = Utility.joiner(arg.toArray(new String[0]), " ");
        Thread th = new Thread(() -> {
            try {
                Atom.Runtime.Compiler.runLine(code, System.out);
            } catch (Throwable t) {
                t.printStackTrace();
                Log.errTag("Compiler", t.toString());
                tellUser(t.toString());
            }
        });
        th.setDaemon(true);
        th.start();

    }

    protected static void availableLib() {
        tellUser("Available library: ");
        libs.forEach((i, l) -> {
            tellUser(i + ". " + l.name + l.version);
            tellUser("Downloaded = " + l.downloaded());
        });
    }

}
