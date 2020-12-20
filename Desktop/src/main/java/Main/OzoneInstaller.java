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

package Main;

import Ozone.Desktop.Pre.PreInstall;
import Ozone.Desktop.Propertied;
import Ozone.Desktop.Swing.Main;
import Ozone.Watcher.Version;

import java.io.File;

/**
 * @author Itzbenz
 */
public class OzoneInstaller {
	public static File mindustry;
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Main m;
		
		try {
			if (System.getProperty("os.name").toUpperCase().contains("WIN"))
				mindustry = new File(System.getenv("AppData") + "/Mindustry");//windows
			else mindustry = new File(System.getenv("HOME") + "/.local/share/Mindustry");//linux
		}catch (Throwable t) {
			mindustry = new File("mindustry/");//i gave up "yeet"//
		}
		m = new Main();
		m.label1.setText("Mindustry " + Propertied.Manifest.getOrDefault("MindustryVersion", "null") + " [Ozone:" + Version.semantic + ":" + Settings.Version.semantic + "]");
		PreInstall.install(m);
	}
	
}
