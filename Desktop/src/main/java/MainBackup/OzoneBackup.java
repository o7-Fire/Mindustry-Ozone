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

package MainBackup;

import Ozone.Desktop.Patch.DesktopPatcher;
import Ozone.Main;
import mindustry.mod.Mod;

//In case of classloader is not URClassloader
public class OzoneBackup extends Mod {

    @Override
    public void init() {
        DesktopPatcher.register();
        Main.init();
    }

    @Override
    public void loadContent() {
        Main.loadContent();
    }
}
