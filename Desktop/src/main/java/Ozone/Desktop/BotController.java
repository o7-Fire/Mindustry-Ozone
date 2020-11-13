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

import Atom.Utility.Random;
import Bot.BotClient;
import Bot.BotInterface;
import Bot.ServerInterface;
import mindustry.Vars;
import mindustry.desktop.DesktopLauncher;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class BotController {
    public static int port = 7070;
    public static String base = Vars.player.name().isEmpty() ? "BotController" : Vars.player.name();
    public static ArrayList<BotClient> botClients = new ArrayList<>();


    public static void launchBot(String name) {
        for (BotClient c : botClients)
            if (c.name.equals(name)) throw new IllegalStateException(name + " already registered");
        BotClient pre = new BotClient(name);
        botClients.add(pre);
    }

    public static BotInterface connectToBot(BotClient b) throws RemoteException, NotBoundException {
        return b.connect();
    }

    public static Process launchBot(BotClient b) throws IOException {
        return b.launch();
    }

    public static HashMap<String, String> generateProp(BotClient b) {
        HashMap<String, String> h = new HashMap<>();
        h.put("ServerRegPort", String.valueOf(port));
        h.put("ServerRegName", base);
        h.put("MindustryExecutable", DesktopLauncher.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        h.put("RegPort", String.valueOf(b.getPort()));
        h.put("RegName", b.rmiName);
        h.put("BotName", b.name);
        h.put("BotID", String.valueOf(Random.getInt(Integer.MAX_VALUE)));
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
