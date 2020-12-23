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

package Ozone.Desktop.Bootstrap;


import Ozone.Desktop.Propertied;
import Ozone.Watcher.Version;
import io.sentry.Scope;
import io.sentry.Sentry;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;


public class SharedBootstrap {
	
	public static LibraryLoader libraryLoader;
	public static boolean customBootstrap, standalone, debug = System.getProperty("intellij.debug.agent") != null || System.getProperty("debug") != null;
	public static long startup = System.currentTimeMillis();
	private static boolean runtime, classpath, atomic, compile;
	private static final String bootstrap = "SharedBootstrap 2.5";
	private static Splash splash = null;
	
	static {
		System.out.println(bootstrap);
		try {
			URL u = ClassLoader.getSystemResource("gif/loading.gif");
			splash = new Splash(u);
			splash.setLabel(bootstrap);
		}catch (Throwable ignored) {}
		setSplash("Initializing Sentry");
		Sentry.init(options -> {
			options.setDsn("https://cd76eb6bd6614c499808176eaaf02b0b@o473752.ingest.sentry.io/5509036");
			options.setRelease("Ozone." + Version.semantic + ":" + "Desktop." + Settings.Version.semantic);
			options.setEnvironment(Propertied.Manifest.getOrDefault("VHash", "no").startsWith("v") ? "release" : "dev");
		});
		setSplash("Configuring Sentry Scope");
		Sentry.configureScope(SharedBootstrap::registerSentry);
	}
	
	public static void classloaderNoParent() {
		setSplash("New LibraryLoader");
		SharedBootstrap.libraryLoader = new LibraryLoader(new URL[]{SharedBootstrap.class.getProtectionDomain().getCodeSource().getLocation()}, null);
	}
	
	protected static void setSplash(String t) {
		if (splash != null) {
			splash.setLabel(t);
			System.out.println(t);
		}
	}
	
	public static void registerSentry(Scope scope) {
		scope.setTag("Ozone.Desktop.Version", Settings.Version.semantic);
		scope.setTag("Ozone.Core.Version", Version.semantic);
		scope.setTag("Operating.System", System.getProperty("os.name") + " x" + System.getProperty("sun.arch.data.model"));
		scope.setTag("Java.Version", System.getProperty("java.version"));
		for (Map.Entry<String, String> e : Propertied.Manifest.entrySet())
			scope.setTag(e.getKey(), e.getValue());
	}
	
	protected static void loadAtomic() throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		if (atomic) throw new IllegalStateException("Atom dependency already loaded");
		setSplash("Loading Atomic Library");
		atomic = true;
		ArrayList<String> se = (ArrayList<String>) libraryLoader.loadClass("Main.LoadAtom").getMethod("main", String[].class).invoke(null, (Object) new String[0]);
		ArrayList<URL> ur = new ArrayList<>();
		for (String s : se) ur.add(new URL(s));
		libraryLoader.addURL(ur);
	}
	
	public static void load(Dependency.Type type) throws IOException {
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
		if (runtime) throw new IllegalStateException("Runtime dependency already loaded");
		runtime = true;
		load(Dependency.Type.runtime);
		loadAtomic();
	}
	
	public static void loadClasspath() throws MalformedURLException {
		if (classpath) throw new IllegalStateException("Classpath dependency already loaded");
		setSplash("Loading " + "Classpath" + " Library");
		classpath = true;
		for (String s : System.getProperty("java.class.path").split(System.getProperty("os.name").toUpperCase().contains("WIN") ? ";" : ":"))
			libraryLoader.addURL(new File(s));
	}
	
	public static void loadMain(String classpath, String[] arg) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		setSplash("Finished");
		splash.setVisible(false);
		splash.dispose();
		splash = null;
		SharedBootstrap.libraryLoader.loadClass(classpath).getMethod("main", String[].class).invoke(null, (Object) arg);
	}
	
	
}
