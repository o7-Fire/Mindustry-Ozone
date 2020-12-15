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
import Ozone.Experimental.Evasion.Identification;
import arc.Core;
import io.sentry.Sentry;
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
        cont.button("@mods", Icon.book, Vars.ui.mods::show).growX();// a sacrifice indeed
        cont.row();
        if (!Manifest.isBot())
            cont.button(Core.bundle.get("BotsController"), Icon.android, Manifest.botControllerDialog::show).growX();
        else
            cont.button(Core.bundle.get("BotsController"), Icon.android, Bot.Manifest.botUI::show).growX();
        cont.row();
        generic();
        cont.button("Reset UID", Icon.refresh, () -> {
            Vars.ui.showConfirm("Reset UID", "Reset all uuid and usid", () -> {
                try {
                    Identification.changeID();
                }catch (Throwable e) {
                    Sentry.captureException(e);
                    Vars.ui.showException(e);
                }
            });
        }).growX();

    }

    void generic() {
        add(Manifest.envInf);
        add(Manifest.dbgMenu);
    }

    void add(OzoneBaseDialog dialog) {
        cont.button(Core.bundle.get(dialog.getClass().getName()), dialog.icon, dialog::show).growX();
        cont.row();
    }
}
