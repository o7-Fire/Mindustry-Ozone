/*******************************************************************************
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
 ******************************************************************************/

package Ozone.UI;

import Ozone.Bot.VirtualController;
import Ozone.Bot.VirtualPlayer;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.gen.Tex;

public class VirtualControllerDialog extends ScrollableDialog {
	@Override
	protected void ctor() {
		super.ctor();
		addNavButton("Create Virtual Player", Icon.add, this::create);
	}
	
	@Override
	protected void setup() {
		table.button("[red]WIP", () -> {
		}).center().growX().disabled(true);
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
		VirtualController.create();
	}
	
	void ad(VirtualPlayer virtualPlayer) {
		table.table(t -> {
			
			t.setBackground(Tex.pane);
			t.table(t2 -> {
				t2.labelWrap("Name: " + virtualPlayer.name()).growX().left();
				t2.row();
				t2.labelWrap("Status: " + virtualPlayer.state.toString()).growX().left();
				t2.row();
				t2.labelWrap("ID: " + virtualPlayer.vid()).growX().left();
			}).growX();
			t.row();
			t.table(t2 -> {
				t.button("More", Icon.admin, () -> new VirtualPlayerInterface(virtualPlayer).show()).growX();
				t.button("Remove", Icon.cancel, () -> {
					VirtualController.delete(virtualPlayer);
					init();
				}).growX();
				t.button("Clone", Icon.copy, () -> {
					try {
						VirtualController.clone(virtualPlayer);
						init();
					}catch (Throwable te) {
						Vars.ui.showException(te);
					}
				}).growX();
			}).growX();
			
		}).growX().row();
	}
}
