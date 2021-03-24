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

/* o7 Inc 2021 Copyright

  Licensed under the o7 Inc License, Version 1.0.1, ("the License");
  You may use this file but only with the License. You may obtain a
  copy of the License at
  
  https://github.com/o7-Fire/Mindustry-Ozone/Licenses
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the license for the specific language governing permissions and
  limitations under the License.
*/

package Ozone.Experimental.Evasion;

import Atom.String.WordGenerator;
import Atom.Utility.Pool;
import Atom.Utility.Random;
import Atom.Utility.Utility;
import Ozone.Patch.Translation;
import Shared.WarningHandler;
import arc.Core;
import arc.graphics.Color;
import arc.math.Rand;
import arc.struct.Seq;
import arc.util.serialization.Base64Coder;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.net.Packets;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static mindustry.Vars.maxNameLength;

@SuppressWarnings("Unchecked")
public class Identification {
	public static HashMap<String, Object> getValue() throws NoSuchFieldException, IllegalAccessException {
		Field f = Core.settings.getClass().getDeclaredField("values");
		f.setAccessible(true);
		return (HashMap<String, Object>) f.get(Core.settings);
	}
	
	public static Packets.ConnectPacket randomConnectPacket() {
		Packets.ConnectPacket c = new Packets.ConnectPacket();
		Player p = null;
		if (Groups.player.size() > 2) p = Random.getRandom(Groups.player);
		c.name = p == null ? (Random.getBool() ? Utility.capitalizeEnforce(WordGenerator.newWord(Random.getInt(5, maxNameLength))) : WordGenerator.newWord(Random.getInt(5, maxNameLength))) : p.name + Translation.getRandomHexColor();
		c.locale = null;//lol no
		c.mods = new Seq<>();
		c.mobile = Random.getBool();
		c.versionType = Version.type;
		c.color = p == null ? Color.valueOf(Random.getRandomHexColor()).rgba() : p.color.rgba();
		c.usid = Identification.getRandomUID();
		c.uuid = Identification.getRandomUID();
		return c;
	}
	
	public static String getUUID(String player) {
		String s = Core.settings.getString("uuid" + player, getRandomUID());
		Core.settings.put("uuid" + player, s);
		return s;
	}
	
	public static String getUUID() {
		return getUUID("");
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
				WarningHandler.handleMindustry(t);
				Vars.ui.showException(t);
			}
			onfinished.run();
		}).start();
	}
	
	public static void changeID() throws NoSuchFieldException, IllegalAccessException {
		ArrayList<String> yikes = getKeys();
		AtomicInteger i = new AtomicInteger();
		Vars.ui.loadfrag.setProgress(() -> (float) i.get() / yikes.size());
		for (String s : yikes) {
			if (Vars.ui != null && Vars.ui.loadfrag != null) {
				Vars.ui.loadfrag.setText(i.get() + "/" + yikes.size());
			}
			Core.settings.put(s, getRandomUID());
			i.getAndIncrement();
		}
		
	}
	
	public static String getRandomUID() {
		byte[] bytes = new byte[8];
		(new Rand()).nextBytes(bytes);
		return new String(Base64Coder.encode(bytes));
	}
	
	public static String getUsid(String ip, int port) {
		return getUsid(ip + ":" + port);
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
