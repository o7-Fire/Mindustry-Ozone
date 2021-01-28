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

import Ozone.Manifest;
import Ozone.Propertied;
import Ozone.Settings.BaseSettings;
import Ozone.Settings.SettingsManifest;
import Ozone.Version;
import io.sentry.Scope;
import io.sentry.Sentry;
import io.sentry.protocol.User;

import java.nio.ByteBuffer;
import java.util.Map;

public class SharedBoot {
	public static boolean standalone, debug = System.getProperty("intellij.debug.agent") != null || System.getProperty("debug") != null || System.getProperty("ozoneTest") != null;
	public static long startup = System.nanoTime();
	public static String type = "Ozone-Core";
	
	static {
		if (!debug) try {
			debug = getOrDefault(SettingsManifest.getMap(), "Ozone.Settings.BaseSettings.debugMode", "false").equalsIgnoreCase("true");
		}catch (Throwable ignored) {
		
		}
		try {
			Manifest.class.getClassLoader().loadClass("Ozone.Desktop.Bootstrap.DesktopBootstrap").getName();
			standalone = false;
		}catch (Throwable ignored) {
			standalone = true;
		}
		if (debug) BaseSettings.debugMode = true;
	}

	public static <T> T getOrDefault(Map<?, T> map, Object key, T def) {
		T t = map.get(key);
		if (t == null) return def;
		return t;
	}


	public static void initSentry() {
		Sentry.init(options -> {
			options.setDsn("https://cd76eb6bd6614c499808176eaaf02b0b@o473752.ingest.sentry.io/5509036");
			options.setRelease(Version.core + ":" + Version.desktop);
			options.setDebug(debug);
			options.setTracesSampleRate(1.0);
			options.setEnvironment(getOrDefault(Propertied.Manifest, "VHash", "unspecified").equals("unspecified") ? "dev" : "release");
			if (System.getProperty("ozoneTest") != null) options.setEnvironment("test");
		}, true);
		Sentry.configureScope(SharedBoot::registerSentry);
	}
	
	public static void registerSentry(Scope scope) {
		try {
			scope.setTag("Ozone.Desktop.Version", Version.desktop);
			scope.setTag("Ozone.Core.Version", Version.core);
			scope.setTag("Operating.System", System.getProperty("os.name") + " x" + System.getProperty("sun.arch.data.model"));
			scope.setTag("Java.Version", System.getProperty("java.version"));
			try {
				User u = new User();
				long l = ByteBuffer.wrap(System.getenv().toString().getBytes()).getLong();// ? cant reverse it to full byte array
				u.setId(l + "");
				scope.setUser(u);//easier to filter asshole
			}catch (Throwable t) {
				Sentry.captureException(t);
			}
			for (Map.Entry<String, String> e : Propertied.Manifest.entrySet())
				scope.setTag(e.getKey(), e.getValue());
		}catch (Throwable t) {
			t.printStackTrace();
			Sentry.captureException(t);
		}
	}
}
