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

package Ozone.Patch;

import Atom.Net.Request;
import Atom.Utility.Encoder;
import Atom.Utility.Pool;
import Atom.Utility.Utility;
import Ozone.Internal.AbstractModule;
import Ozone.Internal.InformationCenter;
import Ozone.Internal.Interface;
import Ozone.Propertied;
import Shared.SharedBoot;
import Shared.WarningHandler;
import Shared.WarningReport;
import arc.Core;
import arc.util.Log;
import mindustry.Vars;
import mindustry.core.Version;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static mindustry.Vars.ui;

public class Updater extends AbstractModule {
	
	public final static AtomicBoolean newRelease = new AtomicBoolean(false);
	private static volatile boolean init;
	public static volatile HashMap<String, String> releaseMap = null;
	public static Future<?> future;
	
	public static void sync() {
		if (init) return;
		init = true;
		Log.debug("[Ozone] Checking Update");
		
		
		try {
			HashMap<String, String> h = Encoder.parseProperty(getRelease(true).openStream());
			newRelease.set(latest(h));
			if (newRelease.get()) {
				releaseMap = h;
				new WarningReport("Latest Release Found: " + h.get("VHash")).setWhyItsAProblem("To fix bug and add new feature").setHowToFix("Go update blin").report();
				if (SharedBoot.isCore() && !Core.settings.getBoolOnce("Ozone-Update-" + h.get("Vhash")))
					Interface.showInfo("New release found, go to mods and reinstall ozone to update\n" + readMap(h));
			}else
				new WarningReport().setProblem("Latest Release Is Already Installed or Unavailable").setWhyItsAProblem("nope ?");
			
		}catch (Throwable t) {
			WarningHandler.handleMindustry(t);
			new WarningReport(t).setLevel(WarningReport.Level.warn).report();
		}
		
		
	}
	
	public static void showUpdateDialog() {
		if (releaseMap == null) {
			if (future.isDone()) ui.showInfo("¯\\_(ツ)_/¯\nCan't find a good update");
			else ui.showInfo("Wait lol, your internet suck");
			return;
		}
		
		StringBuilder rm = new StringBuilder();
		if (releaseMap != null) {
			readMap(rm, releaseMap);
		}
		if (releaseMap != null) {
			showNewRelease(rm);
		}
		
		
	}
	
	
	public static void update(URL url) {
		Vars.ui.loadfrag.show("Downloading");
		try {
			Request.download(url.toExternalForm(), InformationCenter.getCurrentJar());
			Vars.ui.loadfrag.hide();
		}catch (Throwable t) {
			Vars.ui.loadfrag.hide();
			Vars.ui.showException(t);
			WarningHandler.handleMindustry(t);
			
		}
	}
	
	public static boolean latest(Map<String, String> target) {
		Map<String, String> source = Propertied.Manifest;
		//Latest
		try {
			String sa = source.get("TimeMilis");
			String sb = target.get("TimeMilis");
			if (sa == null || sb == null) return false;
			long a = Long.parseLong(sa), b = Long.parseLong(sb);
			if (a == b) return false;
			if (a > b) return false;
		}catch (NumberFormatException t) {
			WarningHandler.handleProgrammerFault(t);
			
			return false;
		}
		//Compatibility
		try {
			int a = Version.build;
			String s = target.get("MindustryVersion");
			if (s == null) return false;
			s = s.substring(1);
			if (s.contains(".")) s = s.substring(0, s.indexOf('.'));
			int b = Integer.parseInt(s);
			if (b != a) return false;
		}catch (NumberFormatException t) {
			WarningHandler.handleProgrammerFault(t);
			return false;
		}
		return true;
	}
	
	public static String readMap(Map<?, ?> m) {
		StringBuilder sb = new StringBuilder();
		readMap(sb, m);
		return sb.toString();
	}
	
	public static void readMap(StringBuilder sb, Map<?, ?> m) {
		for (Map.Entry<?, ?> s : m.entrySet())
			sb.append(s.getKey().toString()).append(": ").append(s.getValue().toString()).append("\n");
	}
	
	public static Future<?> async() {
		return Pool.submit(Updater::sync);
	}
	
	private static void showNewRelease(StringBuilder sb) {
		ui.showConfirm("New Release", "A new compatible release appeared\n" + sb.toString(), () -> Updater.update(Updater.getRelease(SharedBoot.type + ".jar")));
	}
	
	
	public static URL getRelease(String type) {
		try {
			return new URL(getRelease(false).toExternalForm() + type);
		}catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static URL getRelease(boolean manifest) {
		try {
			HashMap<String, String> releaseManifest = Encoder.parseProperty(new URL("https://raw.githubusercontent.com/o7-Fire/Mindustry-Ozone/master/Desktop/release.txt").openStream());
			String base = "https://github.com/o7-Fire/Mindustry-Ozone/releases/download/";
			String version = Propertied.Manifest.get("MindustryVersion");
			if (version == null) throw new NullPointerException("MindustryVersion Null");
			version = releaseManifest.get(version);
			if (version == null) version = Propertied.Manifest.get("MindustryVersion");
			base += version + "/";
			if (manifest) base += "Manifest.properties";
			return new URL(base);
		}catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	public static URL getDownload(String version, boolean manifest) {
		try {
			URL u = new URL(Utility.getDownload(Utility.jitpack, "com.github.o7-Fire.Mindustry-Ozone", manifest ? "Manifest" : "Desktop", version));
			if (manifest) {
				u = new URL("jar:" + u.toExternalForm() + "!/Manifest.properties");
			}
			return u;
		}catch (MalformedURLException t) {
			WarningHandler.handleProgrammerFault(t);
			throw new RuntimeException(t);
		}
	}
	
	@Override
	public void earlyInit() throws Throwable {
		future = async();
	}
}
