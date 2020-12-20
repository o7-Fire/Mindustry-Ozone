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
import Bot.Status;
import Ozone.Desktop.BotController;
import arc.Core;
import arc.scene.ui.Label;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.SettingsDialog;
import arc.scene.ui.layout.Table;
import arc.util.Interval;
import arc.util.Strings;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.dialogs.BaseDialog;


public class BotControllerDialog extends OzoneBaseDialog {
	private final Settings settings = new Settings();
	private Interval timer = new Interval();
	
	public BotControllerDialog() {
		super("Bot Controller");
		buttons.button("Settings", Icon.settings, settings::show).size(210f, 64f);
		buttons.button("Refresh", Icon.refresh, this::setup).size(210f, 64f);
		buttons.button("Create Bot", Icon.add, this::addBot).size(210f, 64f);
		buttons.button("Start Server", Icon.add, this::startServer).size(210f, 64f).disabled(s -> BotController.serverStarted());
	}
	
	void update() {
		if (isShown() && timer.get(200)) setup();
	}
	
	//new TextureRegionDrawable(new TextureRegion(Core.assets.get("sprites/schematic-background.png", Texture.class)))
	void setup() {
		cont.clear();
		if (BotController.botClients.isEmpty()) {
			cont.button("Such an empty, go create a [orange]Bot", () -> {
			}).center().growX().disabled(true);
		}else {
			Table container = new Table();
			for (BotClient b : BotController.botClients) {
				Table bot = new Table(Tex.pane);
				bot.labelWrap("Name:").growX().left();
				bot.labelWrap(b.name).growX().right();
				bot.row();
				bot.labelWrap("Status:").growX().left();
				bot.labelWrap(b.getStatus().toString()).growX().right();
				bot.row();
				bot.button("More", Icon.admin, () -> {
					new BotInfoDialog(b).show();
				}).growX().left();
				bot.button("Remove", Icon.cancel, () -> {
					b.exit();
					BotController.botClients.remove(b);
					setup();
				}).growX().right();
				container.add(bot).growX();
				container.row();
			}
			cont.add(new ScrollPane(container)).growX();
		}
	}
	
	private void startServer() {
		try {
			BotController.startServer();
		}catch (Throwable e) {
			e.printStackTrace();
			Sentry.captureException(e);
			Vars.ui.showException("Failed to start BotServer", e);
		}
	}
	
	private void addBot() {
		
		Vars.ui.showTextInput("Create New Bot", "Choose a name", Vars.maxNameLength, Vars.player.name() + BotController.botClients.size(), true, s -> {
			try {
				BotController.createBot(s);
				setup();
			}catch (Throwable t) {
				Vars.ui.showException(t);
			}
		});
		
	}
	
	public static class BotInfoDialog extends BaseDialog {
		BotClient botClient;
		Label log = new Label("Log"), ping = new Label("0 ms"), status = new Label(Status.OFFLINE.toString()), id = new Label("0");
		Interval timer = new Interval();
		
		public BotInfoDialog(BotClient b) {
			super("Bot " + Strings.stripColors(b.name));
			botClient = b;
			addCloseButton();
			buttons.button("Refresh", Icon.refresh, this::setup).size(210f, 64f);
			setup();
			shown(this::setup);
			onResize(this::setup);
			update(this::update);
		}
		
		public void setup() {
			cont.clear();
			cont.labelWrap("Name:").growX().left();
			cont.label(() -> botClient.name).growX().right();
			cont.button("Launch", Icon.box, () -> {
				if (!BotController.serverStarted()) {
					Vars.ui.showInfo("Start server first");
					return;
				}
				try {
					botClient.launch();
					setup();
				}catch (Throwable e) {
					e.printStackTrace();
					Sentry.captureException(e);
					Vars.ui.showException("Error while launching bot", e);
				}
			}).disabled(botClient.launched()).growX();
			cont.row();
			cont.labelWrap("ID:").growX().left();
			cont.add(id).growX().right();
			cont.button("Connect", Icon.play, () -> {
				try {
					botClient.connect().alive();
					setup();
				}catch (Throwable e) {
					e.printStackTrace();
					Sentry.captureException(e);
					Vars.ui.showException("Error while connecting to bot", e);
				}
			}).disabled(i -> !botClient.launched() || botClient.connected()).growX();
			cont.row();
			cont.labelWrap("Status:").growX().left();
			cont.add(status).growX().right();
			cont.button("Commands", Icon.commandAttack, () -> {
			
			}).disabled(i -> !botClient.launched() || !botClient.connected()).growX();
			cont.row();
			cont.labelWrap("Ping:").growX().left();
			cont.add(ping).growX().right();
			cont.button("Kill", Icon.cancel, () -> {
				botClient.exit();
				BotController.botClients.remove(botClient);
				hide();
			}).disabled(i -> !botClient.launched() && !botClient.connected()).growX();
			cont.row();
			cont.labelWrap("PID:").growX().left();
			cont.label(() -> botClient.launched() ? botClient.process.pid() + "" : "doesn't exist");
			cont.row();
			cont.add(new ScrollPane(log)).width((Core.graphics.getWidth() * 5) / 8).growY().left().bottom();
		}
		
		public void update() {
			if (this.isShown() && timer.get(40) && (botClient.launched() || botClient.connected())) {
				status.setText(botClient.getStatus().toString());
				try {
					long s = System.currentTimeMillis();
					botClient.getRmi().alive();
					s = System.currentTimeMillis() - s;
					ping.setText(s + " ms");
				}catch (Throwable ignored) {
				}
				id.setText(botClient.getId() + "");
				synchronized (botClient.sb) {
					log.setText(botClient.sb.toString());
				}
			}
		}
		
		
	}
	
	public static class Settings extends BaseDialog {
		public Settings() {
			super("Settings");
			addCloseButton();
			setup();
			shown(this::setup);
			onResize(this::setup);
		}
		
		public void setup() {
			cont.clear();
			SettingsDialog.SettingsTable table = new SettingsDialog.SettingsTable();
			cont.labelWrap("Main Port:").growX().left();
			cont.field(String.valueOf(BotController.port), s -> {
				try {
					BotController.port = Integer.parseInt(s);
				}catch (NumberFormatException n) {
					Vars.ui.showException(n);
				}
			}).growX().addInputDialog(5).right();
			cont.row();
			cont.labelWrap("Main Host Name:").growX().left();
			cont.field(BotController.base, s -> BotController.base = s).growX().addInputDialog(64).right();
			cont.add(table);
		}
	}
}
