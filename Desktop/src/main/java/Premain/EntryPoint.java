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

package Premain;

import Main.Ozone;
import Ozone.Desktop.Bootstrap.SharedBootstrap;
import Ozone.Desktop.Propertied;
import arc.backend.sdl.jni.SDL;
import arc.util.Log;
import arc.util.Strings;
import io.sentry.Sentry;
import mindustry.core.Version;
import mindustry.desktop.DesktopLauncher;
import mindustry.mod.Mod;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

//Modloader only
public class EntryPoint extends Mod {
    public Mod OzoneMod = null;
    public EntryPoint() {
        Log.infoTag("Version", Strings.stripColors(Version.combined()));
        String required = Propertied.Manifest.getOrDefault("MindustryVersion", "Mindustry Version is gone");
        try {
            if (!SharedBootstrap.customBootstrap) {
                if (!required.contains(String.valueOf(Version.build)))
                    SDL.SDL_ShowSimpleMessageBox(SDL.SDL_MESSAGEBOX_WARNING, "Ozone", "Incompatible mindustry version, require: " + required);
                startTheRealOne();
            }else {
                Log.infoTag("Ozone", "Running in Ozone Mode");
                OzoneMod = new Main.Ozone();
            }
        }catch (Throwable t) {
            t.printStackTrace();
            Sentry.captureException(t);
        }


    }

    public static void startTheRealOne() {

        try {
            StringBuilder cli = new StringBuilder();
            cli.append(System.getProperty("java.home")).append(File.separator).append("bin").append(File.separator).append("java ");
            for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
                cli.append(jvmArg).append(" ");
            }
            cli.append("-cp ");
            cli.append(Ozone.class.getProtectionDomain().getCodeSource().getLocation().getFile());
            cli.append(" ");
            cli.append(MindustryEntryPoint.class.getTypeName()).append(" ").append(DesktopLauncher.class.getProtectionDomain().getCodeSource().getLocation().getFile());
            new Thread(() -> {
                try {
                    Runtime.getRuntime().exec(cli.toString()).waitFor();
                }catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }).start();
            System.exit(0);
        }catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() {
        try {
            if (OzoneMod != null)
                OzoneMod.init();
        } catch (Throwable t) {
            Log.err(t);
            t.printStackTrace();
            Sentry.captureException(t);
        }
    }

    @Override
    public void loadContent() {
        try {
            if (OzoneMod != null)
                OzoneMod.loadContent();
        } catch (Throwable t) {
            Log.err(t);
            t.printStackTrace();
            Sentry.captureException(t);
        }
    }

}
