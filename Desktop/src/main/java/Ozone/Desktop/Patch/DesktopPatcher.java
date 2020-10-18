package Ozone.Desktop.Patch;

import Atom.Manifest;
import Ozone.Desktop.Pre.DownloadSwing;
import Ozone.Event.Internal;
import arc.Events;
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
                sb.append("Additional library need to be downloaded").append("\n");
                Manifest.library.forEach(library -> {
                    if (library.downloaded()) return;
                    sb.append("-").append(library.name).append(library.version).append("\n");
                });
                Vars.ui.showCustomConfirm("Download", sb.toString(), "Download", "Later", () -> {
                    Manifest.library.forEach(library -> {
                        Thread e = new Thread(() -> {
                            try {
                                DownloadSwing d = new DownloadSwing(new URL(library.link), library.jar);
                                d.display();
                                d.run();
                            } catch (Throwable t) {
                                Vars.ui.showErrorMessage("Failed to download " + library.name + "\n" + t.toString());
                                t.printStackTrace();
                            }
                        });
                        e.setDaemon(true);
                        e.start();
                    });
                }, () -> {
                });
            }
        });
    }
}
