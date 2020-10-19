package Ozone.Desktop.Patch;

import Atom.File.SerializeData;
import Atom.Manifest;
import Ozone.Desktop.Pre.DownloadSwing;
import Ozone.Event.Internal;
import Ozone.Interface;
import Ozone.Patch.ChatOzoneFragment;
import Settings.Desktop;
import arc.Events;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType;

import java.net.URL;

public class DesktopPatcher {

    public static void register() {
        Ozone.Manifest.settings.add(Desktop.class);
        Events.run(Internal.Init.CommandsRegister, Commands::Init);
        Events.run(Internal.Init.TranslationRegister, Translation::Init);
        Events.run(Internal.Init.PatchRegister, () -> {
            Log.infoTag("Ozone", "Patching DesktopInput");
            Vars.control.input = new DesktopInput();
        });
        Events.on(EventType.ClientLoadEvent.class, s -> {
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
            if (Desktop.logMessage) {
                if (Ozone.Desktop.Manifest.messageLog.exists()) {//try to load
                    try {
                        for (Object co : SerializeData.dataArrayIn(Ozone.Desktop.Manifest.messageLog).get()) {
                            ChatOzoneFragment.ChatMessage cm = (ChatOzoneFragment.ChatMessage) co;
                            ChatOzoneFragment.messages.add(cm);
                        }
                    } catch (Throwable t) {
                        Log.errTag("Ozone-MessageLogger", "Cant load " + Ozone.Desktop.Manifest.messageLog.getAbsolutePath());
                        Log.errTag("Ozone-MessageLogger", t.toString());
                        t.printStackTrace();
                    }
                }
            }
        });

    }
}
