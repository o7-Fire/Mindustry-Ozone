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

import Atom.Utility.Encoder;
import Atom.Utility.Pool;
import Atom.Utility.Utility;
import Main.Download;
import Ozone.Desktop.Propertied;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.core.Version;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class Updater {

    public final static AtomicBoolean newRelease = new AtomicBoolean(false), newBuild = new AtomicBoolean(false), finishedCheck = new AtomicBoolean(false);
    private static volatile boolean init;

    public static void init() {
        if (init) return;
        init = true;
        Future a = Pool.submit(() -> {
            try {
                newBuild.set(latest(Encoder.parseProperty(getBuild(true).openStream())));
            }catch (Throwable e) {
                Sentry.captureException(e);
            }
        });

        Future b = Pool.submit(() -> {
            try {
                newRelease.set(latest(Encoder.parseProperty(getRelease(true).openStream())));
            }catch (Throwable e) {
                Sentry.captureException(e);
            }
        });
        Pool.submit(() -> {
            try {
                a.get();
            }catch (Throwable ignored) {}
            try {
                b.get();
            }catch (Throwable ignored) {}
            finishedCheck.set(true);
        });

    }

    public static void update(URL url) {
        Vars.ui.loadfrag.show("Downloading");
        try {

            Download.main(url, new File(Updater.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
            Vars.ui.loadfrag.hide();
        }catch (Throwable t) {
            Vars.ui.loadfrag.hide();
            Vars.ui.showException(t);
            Sentry.captureException(t);
        }
    }

    public static boolean latest(Map<String, String> target) {
        Map<String, String> source = Propertied.Manifest;
        ArrayList<Boolean> check = new ArrayList<>();
        //Latest
        try {
            long a = Long.parseLong(source.getOrDefault("TimeMilis", "0")), b = Long.parseLong(target.getOrDefault("TimeMilis", "-1"));
            check.add(b > a);
        }catch (NumberFormatException asshole) {
            Sentry.captureException(asshole);
            check.add(false);
        }
        //Compatibility
        try {
            int a = Version.build;
            int b = Integer.parseInt(target.getOrDefault("MindustryVersion", "-2").substring(1));
            check.add(b == a);
        }catch (NumberFormatException asshole) {
            Sentry.captureException(asshole);
            check.add(false);
        }
        return !check.contains(false);
    }

    public static URL getBuild(boolean manifest) {
        return getDownload("-SNAPSHOT", manifest);
    }

    public static URL getRelease(boolean manifest) {
        return getDownload("v" + Version.build, manifest);
    }

    public static URL getDownload(String version, boolean manifest) {
        try {
            URL u = new URL(Utility.getDownload(Utility.jitpack, "com.github.o7-Fire.Mindustry-Ozone", manifest ? "Manifest" : "Desktop", version));
            if (manifest)
                u = new URL("jar:" + u.toExternalForm() + "!/Manifest.properties");
            return u;
        }catch (MalformedURLException malformedURLException) {
            Sentry.captureException(malformedURLException);
            throw new RuntimeException(malformedURLException);
        }
    }


    public static Future<?> async() {
        return Pool.submit(Updater::init);
    }
}
