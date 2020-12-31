package Ozone.Test;/*
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

import Atom.File.FileUtility;
import Ozone.Desktop.Pre.DownloadSwing;
import Ozone.Desktop.Propertied;
import Ozone.Pre.Download;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class NetTest extends Test {

	public URL url;

	public NetTest() {
		subTests.add(new SubTest("Downloading Interface Swing", this::downloadGUI));
		subTests.add(new SubTest("Downloading Interface CLI", this::download));
		try {
			String version = Propertied.Manifest.get("MindustryVersion");
			if (version == null) throw new NullPointerException("MindustryVersion not found in property");
			url = new URL("https://github.com/Anuken/Mindustry/releases/download/" + version + "/server-release.jar");
		}catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public static File file() {
		File f = FileUtility.temp();
		System.out.println(f.getAbsolutePath());
		return f;
	}

	public void downloadGUI() {
		DownloadSwing d = new DownloadSwing(url, file());
		d.display();
		d.run();
	}

	public void download() {
		Download d = new Download(url, file());
		d.print(s -> Log.info(s));
		d.run();
	}
}
