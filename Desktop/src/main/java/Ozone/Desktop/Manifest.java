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
import Ozone.Patch.ChatOzoneFragment;
import arc.util.Log;
import arc.util.serialization.Base64Coder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import io.sentry.Sentry;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Manifest {
    public static File messageLogFolder = new File(Atom.Manifest.currentFolder, "Ozone/");
    public static File messageLog = new File(messageLogFolder, "MessageLogArr.dat");
    public static File messageLogBackup = new File(messageLogFolder, "BackupMessageLogArr.dat");
    private static final String gApi = "https://api.github.com/repos/" + Propertied.h.getOrDefault("GithubOwner", "null") + "/" + Propertied.h.getOrDefault("GithubRepo", "null") + "/";
    private static final String github = "https://github.com/" + Propertied.h.getOrDefault("GithubOwner", "null") + "/" + Propertied.h.getOrDefault("GithubRepo", "null") + "/";
    private static final String auth = "Akimov:0ae9361bfed687fd76b5e554f4f1e8872fc55500";
    public static HashMap<String, String> githubManifest = new HashMap<>();

    static {
        getLatestBuild();
        /*
        try {
           JsonArray o = JsonParser.parseString(new String(new URL(gApi+"releases").openStream().readAllBytes())).getAsJsonArray();
           JsonObject mf = o.get(0).getAsJsonObject();
           HashMap<String, String> hmf = Propertied.parse(mf.get("body").getAsString());
           if(!hmf.containsKey("id")){
               Log.errTag("Ozone-Updater", "Failed to get update, release id gone");
           }else {
               githubManifest.put("releaseID", hmf.get("id"));
               JsonArray apiRunner = JsonParser.parseString(new String(new URL(gApi+"actions/artifacts").openStream().readAllBytes())).getAsJsonObject().get("artifacts").getAsJsonArray();
               int packageManifest =  apiRunner.get(0).getAsJsonObject().get("id").getAsInt();
               URL url = new URL(apiRunner.get(0).getAsJsonObject().get("archive_download_url").getAsString());
               HttpURLConnection connection = (HttpURLConnection) url.openConnection();
               connection.setRequestProperty("Authorization", "Basic " + Base64Coder.encodeString(auth));
               connection.setInstanceFollowRedirects(false);
               String main = connection.getHeaderField("Location");
               HashMap<String, String> githubProp = Propertied.parse(new String(new URL("jar:"+main+"!/Manifest.properties").openStream().readAllBytes()));
               githubManifest.putAll(githubProp);
               connection.disconnect();
           }
        }catch (Throwable e) {
            Log.errTag("Ozone-Updater", "Failed to fetch info: " + e.toString());
            e.printStackTrace();
        }

         */
    }

    public static void getLatestBuild() {
        try {
            JsonArray apiRunner = JsonParser.parseString(new String(new URL(gApi + "actions/artifacts").openStream().readAllBytes())).getAsJsonObject().get("artifacts").getAsJsonArray();
            URL url = new URL(apiRunner.get(0).getAsJsonObject().get("archive_download_url").getAsString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + Base64Coder.encodeString(auth));
            connection.setInstanceFollowRedirects(false);
            String main = connection.getHeaderField("Location");
            HashMap<String, String> githubProp = Propertied.parse(new String(new URL("jar:" + main + "!/Manifest.properties").openStream().readAllBytes()));
            githubManifest.putAll(githubProp);
            connection.disconnect();
        }catch (Throwable i) {
            Sentry.captureException(i);
            i.printStackTrace();
        }
    }

    public static void tryLoadLogMessage() {
        Log.infoTag("Ozone-MessageLogger", "Loading messageLog");

        try {
            loadMessageLog(messageLog);
            return;
        }catch (Throwable t) {
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

        try {
            SerializeData.dataOut(new SerializeData.DataArray<>(ChatOzoneFragment.messages.toArray()), messageLog);
        }catch (Throwable t) {
            Log.errTag("Ozone-MessageLogger", "Cant save " + Ozone.Desktop.Manifest.messageLog.getAbsolutePath());
            Log.errTag("Ozone-MessageLogger", t.toString());
            t.printStackTrace();
        }
        try {
            SerializeData.dataOut(new SerializeData.DataArray<>(ChatOzoneFragment.messages.toArray()), messageLogBackup);
        }catch (Throwable t) {
            Log.errTag("Ozone-MessageLogger", "Cant save " + Manifest.messageLogBackup.getAbsolutePath());
            Log.errTag("Ozone-MessageLogger", t.toString());
            t.printStackTrace();
        }

    }
}
