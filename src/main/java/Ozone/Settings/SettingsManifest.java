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

package Ozone.Settings;

import Atom.Reflect.FieldTool;
import Atom.Reflect.Reflect;
import Atom.Time.Countdown;
import Atom.Utility.Digest;
import Atom.Utility.Encoder;
import Ozone.Event.EventExtended;
import arc.Events;
import arc.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

public class SettingsManifest {
	static final File settingsFile = new File("OzoneSettings.properties");
	static URL urlSettings;
	static ConcurrentHashMap<String, String> cache;
	static volatile long lastFileHash = 0;
	
	static {
		Log.info("Settings File for ozone: " + settingsFile.getAbsolutePath());
		try {
			urlSettings = settingsFile.toURI().toURL();
		}catch (MalformedURLException e) {
			throw new RuntimeException("Invalid Path for SettingsFile", e);
		}
		Events.on(EventExtended.Shutdown.class, s -> {
			try {
				saveMap();
			}catch (Throwable e) {
				e.printStackTrace();
			}
		});
	}
	
	public static ConcurrentHashMap<String, String> getMap() throws IOException {
		if (cache == null) {
			long start = System.currentTimeMillis();
			if (settingsFile.exists()) cache = new ConcurrentHashMap<>(Encoder.parseProperty(urlSettings.openStream()));
			else cache = new ConcurrentHashMap<>();
			Log.debug("Loaded @ settings in @", cache.size(), Countdown.result(start));
		}
		return cache;
	}
	
	static void readSettings(Field f) throws IllegalAccessException, IOException {
		f.setAccessible(true);
		Class<?> clz = f.getDeclaringClass();
		String name = clz.getName() + "." + f.getName();
		if (!getMap().containsKey(name)) return;
		f.set(null, Reflect.parseStringToPrimitive(getMap().get(name), f.getType()));
	}
	
	public static void readSettings(Class<?> clazz) {
		long start = System.currentTimeMillis();
		for (Field f : clazz.getDeclaredFields()) {
			try {
				SettingsManifest.readSettings(f);
			}catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		Log.debug("Loaded settings for: " + clazz.getName() + " in " + Countdown.result(start));
	}
	
	static void save(Class<?> clz) throws IOException, IllegalAccessException {
		if (getMap().getOrDefault(clz.getName() + ".hash", "0").equals(getHash(clz))) return;
		for (Field f : clz.getDeclaredFields()) {
			String name = clz.getName() + "." + f.getName();
			if (f.getType().isPrimitive() || f.getType().getName().equals(String.class.getName()))
				if (getMap().containsKey(name)) getMap().replace(name, f.get(null).toString());
				else getMap().put(name, f.get(null).toString());
		}
		if (getMap().contains(clz.getName() + ".hash")) getMap().replace(clz.getName() + ".hash", getHash(clz));
		else getMap().put(clz.getName() + ".hash", getHash(clz));
	}
	
	static String getHash(Class<?> clz) {
		return String.valueOf(ByteBuffer.wrap(Digest.sha256(FieldTool.getFieldDetails(null, clz, false, 500).getBytes())).getLong());
	}
	
	public synchronized static void saveMap() throws IOException {
		if (cache == null) return;
		if (lastFileHash == ByteBuffer.wrap(cache.toString().getBytes()).getLong()) return;
		long start = System.currentTimeMillis();
		Files.write(settingsFile.toPath(), Encoder.property(cache).getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		Log.debug("Saved @ settings in @", cache.size(), Countdown.result(start));
		lastFileHash = ByteBuffer.wrap(cache.toString().getBytes()).getLong();
	}
	
}
