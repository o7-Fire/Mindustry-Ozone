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

import Ozone.Desktop.Bootstrap.SharedBootstrap;
import Ozone.Desktop.Propertied;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class RunMindustry {
	public static void main(String[] args) throws IOException, URISyntaxException {
		SharedBootstrap.classloaderNoParent();
		ProcessBuilder pb = new ProcessBuilder();
		pb.command("java", "-jar", SharedBootstrap.ozoneLoader.cache(new URL("https://github.com/Anuken/Mindustry/releases/download/" + Propertied.Manifest.get("MindustryVersion") + "/Mindustry.jar")).toURI().getPath());
		pb.redirectError(ProcessBuilder.Redirect.INHERIT);
		pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
		pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		pb.start().exitValue();
	}
}
