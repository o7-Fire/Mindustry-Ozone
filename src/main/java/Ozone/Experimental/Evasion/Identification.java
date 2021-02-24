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

package Ozone.Experimental.Evasion;

import Atom.Utility.Pool;
import arc.Core;
import arc.math.Rand;
import arc.util.serialization.Base64Coder;
import io.sentry.Sentry;
import mindustry.Vars;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class Identification {
	public static HashMap<String, Object> getValue() throws NoSuchFieldException, IllegalAccessException {
		Field f = Core.settings.getClass().getDeclaredField("values");
		f.setAccessible(true);
		return (HashMap<String, Object>) f.get(Core.settings);
	}
	
	public static String getUUID(String player) {
		return Core.settings.getString("uuid-" + player, getRandomUID());
	}
	
	public static String getUUID() {
		return Core.settings.getString("uuid", getRandomUID());
	}
	
	public static void setUUID(String s) {
		Core.settings.put("uuid", s);
	}
	
	public static ArrayList<String> getKeys() throws NoSuchFieldException, IllegalAccessException {
		HashMap<String, Object> values = getValue();
		ArrayList<String> yikes = new ArrayList<>();
		for (String s : values.keySet()) {
			if (s.startsWith("usid-") || s.startsWith("uuid")) yikes.add(s);
		}
		return yikes;
	}
	
	public static void changeID(Runnable onfinished) {
		Pool.daemon(() -> {
			try {
				changeID();
			}catch (Throwable t) {
				t.printStackTrace();
				Sentry.captureException(t);
				Vars.ui.showException(t);
			}
			onfinished.run();
		}).start();
	}
	
	public static void changeID() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<String> yikes = getKeys();
		
		for (String s : yikes) Core.settings.put(s, getRandomUID());
		
	}
	
	public static String getRandomUID() {
		byte[] bytes = new byte[8];
		(new Rand()).nextBytes(bytes);
		return new String(Base64Coder.encode(bytes));
	}
	
	public static String getUsid(String ip) {
		if (ip.contains("/")) {
			ip = ip.substring(ip.indexOf("/") + 1);
		}
		
		if (Core.settings.getString("usid-" + ip, null) != null) {
			return Core.settings.getString("usid-" + ip, null);
		}else {
			String result = getRandomUID();
			Core.settings.put("usid-" + ip, result);
			return result;
		}
	}
}
