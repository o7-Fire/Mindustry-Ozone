/*
 * Copyright 2021 Itzbenz
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

package Ozone.Desktop.Bootstrap;


import Ozone.Desktop.Propertied;
import Ozone.Desktop.Swing.Splash;
import Ozone.Version;
import io.sentry.Scope;
import io.sentry.Sentry;
import io.sentry.protocol.User;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SharedBootstrap {
	
	public static LibraryLoader libraryLoader;
	public static boolean customBootstrap, standalone, debug = System.getProperty("intellij.debug.agent") != null || System.getProperty("debug") != null || System.getProperty("ozoneTest") != null;
	public static long startup = System.currentTimeMillis();
	public static final String bootstrap = "SharedBootstrap 2.7", mainClass;
	private static ArrayList<String> loadedList = new ArrayList<>();
	private static Splash splash = null;

	static {
		System.out.println(bootstrap + (debug ? "[Debug]" : ""));
		try {
			URL u = ClassLoader.getSystemResource("gif/loading.gif");
			splash = new Splash(u);
			splash.setLabel(bootstrap);
		}catch (Throwable ignored) {}
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		if (trace.length > 0) mainClass = trace[trace.length - 1].getClassName();
		else mainClass = null;
		setSplash("Configuring Sentry");
		initSentry();
		Sentry.configureScope(SharedBootstrap::registerSentry);
	}
	
	public static void initSentry() {
		Sentry.init(options -> {
			options.setDsn("https://cd76eb6bd6614c499808176eaaf02b0b@o473752.ingest.sentry.io/5509036");
			options.setRelease(Version.core + ":" + Version.desktop);
			options.setDebug(debug);
			options.setTracesSampleRate(1.0);
			options.setEnvironment(Propertied.Manifest.getOrDefault("VHash", "no").equals("unspecified") ? "dev" : "release");
			if (System.getProperty("ozoneTest") != null) options.setEnvironment("test");
		}, true);
	}
	
	private static void moduleCheck(String name) {
		if (loadedList.contains(name))
			throw new RuntimeException(new IllegalStateException("Module: " + name + " already loaded"));
		loadedList.add(name);
	}
	
	public static void classloaderNoParent() {
		setSplash("New LibraryLoader");
		SharedBootstrap.libraryLoader = new LibraryLoader(new URL[]{SharedBootstrap.class.getProtectionDomain().getCodeSource().getLocation()}, null);
	}
	
	protected static void setSplash(String t) {
		if (splash != null) splash.setLabel(t);
		System.out.println(t);
		Sentry.addBreadcrumb(t);
	}
	
	public static void loadMindustry(List<String> args) throws MalformedURLException {
		moduleCheck("Mindustry.jar");
		File mindustryJar = null;
		
		if (System.getProperty("MindustryExecutable") != null)
			mindustryJar = new File(System.getProperty("MindustryExecutable"));
		else if (!args.isEmpty()) mindustryJar = new File(args.get(0));
		
		if (mindustryJar != null && mindustryJar.exists()) SharedBootstrap.libraryLoader.addURL(mindustryJar);
		else {
			System.out.println("No Mindustry jar found, using online resource");
			String version = Propertied.Manifest.get("MindustryVersion");
			if (version == null) throw new NullPointerException("MindustryVersion not found in property");
			SharedBootstrap.libraryLoader.addURL(new URL("https://github.com/Anuken/Mindustry/releases/download/" + version + "/Mindustry.jar"));
			SharedBootstrap.standalone = true;
		}
	}
	
	public static void registerSentry(Scope scope) {
		try {
			scope.setTag("Ozone.Desktop.Version", Version.desktop);
			scope.setTag("Ozone.Core.Version", Version.core);
			scope.setTag("Operating.System", System.getProperty("os.name") + " x" + System.getProperty("sun.arch.data.model"));
			scope.setTag("Java.Version", System.getProperty("java.version"));
			try {
				User u = new User();
				long l = ByteBuffer.wrap(System.getenv().toString().getBytes()).getLong();// ? cant reverse it to full byte array
				u.setId(l + "");
				scope.setUser(u);//easier to filter asshole
			}catch (Throwable t) {
				Sentry.captureException(t);
			}
			for (Map.Entry<String, String> e : Propertied.Manifest.entrySet())
				scope.setTag(e.getKey(), e.getValue());
		}catch (Throwable t) {
			t.printStackTrace();
			Sentry.captureException(t);
		}
	}
	
	protected static void loadAtomic() throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		moduleCheck("Atomic");
		setSplash("Loading Atomic Library");
		ArrayList<String> se = (ArrayList<String>) libraryLoader.loadClass("Main.LoadAtom").getMethod("main", String[].class).invoke(null, (Object) new String[0]);
		ArrayList<URL> ur = new ArrayList<>();
		for (String s : se) ur.add(new URL(s));
		libraryLoader.addURL(ur);
	}
	
	public static void load(Dependency.Type type) throws IOException {
		moduleCheck(type.name());
		Dependency.load();
		setSplash("Loading " + type.name() + " Library");
		ArrayList<URL> h = new ArrayList<>();
		for (Dependency d : Dependency.dependencies) {
			if (!d.type.equals(type)) continue;
			h.add(new URL(d.getDownload()));
		}
		libraryLoader.addURL(h);
		Dependency.save();
	}
	
	public static void loadRuntime() throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		load(Dependency.Type.runtime);
		loadAtomic();
	}
	
	public static void loadClasspath() throws MalformedURLException {
		moduleCheck("Classpath");
		setSplash("Loading " + "Classpath" + " Library");
		for (String s : System.getProperty("java.class.path").split(System.getProperty("os.name").toUpperCase().contains("WIN") ? ";" : ":"))
			libraryLoader.addURL(new File(s));
	}
	
	public static void requireDisplay() {
		if (GraphicsEnvironment.isHeadless())
			throw new RuntimeException(new IllegalStateException("This operation require GraphicsEnvironment"));
	}
	
	public static void loadMain(String classpath, String[] arg) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		setSplash("Finished Loading Bootstrap");
		if (splash != null) {
			splash.setVisible(false);
			splash.dispose();
			splash = null;
		}
		SharedBootstrap.libraryLoader.loadClass(classpath).getMethod("main", String[].class).invoke(null, (Object) arg);
	}
	
	
}
