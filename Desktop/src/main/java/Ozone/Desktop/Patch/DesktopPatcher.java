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

package Ozone.Desktop.Patch;

import Atom.Reflect.Reflect;
import arc.Core;
import arc.Events;
import arc.net.Client;
import arc.util.Log;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.net.ArcNetProvider;
import mindustry.net.Net;

import java.io.File;

public class DesktopPatcher {
	public static File cache = new File(Vars.dataDirectory.file(), "cache/");
	
	public static void async() {
	
	}
	
	
	public static String getServer() {
		try {
			Net.NetProvider n = Reflect.getField(Vars.net.getClass(), "provider", Vars.net);
			ArcNetProvider arc = (ArcNetProvider) n;
			Client c = Reflect.getField(arc.getClass(), "client", arc);
			return c.getRemoteAddressTCP().getHostName() + ":" + c.getRemoteAddressTCP().getPort();
		}catch (Throwable t) {
			Sentry.captureException(t);
			Log.err(t);
			return "Null";
		}
	}
	
	
	public static void register() {
		
		
		Events.on(EventType.ClientLoadEvent.class, s -> {
			Core.settings.getBoolOnce("CrashReportv1", () -> Vars.ui.showConfirm("Anonymous Data Reporter", "We collect your anonymous insensitive data (crash-log) so we can fix thing, no turning back", () -> {
			}));
			
			
		});
		
	}
}
