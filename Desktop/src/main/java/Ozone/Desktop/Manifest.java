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

import java.io.File;

public class Manifest {
    public static File messageLogFolder = new File(Atom.Manifest.currentFolder, "Ozone/");
    public static File messageLog = new File(messageLogFolder, "MessageLogArr.dat");
    public static File messageLogBackup = new File(messageLogFolder, "BackupMessageLogArr.dat");

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
