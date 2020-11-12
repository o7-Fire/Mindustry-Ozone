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

import Bot.Bot;
import Ozone.Desktop.BotController;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.ui.dialogs.BaseDialog;


public class BotControllerDialog extends BaseDialog {

    public BotControllerDialog(String title) {
        super(title);
        addCloseButton();
        setup();
        shown(this::setup);
        onResize(this::setup);
    }

    public void setup() {
        cont.clear();
        Table t = new Table();
        t.labelWrap("Main Port");
        t.field(String.valueOf(BotController.port), s -> {
            try {
                BotController.port = Integer.parseInt(s);
            } catch (NumberFormatException n) {
                Vars.ui.showException(n);
            }
        }).growX().addInputDialog(5);
        Table botList = new Table();
        if (BotController.bots.isEmpty())
            botList.labelWrap("Such an empty");
        else {
            for (Bot b : BotController.bots) {

            }
        }
    }
}
