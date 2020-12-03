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

package Ozone.Desktop;

import Atom.File.SerializeData;
import Atom.Utility.Pool;
import Ozone.Desktop.UI.BotControllerDialog;
import Ozone.Desktop.UI.EnvironmentInformation;
import Ozone.Desktop.UI.ModsMenu;
import Ozone.Patch.ChatOzoneFragment;
import arc.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.sentry.Sentry;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Manifest {
    private static final String gApi = "https://api.github.com/repos/" + Propertied.Manifest.getOrDefault("GithubRepo", "null") + "/";
    private static final String gArtifact = gApi + "actions/artifacts/";
    private static final String gAuth = "QWtpbW92OmMxOWFmZDA5ZmRlNzNiYTg1NDg3ZGMzYjJmNmI2YjIxYmViMWE3ZTU=";//magic key
    public static File messageLogFolder = new File(Atom.Manifest.currentFolder, "Ozone/");
    public static final File messageLog = new File(messageLogFolder, "MessageLogArr.dat");
    public static final File messageLogBackup = new File(messageLogFolder, "BackupMessageLogArr.dat");
    public static int latestReleaseManifestID, latestBuildManifestID;
    public static BotControllerDialog botControllerDialog;
    public static ModsMenu modsMenu;
    public static EnvironmentInformation envInf;

    static {
        latestBuildManifestID = getLatestBuildManifestID();
        latestReleaseManifestID = getLatestReleaseManifestID();
    }


    public static boolean isBot() {
        return System.getProperty("BotID") != null;
    }

    public static boolean compatibleMindustryVersion(HashMap<String, String> a, HashMap<String, String> b) {
        return a.getOrDefault("MindustryVersion", "").equals(b.getOrDefault("MindustryVersion", "!"));
    }

    public static boolean compatibleMindustryVersion(HashMap<String, String> a) {
        String b = a.getOrDefault("MindustryVersion", "");
        if (b.isEmpty()) return false;//not sure
        if (!b.startsWith("v")) return false;//not sure
        b = b.substring(1);
        return Ozone.Manifest.getMindustryVersion().startsWith(b);
    }

    public static boolean isThisTheLatest(HashMap<String, String> a) {
        try {
            long source = Long.parseLong(Propertied.Manifest.getOrDefault("TimeMilis", "1"));
            long target = Long.parseLong(a.getOrDefault("TimeMilis", "0"));
            return target > source;
        } catch (NumberFormatException t) {
            Log.errTag("Long-Parse", t.toString());
        }
        return false;
    }

    public static boolean match(HashMap<String, String> a, HashMap<String, String> b) {
        return a.getOrDefault("BuilderID", "").equals(b.getOrDefault("BuilderID", ""));
    }

    public static int getLatestBuildManifestID() {
        Pool.submit(() -> {
            try {
                JsonArray apiRunner = new JsonParser().parse(new String(new URL(gApi + "actions/artifacts").openStream().readAllBytes())).getAsJsonObject().get("artifacts").getAsJsonArray();
                latestBuildManifestID = apiRunner.get(0).getAsJsonObject().get("id").getAsInt();
            } catch (Throwable t) {
                Sentry.captureException(t);
                Log.errTag("Ozone-Updater", "Failed to fetch latest build");
            }
            return null;
        });
        return 0;
    }

    public static HashMap<String, String> getManifest(int id) throws IOException {
        URL url = new URL(gArtifact + id + "/zip");
        HashMap<String, String> githubProp = Propertied.parse(new String(new URL("jar:" + getArtifactLocation(url) + "!/Manifest.properties").openStream().readAllBytes()));
        HashMap<String, String> temp = new HashMap<>(githubProp);
        temp.put("ManifestID", String.valueOf(id));
        temp.put("ManifestURL", url.toExternalForm());
        id--;
        temp.put("DownloadURL", gArtifact + id + "/zip");
        return temp;
    }

    public static String getArtifactLocation(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + gAuth);
        connection.setInstanceFollowRedirects(false);
        String main = connection.getHeaderField("Location");
        connection.disconnect();
        if (main == null)
            throw new NullPointerException("Cannot get artifact location");
        return main;
    }

    public static int getLatestReleaseManifestID() {
        Pool.submit(() -> {
            try {
                JsonArray o = new JsonParser().parse(new String(new URL(gApi + "releases").openStream().readAllBytes())).getAsJsonArray();
                JsonObject mf = o.get(0).getAsJsonObject();
                HashMap<String, String> hmf = Propertied.parse(mf.get("body").getAsString());
                if (!hmf.containsKey("id")) {

                } else {
                    latestReleaseManifestID = Integer.parseInt(hmf.get("id"));
                }
            } catch (Throwable t) {
                Sentry.captureException(t);
                Log.errTag("Ozone-Updater", "Failed to fetch latest release");
            }
            return null;
        });
        return 0;
    }

    public static void tryLoadLogMessage() {
        Log.infoTag("Ozone-MessageLogger", "Loading messageLog");

        try {
            loadMessageLog(messageLog);
            return;
        } catch (Throwable t) {
            Log.errTag("Ozone-MessageLogger", "Cant load " + Ozone.Desktop.Manifest.messageLog.getAbsolutePath());
            Log.errTag("Ozone-MessageLogger", t.toString());
            t.printStackTrace();
        }
        Log.infoTag("Ozone-MessageLogger", "Loading messageLogBackup");

        try {
            loadMessageLog(messageLogBackup);
        }catch (Throwable t) {
            Log.errTag("Ozone-MessageLogger", "Cant load " + Manifest.messageLogBackup.getAbsolutePath());
            Log.errTag("Ozone-MessageLogger", t.toString());
            t.printStackTrace();
        }
    }

    private static void loadMessageLog(File messageLogBackup) throws java.io.IOException, ClassNotFoundException {

        for (Object co : SerializeData.dataArrayIn(messageLogBackup).get()) {
            if (!(co instanceof ChatOzoneFragment.ChatMessage)) continue;
            ChatOzoneFragment.ChatMessage cm = (ChatOzoneFragment.ChatMessage) co;
            ChatOzoneFragment.messages.add(cm);
            ChatOzoneFragment.messages.sort(chatMessage -> chatMessage.id);
        }
    }

    public static void trySaveLogMessage() {
        Log.infoTag("Ozone-MessageLogger", "Saving messageLog");

        new Thread(() -> {
            synchronized (messageLog) {
                try {
                    SerializeData.dataOut(new SerializeData.DataArray<>(ChatOzoneFragment.messages.toArray()), messageLog);
                }catch (Throwable t) {
                    Log.errTag("Ozone-MessageLogger", "Cant save " + Ozone.Desktop.Manifest.messageLog.getAbsolutePath());
                    Log.errTag("Ozone-MessageLogger", t.toString());
                    Sentry.captureException(t);
                    t.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            synchronized (messageLogBackup) {
                try {
                    SerializeData.dataOut(new SerializeData.DataArray<>(ChatOzoneFragment.messages.toArray()), messageLogBackup);
                }catch (Throwable t) {
                    Log.errTag("Ozone-MessageLogger", "Cant save " + Manifest.messageLogBackup.getAbsolutePath());
                    Log.errTag("Ozone-MessageLogger", t.toString());
                    t.printStackTrace();
                }
            }
        });


    }
}
