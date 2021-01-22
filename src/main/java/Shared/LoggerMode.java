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

package Shared;

import arc.Events;
import arc.struct.Seq;
import arc.util.ColorCodes;
import arc.util.Log;
import arc.util.OS;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.game.EventType;

import java.io.Writer;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import static arc.Core.settings;
import static mindustry.Vars.*;

public class LoggerMode {
	public static SimpleDateFormat dateTime = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	static Writer writer;
	
	public static void loadLogger() {
		
		
		String[] tags = {"[green][D][]", "[royal][I][]", "[yellow][W][]", "[scarlet][E][]", ""};
		String[] stags = {"&lc&fb[D]", "&lb&fb[I]", "&ly&fb[W]", "&lr&fb[E]", ""};
		settings.setAppName(appName);
		Seq<String> logBuffer = new Seq<>();
		if (SharedBoot.debug) Log.level = Log.LogLevel.debug;
		try { writer = settings.getDataDirectory().child("last_log.txt").writer(false); }catch (Throwable ignored) {}
		Log.logger = (level, text) -> {
			try {
				writer.write("[" + Character.toUpperCase(level.name().charAt(0)) + "] " + Log.removeColors(text) + "\n");
				writer.flush();
			}catch (Throwable ignored) {}
			text = "[" + dateTime.format(LocalDateTime.now()) + "] " + text;
			String result = text;
			String rawText = Log.format(stags[level.ordinal()] + "&fr " + text);
			System.out.println(rawText);
			
			result = tags[level.ordinal()] + " " + result;
			if (!text.startsWith("Ozone-Event-")) {
				String t = text;
				try {
					if (t.contains(Vars.player.name()))
						t = t.replaceAll(Vars.player.name, "Vars.player.name");//curb your name
				}catch (Throwable ignored) {}
				Sentry.addBreadcrumb(t, level.name());
			}
			if (!headless && (ui == null || ui.scriptfrag == null)) {
				logBuffer.add(result);
			}else if (!headless) {
				if (!OS.isWindows) {
					for (String code : ColorCodes.values) {
						result = result.replace(code, "");
					}
				}
				
				ui.scriptfrag.addMessage(Log.removeColors(result));
			}
		};
		
		Events.on(EventType.ClientLoadEvent.class, e -> logBuffer.each(ui.scriptfrag::addMessage));
		
	}
}
