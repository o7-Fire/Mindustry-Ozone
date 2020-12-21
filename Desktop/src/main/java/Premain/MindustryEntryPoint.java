/*
 * Copyright 2020 Itzbenz
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

package Premain;

import Ozone.Desktop.Bootstrap.Dependency;
import Ozone.Desktop.Bootstrap.SharedBootstrap;
import Ozone.Desktop.Propertied;
import io.sentry.Sentry;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MindustryEntryPoint {
	public static void main(String[] args) {
		try {
			
			System.out.println("Initializing Ozone Environment");
			SharedBootstrap.classloaderNoParent();
			SharedBootstrap.loadRuntime();
			SharedBootstrap.loadClasspath();
			main(new ArrayList<>(Arrays.asList(args)));
		}catch (Throwable t) {
			t.printStackTrace();
			if (t.getCause() != null) t = t.getCause();
			Catch.write(t);
			Sentry.captureException(t);
			Catch.errorBox(t.toString(), "Ozone Environment");
			System.exit(1);
		}
	}
	
	public static void main(ArrayList<String> args) throws Throwable {
		File mindustryJar = null;
		if (System.getProperty("MindustryExecutable") != null)
			mindustryJar = new File(System.getProperty("MindustryExecutable"));
		else if (!args.isEmpty()) mindustryJar = new File(args.get(0));
		if (mindustryJar != null && mindustryJar.exists()) SharedBootstrap.libraryLoader.addURL(mindustryJar);
		else {
			System.out.println("No Mindustry jar found, using online resource");
			String version = Propertied.Manifest.get("MindustryVersion");
			if (version == null) throw new NullPointerException("MindustryVersion not found in property");
			SharedBootstrap.load(Dependency.Type.provided);
			SharedBootstrap.libraryLoader.addURL(new URL("https://github.com/Anuken/Mindustry/releases/download/" + version + "/Mindustry.jar"));
			SharedBootstrap.standalone = true;
		}
		SharedBootstrap.loadMain("Main.OzoneMindustry", args.toArray(new String[0]));
	}
	
}
