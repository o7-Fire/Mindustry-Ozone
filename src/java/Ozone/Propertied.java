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

package Ozone;

import Atom.Utility.Encoder;
import Shared.WarningHandler;
import Shared.WarningReport;
import arc.util.Log;
import mindustry.core.Version;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Propertied {
	public static HashMap<String, String> Manifest;
	
	static {
		Manifest = read("Manifest.properties");
		if (Manifest.get("MindustryVersion") == null) {
			try {
				Manifest.put("MindustryVersion", "v" + Version.build + (Version.revision == 0 ? "" : "." + Version.revision));
			}catch (Throwable ignored) {}
			WarningHandler.handle(new WarningReport().setProblem("Manifest Failed").setWhyItsAProblem("Updater manifest comparator, version validation").setHowToFix("Try see if Manifest.properties exists on jar/zip").setLevel(WarningReport.Level.err));
		}
	}
	
	public static String getMindustryVersion() {
		return Manifest.get("MindustryVersion");
	}
	
	public static InputStream getResource(String name) throws IOException {
		return Atom.Manifest.internalRepo.getResource(name).openStream();
	}
	
	public static HashMap<String, String> read(String name) {
		HashMap<String, String> temp;
		try {
			temp = parse(new String(Encoder.readAllBytes(getResource(name))));
		}catch (Throwable g) {
			temp = new HashMap<>();
			try { Log.err(g); }catch (Throwable ignored) {}
			WarningHandler.handleMindustry(g);
		}
		return temp;
	}
	
	public static String reverseParse(HashMap<String, String> se) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> s : se.entrySet())
			sb.append(s.getKey()).append("=").append(s.getValue()).append("\n");
		return sb.toString();
	}
	
	public static HashMap<String, String> parse(String se) {
		HashMap<String, String> te = new HashMap<>();
		for (String s : se.split("\n")) {
			if (s.endsWith("\r")) s = s.substring(0, s.length() - 1);
			if (!s.startsWith("#")) te.put(s.split("=")[0], s.split("=")[1]);
		}
		return te;
	}
	
}
