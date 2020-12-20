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

package Bot.UI;

import Bot.Manifest;
import Bot.Status;
import arc.scene.ui.Label;
import arc.util.Interval;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;

public class BotControllerUI extends BaseDialog {
	private Interval timer = new Interval();
	private Label ping = new Label("0 ms"), status = new Label(Status.ONLINE.toString()), id = new Label("0");
	
	public BotControllerUI() {
		super("Bot Status");
		addCloseButton();
		buttons.button("Refresh", Icon.refresh, this::setup).size(210f, 64f);
		setup();
		shown(this::setup);
		onResize(this::setup);
		update(this::update);
	}
	
	private void update() {
		if (isShown() && timer.get(200)) {
			status.setText(Manifest.getStatus().toString());
			long s = System.currentTimeMillis();
			try {
				Manifest.serverInterface.alive();
			}catch (Throwable ignored) {
			}
			s = System.currentTimeMillis() - s;
			ping.setText(s + " ms");
			id.setText(Manifest.oxygen.getID() + "");
		}
	}
	
	public void setup() {
		cont.clear();
		cont.labelWrap("Host Name:").growX().left();
		cont.label(() -> System.getProperty("ServerRegName")).growX().right();
		cont.row();
		cont.labelWrap("Host Port:").growX().left();
		cont.label(() -> System.getProperty("ServerRegPort")).growX().right();
		cont.row();
		cont.labelWrap("Name:").growX().left();
		cont.label(() -> System.getProperty("BotName")).growX().right();
		cont.row();
		cont.labelWrap("ID:").growX().left();
		cont.add(id).growX().right();
		cont.row();
		cont.labelWrap("Status:").growX().left();
		cont.add(status).growX().right();
		cont.row();
		cont.labelWrap("Ping:").growX().left();
		cont.add(ping).growX().right();
	}
}
