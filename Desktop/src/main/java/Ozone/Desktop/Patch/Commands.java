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

package Ozone.Desktop.Patch;

import Atom.Manifest;
import Atom.Struct.Stream;
import Atom.Utility.Utility;
import Ozone.Desktop.Pre.DownloadSwing;
import Settings.Core;
import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Icon;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static Ozone.Commands.Commands.*;
import static Settings.Core.debugMode;

public class Commands {
    private static final LinkedHashMap<Integer, Manifest.Library> libs = new LinkedHashMap<>();
    private static volatile boolean init = false;
    private static int i = 0;

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
        //register("library", new Command(Commands::library));
        register("debug", new Command(Commands::debug, Icon.pause));
        register("info-pos", new Command(Commands::infoPos, Icon.move));
    }

    public static void infoPos(ArrayList<String> arg) {
        tellUser("Mouse x,y: " + Vars.player.mouseX + ", " + Vars.player.mouseY);
        Ozone.Commands.Commands.infoPos(arg);
    }

    public static void debug(ArrayList<String> arg) {
        if (!debugMode) {
            tellUser("The debug mode mason, what do they mean");
            return;
        }

        if (i == 5) {
            tellUser("pls dont");
        }else if (i == 10)
            tellUser("stop pls");
        else if (i == 20) {
            tellUser("wtf ???");
            i = 0;
        }else {
            tellUser("The code mason, what do they mean");
        }
        i++;
    }

    public static void library(ArrayList<String> arg) {
        if (arg.isEmpty()) {
            tellUser("send help:  \"" + Core.commandsPrefix + "library help\"");
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
            }else if (arg.get(0).equalsIgnoreCase("list")) {
                availableLib();
                return;
            }else if (arg.get(0).equalsIgnoreCase("purge")) {
                Manifest.library.forEach(s -> s.getJar().deleteOnExit());
                tellUser("Restart to delete all library");
                return;
            }

        }else if (arg.size() == 2) {
            if (arg.get(0).equalsIgnoreCase("download")) {
                if (arg.get(1).equalsIgnoreCase("all")) {
                    Manifest.library.forEach(Commands::downloadLib);
                    return;
                }else {
                    try {
                        int index = Integer.parseInt(arg.get(1));
                        if (libs.get(index) == null) {
                            tellUser("No such index");
                            availableLib();
                            return;
                        }
                        downloadLib(libs.get(index));
                    }catch (NumberFormatException n) {
                        tellUser("Failed to parse init: " + arg.get(1));
                        return;
                    }
                }
            }
        }
        tellUser("No such commands");
        tellUser("send help:  \"" + Core.commandsPrefix + "library help\"");
    }

    public static void downloadLib(Manifest.Library s) {
        Thread e = new Thread(() -> {
            try {
                DownloadSwing d = new DownloadSwing(new URL(s.getDownloadURL()), s.getJar());
                d.display();
                d.run();
            }catch (Throwable t) {
                tellUser("Failed to download " + s.getName());
                tellUser(t.toString());
                t.printStackTrace();
            }
        });
        e.setDaemon(true);
        e.start();
    }


    public static void javac(ArrayList<String> arg) {


        String code = Utility.joiner(arg.toArray(new String[0]), " ");
        Thread th = new Thread(() -> {
            try {
                Atom.Runtime.Compiler.runLine(code, Stream.getReader(s -> Log.infoTag("javac", s)));
            }catch (FileNotFoundException fail) {
                tellUser("Failed to compile or IOException Problem");
                fail.printStackTrace();
                Log.errTag("Compiler", fail.toString());
            }catch (Throwable t) {
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
            tellUser(i + ". " + l.getName());
            tellUser("Downloaded = " + l.downloaded());
        });
    }

}
