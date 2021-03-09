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

package Main;

import Atom.File.FileUtility;
import Ozone.Desktop.Pre.PreInstall;
import Ozone.Desktop.Swing.Main;
import Ozone.Propertied;
import Ozone.Version;

import java.io.File;

/**
 * @author Itzbenz
 */
public class OzoneInstaller {
	public static File mindustry;
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Main m;
		mindustry = new File(FileUtility.getAppdata(), "Mindustry/");
		m = new Main();
		m.label1.setText("Mindustry " + Propertied.Manifest.getOrDefault("MindustryVersion", "null") + " [Ozone:" + Version.core + ":" + Version.desktop + "]");
		PreInstall.install(m);
	}
	
}
