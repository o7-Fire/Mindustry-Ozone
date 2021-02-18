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

import Ozone.Experimental.Evasion.Identification;
import Ozone.Manifest;
import Ozone.Patch.Translation;
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
		cont.row();
		generic();
		cont.button("Reset UID", Icon.refresh, () -> Vars.ui.showConfirm("Reset UID", "Reset all uuid and usid", () -> {
			Vars.ui.loadfrag.show("Changing ID");
			Identification.changeID(() -> {
				Vars.ui.loadfrag.hide();
				try {
					Vars.ui.showInfo("Changed " + Identification.getKeys().size() + " ID");
				}catch (Throwable ignored) {}
			});
			
		})).growX().row();
		
	}
	
	void generic() {
		ad(Manifest.envInf);
		ad(Manifest.moduleFrag);
		ad(Manifest.uiDebug);
		ad(Manifest.experiment);
		ad(Manifest.logView);
		ad(Manifest.taskList);
	}
	
	void ad(OzoneDialog dialog) {
		cont.button(Translation.get(dialog.getClass().getName()), dialog.icon(), dialog::show).growX();
		cont.row();
	}
}
