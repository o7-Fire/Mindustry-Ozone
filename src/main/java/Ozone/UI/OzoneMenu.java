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

package Ozone.UI;


import Atom.Reflect.Reflect;
import Ozone.Commands.Commands;
import Ozone.Manifest;
import Settings.Core;
import arc.input.KeyCode;
import arc.scene.ui.TextField;
import arc.util.Interval;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;

public class OzoneMenu extends BaseDialog {
    private TextField commandsField;
    private String commands = "";
    Interval interval = new Interval();
    public OzoneMenu(String title, DialogStyle style) {
        super(title, style);
        this.keyDown((key) -> {
            if (key == KeyCode.escape || key == KeyCode.back) {
                arc.Core.app.post(this::hide);
            }else if (key == KeyCode.enter) {
                Commands.call(Core.commandsPrefix + commands);
                commands = "";
                commandsField.clearText();
            }
        });
        this.shown(this::setup);
        this.onResize(this::setup);
        update(this::update);
    }

    void update() {
        if (!isShown()) return;
        if (!interval.get(40)) return;
        try {
            if (!Vars.ui.hudfrag.shown)
                Reflect.getMethod(null, "toggleMenus", Vars.ui.hudfrag).invoke(Vars.ui.hudfrag);
        }catch (Throwable ignored) { }
        if (!Vars.ui.chatfrag.shown()) {
            Vars.ui.chatfrag.toggle();
        }
        arc.Core.scene.setKeyboardFocus(commandsField);
    }

    @Override
    public void hide() {
        super.hide();
        try {
            if (Vars.ui.chatfrag.shown()) {
                Vars.ui.chatfrag.toggle();
            }
            if (!Vars.ui.hudfrag.shown)
                Reflect.getMethod(null, "toggleMenus", Vars.ui.hudfrag).invoke(Vars.ui.hudfrag);
        }catch (Throwable ignored) {}
    }

    public void setup() {


        cont.top();
        cont.clear();
        //   cont.button(Ozone.Core.bundle.get("ozone.javaEditor"), Icon.pencil, () -> {
        //      Ozone.Core.app.post(this::hide);
        //     Manifest.commFrag.toggle();
        // }).size(Ozone.Core.graphics.getWidth() / 6, Ozone.Core.graphics.getHeight() / 12);
        cont.row();
        cont.button(arc.Core.bundle.get("ozone.commandsUI"), Icon.commandRally, () -> {
            arc.Core.app.post(this::hide);
            Manifest.commFrag.toggle();
        }).size(arc.Core.graphics.getWidth() / 6, arc.Core.graphics.getHeight() / 12);
        cont.row();
        cont.table((s) -> {
            s.left();
            s.label(() -> arc.Core.bundle.get("Commands") + ": ");
            commandsField = s.field(commands, (res) -> commands = res).fillX().growX().get();
            s.button(Icon.zoom, () -> {
                Commands.call(Core.commandsPrefix + commands);
                //Commands.call(commands);
                commands = "";
                commandsField.clearText();
            });
        }).growX().fillX().padBottom(6.0F).bottom().size(arc.Core.graphics.getWidth(), arc.Core.graphics.getHeight() / 12);


        try {
            if (Vars.ui.hudfrag.shown)
                Reflect.getMethod(null, "toggleMenus", Vars.ui.hudfrag).invoke(Vars.ui.hudfrag);
        }catch (Throwable ignored) {
        }
    }
}
