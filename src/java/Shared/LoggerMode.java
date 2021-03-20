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

package Shared;

import Atom.Reflect.Reflect;
import Atom.Utility.Utility;
import arc.Events;
import arc.struct.Seq;
import arc.util.ColorCodes;
import arc.util.Log;
import arc.util.OS;
import io.sentry.Sentry;
import mindustry.game.EventType;

import java.io.Writer;

import static Shared.WarningHandler.isLoaded;
import static arc.Core.settings;
import static mindustry.Vars.*;

public class LoggerMode {
	
	static Writer writer;
	static volatile boolean loaded = isLoaded();
	public static Seq<String> logBuffer = new Seq<>();
	public static String[] tags = {"[green][D][]", "[royal][I][]", "[yellow][W][]", "[scarlet][E][]", ""};
	public static String[] color = {"[green]", "[royal]", "[yellow]", "[scarlet]", "[white]"};
	public static String[] stags = {"&lc&fb[D]", "&lb&fb[I]", "&ly&fb[W]", "&lr&fb[E]", ""};
	
	public static void loadLogger() {
		if (loaded) return;
		loaded = true;
		
		if (SharedBoot.debug) Log.level = Log.LogLevel.debug;
		try {
			settings.setAppName(appName);
			writer = settings.getDataDirectory().child("last_log_ozone.txt").writer(false);
		}catch (Throwable ignored) {}
		Log.logger = (level, text) -> {
			int i = Reflect.callerOffset() + 3;
			StackTraceElement st = null;
			try {
				st = Thread.currentThread().getStackTrace()[i];
			}catch (Throwable ignored) {
			
			}
			if (st != null && st.getFileName() != null) {
				while (true) {
					assert st.getFileName() != null;//wtf intellj
					if (!st.getFileName().equals("Log.java")) break;
					try {
						i++;
						st = Thread.currentThread().getStackTrace()[i];
					}catch (Throwable t) {
						st = null;
						break;
					}
				}
			}
			text = "[" + Utility.getDate() + "] [" + Thread.currentThread().getName() + (Thread.currentThread().getThreadGroup() != null ? "-" + Thread.currentThread().getThreadGroup().getName() : "") + (st == null || !SharedBoot.debug ? "" : "-" + st.toString()) + "] " + text;
			try {
				writer.write("[" + Character.toUpperCase(level.name().charAt(0)) + "] " + Log.removeColors(text) + "\n");
				writer.flush();
			}catch (Throwable ignored) {
			}
			
			String result = text;
			String rawText = Log.format(stags[level.ordinal()] + "&fr " + text);
			System.out.println(rawText);
			
			result = tags[level.ordinal()] + " " + result;
			if (!text.startsWith("Ozone-Event-")) {
				String t = text;
				
				Sentry.addBreadcrumb(t, level.name());
			}
			logBuffer.add(result);
			if (ui != null && ui.scriptfrag != null) {
				if (!headless) {
					if (!OS.isWindows) {
						for (String code : ColorCodes.values) {
							result = result.replace(code, "");
						}
					}
					
					ui.scriptfrag.addMessage(Log.removeColors(result));
				}
			}
		};
		
		Events.on(EventType.ClientLoadEvent.class, e -> logBuffer.each(ui.scriptfrag::addMessage));
		
	}
}
