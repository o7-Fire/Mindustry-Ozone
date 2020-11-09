package Ozone.Desktop;

import Main.OzoneMindustry;
import arc.util.Log;
import arc.util.OS;
import arc.util.Strings;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import mindustry.ClientLauncher;
import mindustry.Vars;
import mindustry.gen.Groups;

import static mindustry.Vars.state;
import static mindustry.Vars.ui;

public class OzoneLauncher extends ClientLauncher {
    public static final String discordID = "775205026560999456";
    public static String[] args;
    boolean discord = OS.is64Bit && !OS.isARM && !OS.hasProp("nodiscord");

    public OzoneLauncher(String[] arg) {
        args = arg;
        if (discord) {
            DiscordRPC.INSTANCE.Discord_Initialize(discordID, null, true, null);
            Log.info("Initialized Discord rich presence.");
            Runtime.getRuntime().addShutdownHook(new Thread(DiscordRPC.INSTANCE::Discord_Shutdown));
        }

    }

    @Override
    public void updateRPC() {
        if (!discord) return;

        //common elements they each share
        boolean inGame = Vars.state.isGame();
        String gameMapWithWave = "Unknown Map";
        String gameMode = "";
        String gamePlayersSuffix = "";
        String uiState = "";

        if (inGame) {
            //TODO implement nice name for sector
            gameMapWithWave = Strings.capitalize(Strings.stripColors(Vars.state.map.name()));

            if (Vars.state.rules.waves) {
                gameMapWithWave += " | Wave " + state.wave;
            }
            gameMode = state.rules.pvp ? "PvP" : state.rules.attackMode ? "Attack" : "Survival";
            if (Vars.net.active() && Groups.player.size() > 1) {
                gamePlayersSuffix = " | " + Groups.player.size() + " Players";
            }
        } else {
            if (ui.editor != null && ui.editor.isShown()) {
                uiState = "In Editor";
            } else if (ui.planet != null && ui.planet.isShown()) {
                uiState = "In Launch Selection";
            } else {
                uiState = "In Menu";
            }
        }

        if (discord) {
            DiscordRichPresence presence = new DiscordRichPresence();
            presence.startTimestamp = OzoneMindustry.start;
            if (inGame) {
                presence.state = gameMode + gamePlayersSuffix;
                presence.details = gameMapWithWave;
                if (state.rules.waves) {
                    presence.largeImageText = "Wave " + state.wave;
                }
            } else {
                presence.state = uiState;
            }

            //presence.largeImageKey = "logo";
            DiscordRPC.INSTANCE.Discord_UpdatePresence(presence);
        }
    }
}
