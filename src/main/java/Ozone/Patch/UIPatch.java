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

package Ozone.Patch;

import Ozone.Internal.Module;
import Ozone.Manifest;
import Ozone.UI.CommandsListFrag;
import Ozone.UI.OzoneMenu;
import Ozone.UI.TaskList;
import Ozone.UI.WorldInformation;
import arc.scene.ui.Dialog;
import mindustry.Vars;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;

import java.util.ArrayList;
import java.util.Arrays;

public class UIPatch implements Module {
	public static Dialog.DialogStyle ozoneStyle;
	
	@Override
	public void init() throws Throwable {
		ozoneStyle = new Dialog.DialogStyle() {
			{
				stageBackground = Styles.none;
				titleFont = Fonts.def;
				background = Tex.windowEmpty;
				titleFontColor = Pal.accent;
			}
		};
		Vars.ui.settings = new SettingsDialog();//how to patch mindustry UI
		Manifest.taskList = new TaskList();
		Manifest.commFrag = new CommandsListFrag();
		Manifest.worldInformation = new WorldInformation();
		Manifest.menu = new OzoneMenu(arc.Core.bundle.get("ozone.hud"), ozoneStyle);
		Manifest.commFrag.build(Vars.ui.hudGroup);
	}
	
	@Override
	public ArrayList<Class<? extends Module>> dependOnModule() {
		return new ArrayList<>(Arrays.asList(Translation.class));
	}
}
