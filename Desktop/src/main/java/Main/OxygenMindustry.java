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

package Main;

import Bot.BotInterface;
import Bot.Interface.StubCore;
import Bot.Interface.UIHeadless;
import Bot.ServerInterface;
import Ozone.Desktop.SharedBootstrap;
import arc.ApplicationListener;
import arc.Core;
import arc.Events;
import arc.backend.headless.HeadlessApplication;
import arc.files.Fi;
import arc.math.Rand;
import arc.struct.Seq;
import arc.util.I18NBundle;
import arc.util.Log;
import arc.util.serialization.Base64Coder;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.core.NetClient;
import mindustry.core.Platform;
import mindustry.core.World;
import mindustry.game.EventType;
import mindustry.gen.Player;
import mindustry.input.Binding;
import mindustry.io.JsonIO;
import mindustry.net.Net;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static arc.Core.*;
import static arc.util.Log.format;
import static arc.util.Log.logger;
import static mindustry.Vars.*;

public class OxygenMindustry implements ApplicationListener, Platform, BotInterface {
    protected static String[] tags = {"&lc&fb[D]&fr", "&lb&fb[I]&fr", "&ly&fb[W]&fr", "&lr&fb[E]", ""};
    public static HeadlessApplication h;
    protected static Seq<String> dont = Seq.with("MindustryExecutable", "ServerRegPort", "ServerRegName", "RegPort", "RegName", "BotID");
    public static BotInterface botInterface;
    public static Registry registry;
    public static ServerInterface serverInterface;
    public static OxygenMindustry o;
    protected static DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"), autosaveDate = DateTimeFormatter.ofPattern("MM-dd-yyyy_HH-mm-ss");
    String uuid;

    public OxygenMindustry() {
        byte[] result = new byte[8];
        new Rand().nextBytes(result);
        uuid = new String(Base64Coder.encode(result));
    }

    public static void main(String[] args) throws IOException, NotBoundException, AlreadyBoundException {
        SharedBootstrap.customBootstrap = true;
        //preCheck();
        preInit();
        Log.info("Creating Headless Bot");
        o = new OxygenMindustry();
        h = new HeadlessApplication(o, t -> {
            t.printStackTrace();
            Sentry.captureException(t);
        });
        Log.info("Headless Bot Created");
        //createInterface(o);
        // connectToServer();
    }

    public static void preInit() {
        appName = "Oxygen-Mindustry-Headless";
        headless = true;
        Vars.net = new Net(platform.getNet());
        logger = (level1, text) -> {
            String result = "[" + dateTime.format(LocalDateTime.now()) + "] " + format(tags[level1.ordinal()] + " " + text + "&fr");
            System.out.println(result);
            Sentry.addBreadcrumb(text, level1.name());
        };
        Log.info("Logger Online");
    }

    public static void preCheck() {
        Log.info("Pre Check");
        Seq<String> s = new Seq<>(dont);
        for (String e : System.getProperties().keySet().toArray(new String[0])) {
            if (s.contains(e)) s.remove(e);
        }
        if (!s.isEmpty()) throw new RuntimeException(s.toString() + " is null in system property");
    }

    public static void createInterface(BotInterface b) throws AlreadyBoundException, RemoteException {
        Log.info("Creating Bot Interface");
        int i = Integer.parseInt(System.getProperty("RegPort"));
        String interfaceName = System.getProperty("RegName");
        registry = LocateRegistry.createRegistry(i);
        botInterface = b;
        BotInterface stub = (BotInterface) UnicastRemoteObject.exportObject(botInterface, i);
        registry.bind(interfaceName, stub);
        Log.info("Bot Interface Created on port: @ with name \"@\"", i, interfaceName);
    }

    public static void connectToServer() throws IOException, NotBoundException {
        Log.info("Connecting to server");
        int port = Integer.parseInt(System.getProperty("ServerRegPort"));
        Registry registry = LocateRegistry.getRegistry(port);
        serverInterface = (ServerInterface) registry.lookup(System.getProperty("ServerRegName"));
        Log.info("Connected to \"@\" at port @ with reg \"@\"", serverInterface.name(), port, System.getProperty("ServerRegName"));
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public void init() {
        loadSetting();
        platform = this;
        player = Player.create();
        netClient = new NetClient();
        try {
            ui = new UIHeadless();
            ui.init();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        logic = new StubCore.LogicTM();
        world = new World();
        state = new GameState();
    }

    @Override
    public void update() {
        Events.fire(EventType.Trigger.update);
    }

    public void loadSetting() {
        settings.setJson(JsonIO.json());
        settings.setDataDirectory(Core.files.local("bots/config/"));
        loadSettings();
        settings.defaults("locale", "default", "blocksync", true);
        keybinds.setDefaults(Binding.values());
        settings.setAutosave(true);
        settings.load();
        Fi handle = Core.files.internal("bundles/bundle");
        Locale locale;
        String loc = settings.getString("locale");
        if (loc.equals("default")) {
            locale = Locale.getDefault();
        } else {
            Locale lastLocale;
            if (loc.contains("_")) {
                String[] split = loc.split("_");
                lastLocale = new Locale(split[0], split[1]);
            } else {
                lastLocale = new Locale(loc);
            }

            locale = lastLocale;
        }

        Locale.setDefault(locale);
        Core.bundle = I18NBundle.createBundle(handle, locale);
    }


    @Override
    public boolean alive() throws RemoteException {
        return app.isHeadless();
    }

    @Override
    public int getID() throws RemoteException {
        return Integer.parseInt(System.getProperty("BotID"));
    }

    @Override
    public void kill() throws RemoteException {
        Log.info("SIGKILL Signal Received");
        app.exit();
    }

    @Override
    public String getType() throws RemoteException {
        return app.getType().toString();
    }
}
