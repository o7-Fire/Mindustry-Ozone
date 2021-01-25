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

package Ozone.Patch;

import Atom.Net.Request;
import Atom.Utility.Encoder;
import Atom.Utility.Pool;
import Atom.Utility.Random;
import Atom.Utility.Utility;
import Ozone.Internal.InformationCenter;
import Ozone.Propertied;
import arc.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.core.Version;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
//TODO add github provider for release
public class Updater {
	
	public final static AtomicBoolean newRelease = new AtomicBoolean(false), newBuild = new AtomicBoolean(false);
	private static volatile boolean init;
	private static volatile String last = "-SNAPSHOT";
	
	public static void init() {
		if (init) return;
		init = true;
		Log.debug("Update Daemon Started");
		
			Future a = Pool.submit(() -> {
				try {
					HashMap<String, String> he = Encoder.parseProperty(getBuild(true).openStream());
					newBuild.set(latest(he));
					if (newBuild.get()) Log.infoTag("Updater", "New Latest Build Found: " + he.get("VHash"));
					else {
						Log.debug("Latest Build Incompatible or Unavailable");
						Pool.daemon(() -> {
							try {
								URL u = new URL("https://api.github.com/repos/o7-Fire/Mindustry-Ozone/commits?per_page=" + Random.getInt(2, 15));
								JsonArray js = JsonParser.parseString(Encoder.readString(u.openStream())).getAsJsonArray();
								ArrayList<Future<String>> list = new ArrayList<>();
								for (JsonElement je : js) {
									list.add(Pool.submit(() -> checkJsonGithub(je)));
								}
								for (Future<String> f : list) {
									try {
										if (newBuild.get()) return;
										String h = f.get();
										if (h == null) continue;
										last = h;
										newBuild.set(true);
										if (newBuild.get()) Log.infoTag("Updater", "New Build Found: " + h);
										return;
									}catch (Throwable ignored) {}
								}
								Log.debug("No Compatible Build Found on Pool");
							}catch (Throwable e) {
								Sentry.captureException(e);
							}
						}).start();
					}
				}catch (Throwable e) {
					Sentry.captureException(e);
				}
			});
			
			Future b = Pool.submit(() -> {
				try {
					HashMap<String, String> h = Encoder.parseProperty(getRelease(true).openStream());
					newRelease.set(latest(h));
					if (newRelease.get()) Log.infoTag("Updater", " Release Found: " + h.get("VHash"));
					else Log.debug("Latest Release Is Already Installed or Unavailable");
				}catch (Throwable e) {
					Sentry.captureException(e);
					Log.err(e);
					Log.err("Failed to update");
				}
			});
			try {
				a.get();
			}catch (Throwable ignored) {}
			try {
				b.get();
			}catch (Throwable ignored) {}
			if (newBuild.get()) return;
		
		
	}
	
	private static String checkJsonGithub(JsonElement je) {
		try {
			JsonObject jb = (JsonObject) je;
			String sha = jb.get("sha").getAsString();
			if (sha == null) throw new NullPointerException("SHA null" + je.toString());
			HashMap<String, String> h;
			h = Encoder.parseProperty(getDownload(sha, true).openStream());
			if (latest(h)) return sha;
			else return null;
		}catch (Throwable ignored) {
			return null;
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
			Sentry.captureException(t);
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
			if (a > b) return false;
		}catch (NumberFormatException asshole) {
			Sentry.captureException(asshole);
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
		}catch (NumberFormatException asshole) {
			Sentry.captureException(asshole);
			return false;
		}
		return true;
	}
	
	public static URL getBuild(boolean manifest) {
		return getDownload(last, manifest);
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
		}catch (MalformedURLException malformedURLException) {
			Sentry.captureException(malformedURLException);
			throw new RuntimeException(malformedURLException);
		}
	}
	
	
	public static Future<?> async() {
		return Pool.submit(Updater::init);
	}
}
