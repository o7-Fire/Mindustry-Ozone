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

package Ozone.UI;

import Ozone.Bot.VirtualController;
import Ozone.Bot.VirtualPlayer;
import mindustry.gen.Icon;
import mindustry.gen.Tex;

public class VirtualControllerDialog extends ScrollableDialog {
	@Override
	protected void ctor() {
		super.ctor();
		buttons.button("Create Virtual Player", Icon.add, this::create).size(210f, 64f);
	}
	
	@Override
	protected void setup() {
		if (VirtualController.virtualPlayers.size() == 0) {
			table.button("Such an empty, go create a [orange]Virtual Player", () -> {
			}).center().growX().disabled(true);
		}else {
			for (VirtualPlayer v : VirtualController.virtualPlayers) {
				ad(v);
			}
		}
	}
	
	void create() {
	
	}
	
	void ad(VirtualPlayer virtualPlayer) {
		table.table(t -> {
			t.setBackground(Tex.pane);
			t.labelWrap("Name:").growX().left();
			t.labelWrap(virtualPlayer.name).growX().right();
			t.row();
			t.labelWrap("Status:").growX().left();
			t.labelWrap(virtualPlayer.state.getState().toString()).growX().right();
			t.row();
			t.button("More", Icon.admin, () -> new VirtualPlayerInterface(virtualPlayer).show()).growX().left();
			t.button("Remove", Icon.cancel, () -> {
				VirtualController.delete(virtualPlayer);
				init();
			}).growX().right();
		}).growX().growY();
	}
}
