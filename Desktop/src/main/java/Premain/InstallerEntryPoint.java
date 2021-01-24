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

package Premain;

import Ozone.Desktop.Bootstrap.DesktopBootstrap;
import io.sentry.Sentry;


public class InstallerEntryPoint {
	static long start = System.currentTimeMillis();
	
	//Standalone only
	public static void main(String[] args) {
		if (System.getProperty("ozoneTest") != null) {
			TestEntryPoint.main(args);
			return;
		}
		try {
			long a, b, c;
			DesktopBootstrap.requireDisplay();
			DesktopBootstrap.classloaderNoParent();
			a = System.currentTimeMillis();
			DesktopBootstrap.loadRuntime();
			b = System.currentTimeMillis();
			DesktopBootstrap.loadClasspath();
			
			DesktopBootstrap.loadMain("Main.OzoneInstaller", args);
		}catch (Throwable t) {
			Catch.write(t);
			t.printStackTrace();
			if (t.getCause() != null) t = t.getCause();
			Sentry.captureException(t);
			Catch.errorBox(t.toString(), "Ozone Installer");
			System.exit(1);
		}
	}
}
