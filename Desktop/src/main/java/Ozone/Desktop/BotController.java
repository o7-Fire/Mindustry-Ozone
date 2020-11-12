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

import Bot.Bot;
import Bot.ServerInterface;
import Main.Ozone;
import Premain.BotEntryPoint;
import mindustry.Vars;
import mindustry.desktop.DesktopLauncher;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BotController {
    public static int port = 7070;
    public static String base = Vars.player.name().isEmpty() ? "BotController" : Vars.player.name();
    public static ArrayList<Bot> bots = new ArrayList<>();

    public static Process launchBot(HashMap<String, String> property) throws IOException {
        StringBuilder cli = new StringBuilder();
        cli.append(System.getProperty("java.home")).append(File.separator).append("bin").append(File.separator).append("java ");
        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            cli.append(jvmArg).append(" ");
        }
        for (Map.Entry<String, String> p : property.entrySet())
            cli.append("-D").append(p.getKey()).append("=").append(p.getValue()).append(" ");
        cli.append("-cp ");
        cli.append(Ozone.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        cli.append(" ");
        cli.append(BotEntryPoint.class.getTypeName()).append(" ");
        return Runtime.getRuntime().exec(cli.toString());
    }

    public static HashMap<String, String> generateProp(int p, String regName) {
        HashMap<String, String> h = new HashMap<>();
        h.put("ServerRegPort", String.valueOf(p));
        h.put("ServerRegName", base);
        h.put("MindustryExecutable", DesktopLauncher.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        h.put("RegPort", String.valueOf(port));
        h.put("RegName", regName);
        return h;
    }

    public static class ServerImplementation implements ServerInterface {
        @Override
        public String name() throws RemoteException {
            return Vars.player.name();
        }

        @Override
        public boolean alive() throws RemoteException {
            return true;
        }
    }
}
