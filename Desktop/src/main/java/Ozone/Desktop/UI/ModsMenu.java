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

package Ozone.Desktop.UI;

import Ozone.Desktop.Manifest;
import arc.Core;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;

public class ModsMenu extends BaseDialog {
    public ModsMenu() {
        super("Menu");
        addCloseButton();
        shown(this::setup);
        onResize(this::setup);
    }

    void setup() {
        cont.clear();
        cont.button("@mods", Icon.book, Vars.ui.mods::show).growX();
        cont.row();
        cont.button(Core.bundle.get("BotsController"), Icon.android, Manifest.botControllerDialog::show).growX();
        /*
        cont.button(Random.getString(),Icon.android, ()->{}).growX();
        cont.button(Random.getString(),Icon.android, ()->{}).growX();
        cont.button(Random.getString(),Icon.android, ()->{}).growX();
        cont.button(Random.getString(),Icon.android, ()->{}).growX();
        cont.row();
        cont.button(Random.getString(),Icon.android, ()->{}).left();
        cont.button(Random.getString(),Icon.android, ()->{}).left();
        cont.button(Random.getString(),Icon.android, ()->{}).left();
        cont.button(Random.getString(),Icon.android, ()->{}).left();
        cont.row();
        cont.field(Random.getString(), s ->{

        }).growX().right();
        cont.button(Random.getString(),Icon.android, ()->{}).growX().left();

         */

    }
}
