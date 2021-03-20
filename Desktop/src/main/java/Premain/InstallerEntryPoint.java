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

package Premain;

import Ozone.Desktop.Bootstrap.DesktopBootstrap;
import Shared.InfoBox;
import io.sentry.Sentry;


public class InstallerEntryPoint {
	static long start = System.currentTimeMillis();
	
	//Standalone only
	public static void main(String[] args) {
		if (System.getProperty("ozoneTest") != null) {
			TestEntryPoint.main(args);
			return;
		}
		
		if (System.getProperty("server") != null) {
			StartServerPoint.main(args);
			return;
		}
		
		try {
			DesktopBootstrap.requireDisplay();
			DesktopBootstrap.classloaderNoParent();
			DesktopBootstrap.loadRuntime();
			DesktopBootstrap.loadClasspath();
			DesktopBootstrap.loadMain("Main.OzoneInstaller", args);
		}catch (Throwable t) {
			InfoBox.write(t);
			t.printStackTrace();
			if (t.getCause() != null) t = t.getCause();
			Sentry.captureException(t);
			InfoBox.errorBox(t.toString(), "Ozone Installer");
			System.exit(1);
		}
	}
}
