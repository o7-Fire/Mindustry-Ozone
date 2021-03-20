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
import Ozone.Propertied;
import Shared.InfoBox;

import java.net.URL;

public class StartServerPoint {
	public static void main(String[] args) {
		try {
			args = new String[]{"host", "Ancient_Caldera", "sandbox"};
			DesktopBootstrap.classloaderNoParent();
			DesktopBootstrap.loadClasspath();
			String version = Propertied.Manifest.get("MindustryVersion");
			if (version == null) throw new NullPointerException("MindustryVersion not found in property");
			DesktopBootstrap.ozoneLoader.addURL(new URL("https://github.com/Anuken/Mindustry/releases/download/" + version + "/server-release.jar"));
			DesktopBootstrap.loadMain("mindustry.server.ServerLauncher", args);
		}catch (Throwable t) {
			t.printStackTrace();
			InfoBox.write(t);
			
			System.exit(1);
		}
	}
}
