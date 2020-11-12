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
import Bot.ServerInterface;
import arc.ApplicationListener;
import arc.backend.headless.HeadlessApplication;
import arc.struct.Seq;
import arc.util.Log;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.core.Platform;
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

import static arc.util.Log.format;
import static arc.util.Log.logger;
import static mindustry.Vars.platform;

public class OxygenMindustry implements ApplicationListener {
    protected static String[] tags = {"&lc&fb[D]&fr", "&lb&fb[I]&fr", "&ly&fb[W]&fr", "&lr&fb[E]", ""};
    protected static DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"),
            autosaveDate = DateTimeFormatter.ofPattern("MM-dd-yyyy_HH-mm-ss");
    protected static HeadlessRemoteApplication h;
    protected static BotInterface botInterface;
    protected static Registry registry;
    protected static ServerInterface serverInterface;


    public static void main(String[] args) throws IOException, NotBoundException, AlreadyBoundException {
        preCheck();
        Vars.platform = new Platform() {
        };
        Vars.net = new Net(platform.getNet());
        logger = (level1, text) -> {
            String result = "[" + dateTime.format(LocalDateTime.now()) + "] " + format(tags[level1.ordinal()] + " " + text + "&fr");
            System.out.println(result);
            Sentry.addBreadcrumb(text, level1.name());
        };
        Log.info("Logger Online");
        Log.info("Creating Headless Bot");
        h = new HeadlessRemoteApplication(new OxygenMindustry());
        Log.info("Headless Bot Created");
        createInterface();
        createClient();
    }

    public static void preCheck() {
        Log.info("Pre Check");
        Seq<String> s = Seq.with("MindustryExecutable", "ServerRegPort", "ServerRegName", "RegPort", "RegName");
        for (String e : System.getProperties().keySet().toArray(new String[0])) {
            if (s.contains(e)) s.remove(e);
        }
        if (!s.isEmpty()) throw new RuntimeException(s.toString() + " is null in system property");
    }

    public static void createInterface() throws AlreadyBoundException, RemoteException {
        Log.info("Creating Bot Interface");
        int i = Integer.parseInt(System.getProperty("RegPort"));
        String interfaceName = System.getProperty("RegName");
        registry = LocateRegistry.createRegistry(i);
        botInterface = new Interface(h);
        BotInterface stub = (BotInterface) UnicastRemoteObject.exportObject(botInterface, 0);
        registry.bind(interfaceName, stub);
        Log.info("Bot Interface Created on port: @ with name \"@\"", i, interfaceName);
    }

    public static void createClient() throws IOException, NotBoundException {
        Log.info("Connecting to server");
        Registry registry = LocateRegistry.getRegistry(System.getProperty("ServerRegPort"));
        serverInterface = (ServerInterface) registry.lookup(System.getProperty("ServerRegName"));
        Log.info("Connected to server: @", serverInterface.name());
    }

    @Override
    public void init() {

    }

    public static class HeadlessRemoteApplication extends HeadlessApplication {
        public HeadlessRemoteApplication(ApplicationListener listener) {
            super(listener);
        }
    }

    static class Interface implements BotInterface {
        HeadlessRemoteApplication app;

        public Interface(HeadlessRemoteApplication a) {
            app = a;
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
}
