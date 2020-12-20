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

import Ozone.Desktop.Bootstrap.Dependency;
import Ozone.Desktop.Bootstrap.SharedBootstrap;
import Ozone.Desktop.Propertied;

import java.net.URL;

public class StartServer {
    public static void main(String[] args) {
        try {
            args = new String[]{"host", "Ancient_Caldera", "sandbox"};
            SharedBootstrap.classloaderNoParent();
            String version = Propertied.Manifest.get("MindustryVersion");
            if (version == null) throw new NullPointerException("MindustryVersion not found in property");
            SharedBootstrap.load(Dependency.Type.provided);
            SharedBootstrap.libraryLoader.addURL(new URL("https://github.com/Anuken/Mindustry/releases/download/" + version + "/server-release.jar"));
            SharedBootstrap.standalone = true;
            SharedBootstrap.loadMain("mindustry.server.ServerLauncher", args);
        }catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
}
