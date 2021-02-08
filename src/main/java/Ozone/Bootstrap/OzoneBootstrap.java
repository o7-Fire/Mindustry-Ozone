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

package Ozone.Bootstrap;

import Atom.Utility.Cache;
import Atom.Utility.Encoder;
import Ozone.Settings.BaseSettings;
import Ozone.Version;
import Shared.SharedBoot;
import arc.util.Log;

import java.net.URL;
import java.util.HashMap;

import static Ozone.Patch.Updater.getRelease;
import static Ozone.Patch.Updater.latest;

public class OzoneBootstrap {
	public static URL neu = null;
	
	public static boolean init() {
		SharedBoot.initSentry();
		if (SharedBoot.debug) {
			Log.level = Log.LogLevel.debug;
			BaseSettings.debugMode = true;
		}
		Log.info("Ozone-Version: " + Version.core);
		try {
			HashMap<String, String> h = Encoder.parseProperty(getRelease(true).openStream());
			if (latest(h)) neu = Cache.http(getRelease(SharedBoot.type + ".jar"));
		}catch (Throwable e) {
			Log.err(e);
		}
		return false;
	}
}
