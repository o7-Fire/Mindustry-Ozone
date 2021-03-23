/*******************************************************************************
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
 ******************************************************************************/

package Ozone.Desktop.Bootstrap;


import Atom.Manifest;
import Ozone.Desktop.Swing.Splash;
import Ozone.Propertied;
import Shared.SharedBoot;
import io.sentry.Sentry;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class DesktopBootstrap {
	
	public static OzoneLoader ozoneLoader;
	public static boolean customBootstrap, debug = SharedBoot.debug;
	public static final String bootstrap = "DesktopBootstrap 3.2", mainClass;
	private static ArrayList<String> loadedList = new ArrayList<>();
	private static Splash splash = null;
	
	static {
		System.out.println(bootstrap + (debug ? " [Debug]" : ""));
		if (!debug) try {
			URL u = Manifest.internalRepo.getResource("gif/loading.gif");
			splash = new Splash(u);
			splash.setLabel(bootstrap);
		}catch (Throwable ignored) {}
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		if (trace.length > 0) mainClass = trace[trace.length - 1].getClassName();
		else mainClass = null;
		SharedBoot.type = "Ozone-Desktop";
		setSplash("Configuring Sentry");
		initSentry();
	}
	
	public static void initSentry() {
		SharedBoot.initSentry();
	}
	
	private static void moduleCheck(String name) {
		if (loadedList.contains(name))
			throw new RuntimeException(new IllegalStateException("AbstractModule: " + name + " already loaded"));
		loadedList.add(name);
	}
	
	public static void classloaderNoParent() {
		setSplash("New LibraryLoader");
		DesktopBootstrap.ozoneLoader = new OzoneLoader(new URL[]{DesktopBootstrap.class.getProtectionDomain().getCodeSource().getLocation()}, null);
	}
	
	protected static void setSplash(String t) {
		if (splash != null) splash.setLabel(t);
		System.out.println(t);
		Sentry.addBreadcrumb(t);
	}
	
	public static void loadMindustry() throws MalformedURLException {
		loadMindustry(new ArrayList<>());
	}
	
	public static void loadMindustry(List<String> args) throws MalformedURLException {
		moduleCheck("Mindustry.jar");
		File mindustryJar = null;
		
		if (System.getProperty("MindustryExecutable") != null)
			mindustryJar = new File(System.getProperty("MindustryExecutable"));
		else if (!args.isEmpty()) mindustryJar = new File(args.get(0));
		
		if (mindustryJar != null && mindustryJar.exists()) DesktopBootstrap.ozoneLoader.addURL(mindustryJar);
		else {
			System.out.println("No Mindustry jar found");
			String version = Propertied.Manifest.get("MindustryVersion");
			if (version == null) throw new NullPointerException("MindustryVersion not found in property");
			URL u = new URL("https://github.com/Anuken/Mindustry/releases/download/" + version + "/Mindustry.jar");
			DesktopBootstrap.ozoneLoader.addURL(u);
			System.out.println("Using: " + u.toString());
		}
	}
	
	
	public static void loadAtomic() throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		moduleCheck("Atomic");
		setSplash("Loading Atomic Library");
		//ArrayList<String> se = (ArrayList<String>) ozoneLoader.loadClass("Main.LoadAtom").getMethod("main", String[].class).invoke(null, (Object) new String[0]);
		ArrayList<String> se = Main.LoadAtom.main(new String[0]);
		ArrayList<URL> ur = new ArrayList<>();
		for (String s : se) ur.add(new URL(s));
		ozoneLoader.addURL(ur);
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
		ozoneLoader.addURL(h);
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
			ozoneLoader.addURL(new File(s));
	}
	
	public static void requireDisplay() {
		try {
			if (GraphicsEnvironment.isHeadless())
				throw new RuntimeException(new IllegalStateException("This operation require GraphicsEnvironment"));
		}catch (Throwable ignored) {
			throw new RuntimeException(new IllegalStateException("This operation require GraphicsEnvironment"));
		}
	}
	
	public static void loadMain(Class<?> main, String[] arg) throws Throwable {
		loadMain(main.getName(), arg);
	}
	
	public static void loadMain(String classpath, String[] arg) throws Throwable {
		setSplash("Finished Loading Bootstrap");
		if (splash != null) {
			splash.setVisible(false);
			splash.dispose();
			splash = null;
		}
		try {
			DesktopBootstrap.ozoneLoader.loadClass(classpath).getMethod("main", String[].class).invoke(null, (Object) arg);
		}catch (InvocationTargetException t) {
			throw (t.getCause() != null ? t.getCause() : t);
		}
		if (!DesktopBootstrap.debug) return;
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		for (Thread t : threadSet)
			if (!t.isDaemon()) setSplash(t.getId() + ". " + t.getName() + " alive ? " + t.isAlive());
	}
	
	
}
