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

import Atom.Utility.Encoder;
import Atom.Utility.Utility;
import Ozone.Internal.Module;
import Ozone.Manifest;
import Ozone.Patch.Mindustry.SettingsDialog;
import Ozone.UI.CommandsListFrag;
import Ozone.UI.OzoneMenu;
import Ozone.UI.TaskList;
import Ozone.UI.WorldInformation;
import Shared.SharedBoot;
import arc.Events;
import arc.scene.Element;
import arc.scene.ui.Dialog;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static mindustry.Vars.ui;

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
		Manifest.menu = new OzoneMenu(Translation.get("ozone.hud"), ozoneStyle);
		Manifest.commFrag.build(Vars.ui.hudGroup);
		Events.on(EventType.ResizeEvent.class, c -> {
			onResize();
		});
		onResize();
	}
	
	private void onResize() {
		if (VarsPatch.menu != null) {
			VarsPatch.menu.button("Update", Icon.refresh, () -> {
				try {
					StringBuilder sb = new StringBuilder();
					InputStream is;
					if (Updater.newRelease.get()) is = Updater.getRelease(true).openStream();
					else is = Updater.getBuild(true).openStream();
					HashMap<String, String> h = Encoder.parseProperty(is);
					h.put("TimeStamp", Utility.getDate(Long.parseLong(h.get("TimeMilis"))));
					for (Map.Entry<String, String> e : h.entrySet()) {
						sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n");
					}
					if (Updater.newRelease.get() && Updater.newBuild.get()) {
						ui.showCustomConfirm("Choose", "", "Release", "Build", () -> ui.showConfirm("New Release", "A new compatible release appeared\n" + sb.toString(), () -> Updater.update(Updater.getRelease(SharedBoot.type + ".jar"))), () -> ui.showConfirm("New Build", "A new compatible build appeared\n" + sb.toString(), () -> Updater.update(Updater.getBuild(false))));
						return;
					}
					
					if (Updater.newRelease.get()) {
						ui.showConfirm("New Release", "A new compatible release appeared\n" + sb.toString(), () -> Updater.update(Updater.getRelease(SharedBoot.type + ".jar")));
					}else {
						ui.showConfirm("New Build", "A new compatible build appeared\n" + sb.toString(), () -> Updater.update(Updater.getBuild(false)));
					}
				}catch (Throwable t) {
					ui.showException(t);
					Sentry.captureException(t);
				}
			}).growX().bottom().name("buildcheck").visible(() -> Updater.newBuild.get() || Updater.newRelease.get()).update(Element::updateVisibility);
			VarsPatch.menu.button("Ozone-Menu", Icon.file, () -> Manifest.menu.show()).growX().bottom();
			//VarsPatch.menu.table(t->{ }).growY();
		}
	}
	
	@Override
	public ArrayList<Class<? extends Module>> dependOnModule() {
		return new ArrayList<>(Arrays.asList(Translation.class, VarsPatch.class));
	}
}
