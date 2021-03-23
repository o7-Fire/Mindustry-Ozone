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

import Ozone.Experimental.Evasion.Identification;
import Ozone.Internal.Interface;
import Ozone.Manifest;
import Ozone.Patch.Translation;
import mindustry.Vars;
import mindustry.gen.Icon;

import java.util.ArrayList;

public class ModsMenu extends ScrollableDialog {
	static ArrayList<OzoneDialog> dialogs = new ArrayList<>();
	
	public ModsMenu() {
		super("Ozone Mods Menu");
	}
	
	public static void add(OzoneDialog dialog) {
		dialogs.add(dialog);
	}
	
	@Override
	protected void ctor() {
		super.ctor();
		addNavButton("o7-Discord", Icon.discord, () -> {
			Interface.openLink("https://discord.gg/2tqguRj");
		});
		addNavButton("Ozone-Wiki", Icon.bookOpen, () -> {
			Interface.openLink("https://github.com/o7-Fire/Mindustry-Ozone/wiki");
		});
	}
	
	public void setup() {
		table.clear();
		table.button("@mods", Icon.book, Vars.ui.mods::show).growX();// a sacrifice indeed
		table.row();
		table.row();
		generic();
		table.button("Reset UID", Icon.refresh, () -> Vars.ui.showConfirm("Reset UID", "Reset all uuid and usid", () -> {
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
		ad(Manifest.uiDebug);
		ad(Manifest.experiment);
		ad(Manifest.logView);
		ad(Manifest.taskList);
		ad(Manifest.bundleViewer);
		ad(Manifest.warning);
		for (OzoneDialog o : dialogs)
			ad(o);
	}
	
	public void ad(OzoneDialog dialog) {
		table.button(Translation.colorized(dialog.getTitle()), dialog.icon(), dialog::show).growX();
		table.row();
	}
}
