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

import Ozone.Desktop.Bootstrap.DesktopBootstrap;
import Ozone.Propertied;

public class DownloadDoc {
	public static void main(String[] args) throws Throwable {
		DesktopBootstrap.classloaderNoParent();
		DesktopBootstrap.loadRuntime();
		DesktopBootstrap.loadMain(Main.class.getName(), args);
		
	}
	
	public static class Main {
		
		public static void main(String[] args) {
			String mindustry = "https://github.com/Anuken/Mindustry/archive/" + Propertied.Manifest.getOrDefault("MindustryVersion", "v121.1") + ".zip";
			
		}
	}
}
