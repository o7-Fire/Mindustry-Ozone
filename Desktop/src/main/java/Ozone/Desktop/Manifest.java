package Ozone.Desktop;

import Atom.File.SerializeData;
import Ozone.Patch.ChatOzoneFragment;
import arc.util.Log;

import java.io.File;

public class Manifest {
    public static File messageLog = new File("MessageLogArr.dat");
    public static File messageLogBackup = new File("BackupMessageLogArr.dat");

    public static void tryLoadLogMessage() {
        try {
            messageLog.deleteOnExit();
            for (Object co : SerializeData.dataArrayIn(messageLog).get()) {
                ChatOzoneFragment.ChatMessage cm = (ChatOzoneFragment.ChatMessage) co;
                ChatOzoneFragment.messages.add(cm);
                ChatOzoneFragment.messages.sort(chatMessage -> chatMessage.id);
            }
        } catch (Throwable t) {
            Log.errTag("Ozone-MessageLogger", "Cant load " + Ozone.Desktop.Manifest.messageLog.getAbsolutePath());
            Log.errTag("Ozone-MessageLogger", t.toString());
            t.printStackTrace();
        }
    }

    public static void trySaveLogMessage() {
        try {
            SerializeData.dataOut(new SerializeData.DataArray<>(ChatOzoneFragment.messages.toArray()), messageLog);
            SerializeData.dataOut(new SerializeData.DataArray<>(ChatOzoneFragment.messages.toArray()), messageLogBackup);
        } catch (Throwable t) {
            Log.errTag("Ozone-MessageLogger", "Cant save " + Ozone.Desktop.Manifest.messageLog.getAbsolutePath());
            Log.errTag("Ozone-MessageLogger", t.toString());
            t.printStackTrace();
        }
    }
}
