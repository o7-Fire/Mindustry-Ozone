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

import Atom.Time.Time;
import Ozone.Manifest;
import Ozone.Propertied;
import Ozone.Settings.SettingsManifest;
import Ozone.Version;
import io.sentry.Scope;
import io.sentry.Sentry;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SharedBoot {
	public static boolean test = System.getProperty("ozoneTest") != null;
	public static boolean standalone, hardDebug = System.getProperty("intellij.debug.agent") != null || System.getProperty("debug") != null || test;
	public static boolean debug = hardDebug;
	public static Time timeStart = new Time(), timeFinish;
	public static String type = "Ozone-Core";
	
	public static void resetTime() {
		timeStart = new Time();
	}
	
	public static void finishStartup() {
		timeFinish = SharedBoot.timeStart.elapsed();
		new WarningReport().setProblem("Startup in " + timeFinish.convert(TimeUnit.MILLISECONDS).toString()).setWhyItsAProblem("Successfully startup without crash").setLevel(WarningReport.Level.info).report();
		
	}
	
	public static boolean isCore() {
		return type.equals("Ozone-Core");
	}
	
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
		
	}

	public static <T> T getOrDefault(Map<?, T> map, Object key, T def) {
		T t = map.get(key);
		if (t == null) return def;
		return t;
	}
	
	public static void initSentry() {
		if (hardDebug && !test) {
			System.out.println("Hard Debug, disabling sentry");
			return;
		}
		if (debug) {
			System.out.println("Sentry: Debug");
		}
		Sentry.init(options -> {
			options.setDsn("https://cd76eb6bd6614c499808176eaaf02b0b@o473752.ingest.sentry.io/5509036");
			options.setRelease(Version.core + ":" + Version.desktop);
			options.setDebug(debug);
			options.setTracesSampleRate(1.0);
			options.setEnvironment(getOrDefault(Propertied.Manifest, "VHash", "unspecified").equals("unspecified") ? "dev" : "release");
			if (test) options.setEnvironment("test");
		}, true);
		Sentry.configureScope(SharedBoot::registerSentry);
	}
	
	public static void registerSentry(Scope scope) {
		try {
			scope.setTag("Ozone.Desktop.Version", Version.desktop);
			scope.setTag("Ozone.Core.Version", Version.core);
			scope.setTag("Operating.System", System.getProperty("os.name") + " x" + System.getProperty("os.arch"));
			scope.setTag("Java.Version", System.getProperty("java.version"));
			try {
				scope.setTag("Client.Version", mindustry.core.Version.combined());
			}catch (Throwable ignored) {}
			for (Map.Entry<String, String> e : Propertied.Manifest.entrySet())
				scope.setTag(e.getKey(), e.getValue());
		}catch (Throwable t) {
			t.printStackTrace();
			Sentry.captureException(t);
		}
	}
}
