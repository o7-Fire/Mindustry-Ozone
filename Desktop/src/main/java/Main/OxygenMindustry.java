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

import Bot.Interface.Shared.BotInterface;
import Bot.Interface.Shared.ServerInterface;
import Ozone.Commands.Task.Task;
import Ozone.Commands.TaskInterface;
import Ozone.Desktop.Bootstrap.SharedBootstrap;
import arc.Application;
import arc.Core;
import arc.Events;
import arc.Files;
import arc.backend.sdl.SdlConfig;
import arc.files.Fi;
import arc.math.Rand;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.OS;
import arc.util.serialization.Base64Coder;
import io.sentry.Sentry;
import mindustry.ClientLauncher;
import mindustry.Vars;
import mindustry.core.NetClient;
import mindustry.game.EventType;
import mindustry.gen.Call;

import java.io.File;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.function.Consumer;

import static Bot.Manifest.*;
import static arc.Core.app;
import static arc.util.Log.format;
import static arc.util.Log.logger;
import static mindustry.Vars.appName;

public class OxygenMindustry extends ClientLauncher implements BotInterface {
	// protected static String[] tags = {"&lc&fb[D]&fr", "&lb&fb[I]&fr", "&ly&fb[W]&fr", "&lr&fb[E]", ""};
	public static Application h;
	public static volatile boolean bot;
	protected static String[] tags = {"[green][D][]", "[royal][I][]", "[yellow][W][]", "[scarlet][E][]", ""};
	protected static Seq<String> dont = Seq.with("MindustryExecutable", "ServerRegPort", "ServerRegName", "RegPort", "RegName", "BotID", "BotName");
	protected static DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"), autosaveDate = DateTimeFormatter.ofPattern("MM-dd-yyyy_HH-mm-ss");
	String uuid;
	
	public OxygenMindustry() {
		byte[] result = new byte[8];
		new Rand().nextBytes(result);
		uuid = new String(Base64Coder.encode(result));
	}
	
	public static void main(String[] args) {
		bot = true;
		SharedBootstrap.customBootstrap = true;
		logger();
		Log.info("Logger Online");
		preCheck();
		preInit();
		Log.info("Creating Bot");
		oxygen = new OxygenMindustry();
		h = new SdlAppWithExtraStep(oxygen, new SdlConfig() {
			{
				this.title = "Mindustry-Oxygen";
				this.maximized = true;
				this.stencil = 8;
				this.width = 900;
				this.height = 700;
				this.setWindowIcon(Files.FileType.internal, "icons/rect10.png");
			}
		});
		Log.info(h.getType().name() + " Bot Exited");
		
	}
	
	private static void watcher() {
		Thread t = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(500);
					serverInterface.alive();
				}catch (Throwable e) {
					Log.err(e);
					break;
				}
			}
			oxygen.kill();
		});
		t.setDaemon(true);
		t.start();
	}
	
	public static void preInit() {
		appName = "Oxygen-Mindustry-Bot";
		Events.on(EventType.ClientCreateEvent.class, s -> {
			logger();
		});
	}
	
	public static void logger() {
		
		logger = (level1, text) -> {
			String result = "[" + dateTime.format(LocalDateTime.now()) + "] " + format(tags[level1.ordinal()] + " " + text + "[white]");
			System.out.println(result);
			Sentry.addBreadcrumb(text, level1.name());
		};
	}
	
	public static void preCheck() {
		Log.info("Pre Check");
		Seq<String> s = new Seq<>(dont);
		for (String e : System.getProperties().keySet().toArray(new String[0])) {
			if (s.contains(e)) s.remove(e);
		}
		if (!s.isEmpty()) throw new IllegalArgumentException(s.toString() + " is null in system property");
	}
	
	public static void createInterface(BotInterface b) throws AlreadyBoundException, RemoteException {
		Log.info("Creating Bot Interface");
		int i = Integer.parseInt(System.getProperty("RegPort"));
		String interfaceName = System.getProperty("RegName");
		registry = LocateRegistry.createRegistry(i);
		BotInterface stub = (BotInterface) UnicastRemoteObject.exportObject(b, i);
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
	public void init() {
		Fi f = new Fi(OS.getAppDataDirectoryString(appName)).child("mods/");
		f.mkdirs();
		f = f.child("Ozone.jar");
		File ozone = new File(OxygenMindustry.class.getProtectionDomain().getCodeSource().getLocation().getFile());
		if (!f.exists() && ozone.isFile()) new Fi(ozone).copyTo(f);
		try {
			createInterface(oxygen);
			connectToServer();
			watcher();
		}catch (Throwable t) {
			throw new RuntimeException("Failed to establish RMI connection", t);
		}
		Events.run(EventType.Trigger.update, () -> {
		
		});
		Core.settings.put("name", System.getProperty("BotName"));
		super.init();
	}
	
	@Override
	public void addTask(Task t) throws RemoteException {
		addTask(t, null);
	}
	
	@Override
	public void addTask(Task t, Consumer<Object> onEnd) {
		TaskInterface.addTask(t, onEnd);
	}
	
	@Override
	public void clearTask() {
		TaskInterface.reset();
	}
	
	@Override
	public ArrayList<Task> getTask() {
		ArrayList<Task> task = new ArrayList<>();
		for (Task t : TaskInterface.taskQueue) task.add(t);
		return task;
	}
	
	@Override
	public void connect(String ip, int port) {
		NetClient.connect(ip, port);
	}
	
	@Override
	public void sendChat(String s) {
		if (Vars.net.active()) Call.sendChatMessage(s);
	}
	
	@Override
	public String getUUID() {
		return uuid;
	}
	
	@Override
	public boolean alive() {
		return app.isHeadless();
	}
	
	@Override
	public int getID() {
		return Integer.parseInt(System.getProperty("BotID"));
	}
	
	@Override
	public void kill() {
		Log.info("SIGKILL Signal Received");
		app.exit();
	}
	
	@Override
	public String getType() {
		return app.getType().name();
	}
}
