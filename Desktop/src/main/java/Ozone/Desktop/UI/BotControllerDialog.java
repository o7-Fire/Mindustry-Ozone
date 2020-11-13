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

import Bot.BotClient;
import Ozone.Desktop.BotController;
import arc.Core;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;


public class BotControllerDialog extends BaseDialog {

    public BotControllerDialog() {
        super("Bot Controller");
        addCloseButton();
        setup();
        shown(this::setup);
        onResize(this::setup);
    }

    public void setup() {
        cont.clear();
        Table t = new Table();
        Table botList = new Table();
        t.labelWrap("Main Port:");
        t.field(String.valueOf(BotController.port), s -> {
            try {
                BotController.port = Integer.parseInt(s);
            } catch (NumberFormatException n) {
                Vars.ui.showException(n);
            }
        }).growX().addInputDialog(5);
        t.row();
        t.button(Icon.add, this::addBot);
        if (BotController.botClients.isEmpty())
            botList.labelWrap("Such an empty");
        else {
            for (BotClient b : BotController.botClients) {
                Table bot = new Table();
                bot.labelWrap("Name:");
                bot.labelWrap(b.name);
                bot.row();
                bot.labelWrap("ID:");
                bot.labelWrap(String.valueOf(b.getId()));
                bot.button(Icon.admin, () -> {
                    Core.app.post(this::hide);
                    new BotInfoDialog(b).show();
                });

                botList.addChild(bot);
            }
        }
        cont.button(Icon.refresh, this::setup);
        cont.row();
        cont.add(t);
        cont.row();
        cont.add(botList);
    }

    private void addBot() {
        try {
            Vars.ui.showTextInput("Create New Bot", "Choose a name", Vars.maxNameLength, Vars.player.name() + BotController.botClients.size(), true, BotController::launchBot);
        } catch (Throwable t) {
            Vars.ui.showException(t);
        }
    }

    public static class BotInfoDialog extends BaseDialog {
        BotClient botClient;
        Label log = new Label("Log");

        public BotInfoDialog(BotClient b) {
            super(b.name);
            botClient = b;
            addCloseButton();
            setup();
            shown(this::setup);
            onResize(this::setup);
            update(this::update);
        }

        public void setup() {
            cont.clear();
            cont.labelWrap("Name:");
            cont.label(() -> botClient.name);
            cont.row();
            cont.labelWrap("ID:");
            cont.label(() -> String.valueOf(botClient.getId()));
            cont.row();
            cont.labelWrap("Status:");
            cont.label(() -> botClient.getStatus().toString());
            cont.row();
            cont.button("Launch", Icon.box, () -> {
                try {
                    botClient.launch();
                } catch (Throwable e) {
                    e.printStackTrace();
                    Sentry.captureException(e);
                    Vars.ui.showException(e);
                }
            }).disabled(botClient.launched());
            cont.button("Connect", Icon.play, () -> {
                try {
                    botClient.connect();
                } catch (Throwable e) {
                    e.printStackTrace();
                    Sentry.captureException(e);
                    Vars.ui.showException(e);
                }
            }).disabled(i -> !botClient.launched() || botClient.connected());
            cont.add(log);
        }

        public void update() {
            log.clear();
            log.setText(botClient.sb.toString());
        }

    }
}
