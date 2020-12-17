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
import Atom.Utility.Random;
import Atom.Utility.Utility;
import Main.Download;
import Ozone.Desktop.Propertied;
import arc.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.core.Version;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class Updater {

    public final static AtomicBoolean newRelease = new AtomicBoolean(false), newBuild = new AtomicBoolean(false);
    private static volatile boolean init;
    public static volatile ArrayList<String> commitMessage = null;
    private static volatile String last = "-SNAPSHOT";

    public static void init() {
        if (init) return;
        init = true;
        Log.debug("Update Daemon Started");

        Pool.daemon(() -> {
            ArrayList<Future<String>> list = new ArrayList<>();
            ArrayList<String> ls = new ArrayList<>();
            try {
                URL u = new URL("https://api.github.com/repos/o7-Fire/Mindustry-Ozone/commits?per_page=80");
                JsonArray js = JsonParser.parseString(new String(u.openStream().readAllBytes())).getAsJsonArray();
                for (JsonElement je : js) {
                    list.add(Pool.submit(() -> {
                        try {
                            JsonObject jo = je.getAsJsonObject();
                            JsonObject commit = jo.get("commit").getAsJsonObject();
                            return commit.get("message").getAsString();
                        }catch (Throwable t) {
                            return "";
                        }
                    }));
                }
                for (Future<String> f : list) {
                    try {
                        ls.add(f.get());
                    }catch (Throwable ignored) {}
                }
            }catch (Throwable t) {
                Sentry.captureException(t);
            }
            commitMessage = ls;
        }).start();

        Pool.daemon(() -> {
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
            try {
                a.get();
            }catch (Throwable ignored) {}
            try {
                b.get();
            }catch (Throwable ignored) {}
            if (newBuild.get()) return;
            Pool.daemon(() -> {
                try {
                    URL u = new URL("https://api.github.com/repos/o7-Fire/Mindustry-Ozone/commits?per_page=" + Random.getInt(2, 15));
                    JsonArray js = JsonParser.parseString(new String(u.openStream().readAllBytes())).getAsJsonArray();
                    ArrayList<Future<String>> list = new ArrayList<>();
                    for (JsonElement je : js) {
                        list.add(Pool.submit(() -> checkJsonGithub(je)));
                    }
                    for (Future<String> f : list) {
                        try {
                            String h = f.get();
                            if (h == null) continue;
                            last = h;
                            newBuild.set(true);
                            return;
                        }catch (Throwable ignored) {}
                    }
                }catch (Throwable e) {
                    Sentry.captureException(e);
                }
            }).start();
        }).start();

    }

    private static String checkJsonGithub(JsonElement je) {
        try {
            JsonObject jb = (JsonObject) je;
            String sha = jb.get("sha").getAsString();
            if (sha == null) throw new NullPointerException("SHA null" + je.toString());
            /*
            Instant instant = Instant.parse(jb.get("commit").getAsJsonObject().get("committer").getAsJsonObject().get("date").getAsString());
            JsonObject tree = jb.get("commit").getAsJsonObject().get("tree").getAsJsonObject();
            URL url = null;
            for(JsonElement e : JsonParser.parseString(new String(new URL(tree.get("url").getAsString()).openStream().readAllBytes())).getAsJsonObject().get("tree").getAsJsonArray()){
                JsonObject s = (JsonObject) e;
                if(s.getAsJsonObject().get("path").getAsString().equals("settings.gradle"))
                    url = new URL(s.getAsJsonObject().get("url").getAsString());
            }

            HashMap<String, String> h = Encoder.parseProperty(url.openStream());
            h.put("TimeMilis", String.valueOf(instant.getEpochSecond() * 100));


             */
            HashMap<String, String> h;
            h = Encoder.parseProperty(getDownload(sha, true).openStream());
            if (latest(h))
                return sha;
            else
                return null;
        }catch (Throwable ignored) {
            return null;
        }
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
            String s = target.getOrDefault("MindustryVersion", "-2").substring(1);
            if (s.contains(".")) s = s.substring(0, s.indexOf('.'));
            int b = Integer.parseInt(s);
            check.add(b == a);
        }catch (NumberFormatException asshole) {
            Sentry.captureException(asshole);
            check.add(false);
        }
        return !check.contains(false);
    }

    public static URL getBuild(boolean manifest) {
        return getDownload(last, manifest);
    }

    public static URL getRelease(boolean manifest) {
        return getDownload("v" + Version.build, manifest);
    }

    public static URL getDownload(String version, boolean manifest) {
        try {
            URL u = new URL(Utility.getDownload(Utility.jitpack, "com.github.o7-Fire.Mindustry-Ozone", manifest ? "Manifest" : "Desktop", version));
            if (manifest) {
                u = new URL("jar:" + u.toExternalForm() + "!/Manifest.properties");
            }
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
