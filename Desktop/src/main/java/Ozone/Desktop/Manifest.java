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
            for (Object co : SerializeData.dataArrayIn(messageLog).get()) {
                if (!(co instanceof ChatOzoneFragment.ChatMessage)) continue;
                ChatOzoneFragment.ChatMessage cm = (ChatOzoneFragment.ChatMessage) co;
                ChatOzoneFragment.messages.add(cm);
                ChatOzoneFragment.messages.sort(chatMessage -> chatMessage.id);
            }
            return;
        } catch (Throwable t) {
            Log.errTag("Ozone-MessageLogger", "Cant load " + Ozone.Desktop.Manifest.messageLog.getAbsolutePath());
            Log.errTag("Ozone-MessageLogger", t.toString());
            t.printStackTrace();
        }
        Log.infoTag("Ozone-MessageLogger", "Loading messageLogBackup");

        try {
            for (Object co : SerializeData.dataArrayIn(messageLogBackup).get()) {
                if (!(co instanceof ChatOzoneFragment.ChatMessage)) continue;
                ChatOzoneFragment.ChatMessage cm = (ChatOzoneFragment.ChatMessage) co;
                ChatOzoneFragment.messages.add(cm);
                ChatOzoneFragment.messages.sort(chatMessage -> chatMessage.id);
            }
        } catch (Throwable t) {
            Log.errTag("Ozone-MessageLogger", "Cant load " + Manifest.messageLogBackup.getAbsolutePath());
            Log.errTag("Ozone-MessageLogger", t.toString());
            t.printStackTrace();
        }
    }

    public static void trySaveLogMessage() {
        Log.infoTag("Ozone-MessageLogger", "Saving messageLog");

        try {
            SerializeData.dataOut(new SerializeData.DataArray<>(ChatOzoneFragment.messages.toArray()), messageLog);
        } catch (Throwable t) {
            Log.errTag("Ozone-MessageLogger", "Cant save " + Ozone.Desktop.Manifest.messageLog.getAbsolutePath());
            Log.errTag("Ozone-MessageLogger", t.toString());
            t.printStackTrace();
        }
        try {
            SerializeData.dataOut(new SerializeData.DataArray<>(ChatOzoneFragment.messages.toArray()), messageLogBackup);
        } catch (Throwable t) {
            Log.errTag("Ozone-MessageLogger", "Cant save " + Manifest.messageLogBackup.getAbsolutePath());
            Log.errTag("Ozone-MessageLogger", t.toString());
            t.printStackTrace();
        }

    }
}
