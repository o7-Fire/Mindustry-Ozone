package Ozone.Desktop.Patch;

import Atom.Manifest;
import Ozone.Desktop.Pre.DownloadSwing;
import Ozone.Event.Internal;
import Ozone.Interface;
import arc.Events;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType;

import java.net.URL;

public class DesktopPatcher {
    public static void register() {
        Events.run(Internal.Init.CommandsRegister, Commands::Init);
        Events.run(Internal.Init.TranslationRegister, Translation::Init);
        Events.on(EventType.ClientLoadEvent.class, s -> {
            long need = Manifest.library.stream().filter(library -> !library.downloaded()).count();
            if (need == 0) return;
            if (!Vars.disableUI) {
                StringBuilder sb = new StringBuilder();
                sb.append("Additional library need to be downloaded (restart required)").append("\n");
                Manifest.library.forEach(library -> {
                    if (library.downloaded()) return;
                    sb.append("-").append(library.name).append(library.version).append("\n");
                });
                Vars.ui.showCustomConfirm("Download", sb.toString(), "Download", "Later", () -> {
                    for (Manifest.Library library : Manifest.library) {
                        try {
                            DownloadSwing d = new DownloadSwing(new URL(library.link), library.jar);
                            d.display();
                            d.run();
                            if (library.jar.exists()) {
                                Log.infoTag("Ozone", library.name + ":" + library.version + " downloaded");
                            }
                        } catch (Throwable t) {
                            Vars.ui.showErrorMessage("Failed to download " + library.name + "\n" + t.toString());
                            t.printStackTrace();
                        }
                    }
                    Interface.restart();
                }, () -> {
                });
            }
        });
    }
}
