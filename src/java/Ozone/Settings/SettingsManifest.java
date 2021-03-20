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

package Ozone.Settings;

import Atom.File.FileUtility;
import Atom.Reflect.FieldTool;
import Atom.Reflect.Reflect;
import Atom.Utility.Digest;
import Atom.Utility.Encoder;
import Shared.WarningHandler;
import Shared.WarningReport;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class SettingsManifest {
	public static File settingsFile = new File("OzoneSettings.properties");
	static ConcurrentHashMap<String, String> cache;
	static HashSet<Class<?>> list = new HashSet<>();
	
	public static void changeFile(File neu) {
		if (neu.equals(settingsFile)) return;
		if (!neu.exists()) {
			if (settingsFile.exists()) {
				getMap();
			}
		}else {
			cache = null;
		}
		settingsFile = neu;
		reload();
	}
	
	public static void reload() {
		saveMap();
		cache = null;
		getMap();
		for (Class<?> c : list)
			readSettings(c);
	}
	
	public static ConcurrentHashMap<String, String> getMap() {
		if (cache == null) {
			try {
				cache = new ConcurrentHashMap<>(Encoder.parseProperty(new String(FileUtility.readAllBytes(settingsFile))));
			}catch (Throwable ignored) {
				cache = new ConcurrentHashMap<>();
			}
		}
		return cache;
	}
	
	static void readSettings(Field f) {
		try {
			f.setAccessible(true);
			Class<?> clz = f.getDeclaringClass();
			String name = clz.getName() + "." + f.getName();
			if (!getMap().containsKey(name)) return;
			f.set(null, Reflect.parseStringToPrimitive(getMap().get(name), f.getType()));
		}catch (Throwable ignored) {}
	}
	
	public static void readSettings(Class<?> clazz) {
		try {
			list.add(clazz);
		}catch (Throwable ignored) {}
		for (Field f : clazz.getDeclaredFields()) {
			if (f.getType().isPrimitive() || f.getType().equals(String.class)) SettingsManifest.readSettings(f);
		}
	}
	
	static void save(Class<?> clz) throws IOException, IllegalAccessException {
		if ((getMap().get(clz.getName() + ".hash") == null ? "0" : getMap().get(clz.getName() + ".hash")).equals(getHash(clz)))
			return;
		for (Field f : clz.getDeclaredFields()) {
			String name = clz.getName() + "." + f.getName();
			if (f.getType().isPrimitive() || f.getType().getName().equals(String.class.getName()))
				getMap().put(name, f.get(null).toString());
		}
		getMap().put(clz.getName() + ".hash", getHash(clz));
	}
	
	static String getHash(Class<?> clz) {
		return String.valueOf(ByteBuffer.wrap(Digest.sha256(FieldTool.getFieldDetails(null, clz, false, 500).getBytes())).getLong());
	}
	
	public static void saveMap() {
		if (cache == null) return;
		FileUtility.makeFile(settingsFile);
		if (!settingsFile.canWrite()) {
			WarningHandler.handle(new WarningReport().setProblem("Can't write to file: " + settingsFile.getAbsolutePath()).setWhyItsAProblem("its your settings file").setHowToFix("Check permission or file if its corrupted or busy").setLevel(WarningReport.Level.err));
			return;
		}
		try {
			if (!FileUtility.write(settingsFile, Encoder.property(cache).getBytes())) {
				WarningHandler.handle(new WarningReport().setProblem("Failed to save: " + settingsFile.getAbsolutePath()).setWhyItsAProblem("no its your problem").setHowToFix("see if shit is corrupted or denied by system").setLevel(WarningReport.Level.warn));
			}
		}catch (Throwable t) {
			new WarningReport(t).setWhyItsAProblem("No Ozone Settings Save").report();
		}
	}
	
}
