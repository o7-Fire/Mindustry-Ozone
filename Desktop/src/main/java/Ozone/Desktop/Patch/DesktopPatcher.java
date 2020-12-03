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

import Atom.Reflect.Reflect;
import Atom.Utility.Pool;
import Ozone.Desktop.Bootstrap.SharedBootstrap;
import Ozone.Desktop.Manifest;
import Ozone.Event.EventExtended;
import Ozone.Event.Internal;
import Settings.Desktop;
import arc.Core;
import arc.Events;
import arc.backend.sdl.jni.SDL;
import arc.files.Fi;
import arc.net.Client;
import arc.util.Log;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import mindustry.net.ArcNetProvider;
import mindustry.net.Net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.util.HashMap;

public class DesktopPatcher {
    public static File cache = new File(Vars.dataDirectory.file(), "cache/");
    public static void async() {
        if (false) {
            if (Ozone.Desktop.Manifest.messageLog.exists()) {//try to load
                Ozone.Desktop.Manifest.tryLoadLogMessage();
            }
            Events.run(EventExtended.Connect.Disconnected, Ozone.Desktop.Manifest::trySaveLogMessage);
        }
    }

    public static void checkRelease() {
        Pool.submit(() -> {
            try {
                HashMap<String, String> release = Manifest.getManifest(Manifest.latestReleaseManifestID);
                if (!Manifest.compatibleMindustryVersion(release)) return null;
                if (!Manifest.isThisTheLatest(release)) return null;
                Events.on(EventType.ClientLoadEvent.class, s -> Vars.ui.showConfirm("Ozone-Update", "A new compatible release appeared", () -> selfUpdate(release.get("DownloadURL"))));
            } catch (Throwable i) {
                Log.errTag("Ozone-Updater", "Failed to get latest release manifest: " + i.toString());
                Sentry.captureException(i);
            }
            return null;
        });

    }

    public static String getServer() {
        try {
            Net.NetProvider n = Reflect.getField(Vars.net.getClass(), "provider", Vars.net);
            ArcNetProvider arc = (ArcNetProvider) n;
            Client c = Reflect.getField(arc.getClass(), "client", arc);
            return c.getRemoteAddressTCP().getHostName() + ":" + c.getRemoteAddressTCP().getPort();
        }catch (Throwable t) {
            Sentry.captureException(t);
            Log.err(t);
            return "Null";
        }
    }

    public static void selfUpdate(String url) {
        Vars.ui.loadfrag.show();
        Sentry.configureScope(scope -> {
            scope.setTag("Server", getServer());
            scope.setTag("PlayerSize", String.valueOf(Groups.player.size()));
            scope.setTag("Multiplayer", String.valueOf(Vars.net.client()));
        });
        Thread t = new Thread(() -> {
            try {
                File jar = new File(SharedBootstrap.class.getProtectionDomain().getCodeSource().getLocation().getFile());
                if (!new Fi(jar).extension().equals("jar"))
                    throw new InvalidPathException(jar.getAbsolutePath(), "is not a jar file");
                URL real = new URL("jar:" + Manifest.getArtifactLocation(new URL(url)) + "!/Ozone-Desktop.jar");
                FileOutputStream f = new FileOutputStream(jar);
                InputStream is = real.openStream();
                f.write(is.readAllBytes());
                SDL.SDL_ShowSimpleMessageBox(SDL.SDL_MESSAGEBOX_INFORMATION, "Exit", "Relaunch mindustry");
                System.exit(0);
            }catch (Throwable e) {
                Vars.ui.showException(e);
                Sentry.captureException(e);
            }finally {
                Vars.ui.loadfrag.hide();
            }
        });
        t.setDaemon(true);
        t.start();


    }

    public static void register() {
        checkRelease();

        Ozone.Manifest.settings.add(Desktop.class);
        Events.run(Internal.Init.CommandsRegister, Commands::Init);
        Events.run(Internal.Init.TranslationRegister, Translation::Init);
        Events.run(Internal.Init.PatchRegister, () -> {
            Vars.control.input = new DesktopInput();
        });
        Events.on(EventType.ClientLoadEvent.class, s -> {
            Core.settings.getBoolOnce("CrashReportv1", () -> {
                Vars.ui.showConfirm("Anonymous Data Reporter", "We collect your anonymous insensitive data (crash-log) so we can fix thing, no turning back", () -> {
                });
            });
            /*
            long need = Manifest.library.stream().filter(library -> !library.downloaded()).count();
            if (need == 0) return;
            if (!Vars.disableUI) {

                StringBuilder sb = new StringBuilder();
                sb.append("Additional library need to be downloaded").append("\n");
                Manifest.library.forEach(library -> {
                    if (library.downloaded()) return;
                    sb.append("-").append(library.getName()).append("\n");
                });
                sb.append("(restart required)");
                Vars.ui.showCustomConfirm("Download", sb.toString(), "Download", "Later", () -> {
                    for (Manifest.Library library : Manifest.library) {
                        try {
                            DownloadSwing d = new DownloadSwing(new URL(library.getDownloadURL()), library.getJar());
                            d.display();
                            d.run();
                            if (library.getJar().exists()) {
                                Log.infoTag("Ozone", library.getName() + " downloaded");
                            }
                        } catch (Throwable t) {
                            Vars.ui.showErrorMessage("Failed to download " + library.getName() + "\n" + t.toString());
                            t.printStackTrace();
                        }
                    }
                    Interface.restart();
                }, () -> {
                });
            }

             */


        });

    }
}
