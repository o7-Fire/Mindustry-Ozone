package Ozone.Desktop.Patch;

import Ozone.Event.Internal;
import arc.Events;

public class DesktopPatcher {
    public static void register() {
        Events.run(Internal.Init.CommandsRegister, Commands::Init);
        Events.run(Internal.Init.TranslationRegister, Translation::Init);
    }
}
