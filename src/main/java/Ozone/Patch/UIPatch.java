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

package Ozone.Patch;

import Atom.Reflect.Reflect;
import Atom.Utility.Random;
import Atom.Utility.Utility;
import Ozone.Experimental.Evasion.Identification;
import Ozone.Internal.Module;
import Ozone.Manifest;
import Ozone.Patch.Mindustry.LogicDialogPatch;
import Ozone.UI.*;
import Shared.SharedBoot;
import arc.Core;
import arc.Events;
import arc.scene.ui.Dialog;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.ui.MobileButton;
import mindustry.ui.Styles;
import mindustry.ui.fragments.MenuFragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

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
		
		Vars.ui.settings.game.row();
		Vars.ui.settings.game.table(gameTable -> {
			gameTable.row();
			gameTable.table(this::h).growX().row();
			Vars.ui.settings.hidden(Manifest::saveSettings);
			gameTable.button("Save Ozone Settings", Manifest::saveSettings).growX().row();
			gameTable.button("Reset UID", () -> {
				try {
					Identification.changeID();
					Vars.ui.showInfo("Successful");
				}catch (Throwable t) {
					Vars.ui.showException(t);
					Sentry.captureException(t);
				}
			}).growX();
		}).center();
		Manifest.taskList = new TaskList();
		Manifest.warning = new Warning();
		Manifest.bundleViewer = new BundleViewer();
		Manifest.commFrag = new CommandsListFrag();
		Manifest.worldInformation = new WorldInformation();
		Manifest.playSettings = new OzonePlaySettings();
		Manifest.menu = new OzoneMenu(Translation.get("ozone.hud"), ozoneStyle);
		Manifest.moduleFrag = new ModuleFrag();
		Manifest.envInf = new EnvironmentInformation();
		Manifest.logView = new LogView();
		Manifest.uiDebug = new UILayout();
		Manifest.experiment = new ExperimentDialog();
		ModsMenu.add(new VirtualControllerDialog());
		Manifest.modsMenu = new ModsMenu();
		Manifest.commFrag.build(Vars.ui.hudGroup);
		ui.logic = new LogicDialogPatch();
		Events.on(EventType.ResizeEvent.class, c -> {
			onResize();
		});
		onResize();
	}
	
	private void onResize() {
		if (VarsPatch.menu != null) {
			if (Vars.testMobile) try {
				Reflect.getMethod(MenuFragment.class, "buildMobile", ui.menufrag).invoke(ui.menufrag);
			}catch (Throwable ignored) {}
			if (Vars.mobile || Vars.testMobile) {
				if (Core.graphics.isPortrait()) VarsPatch.menu.row();
				VarsPatch.menu.add(new MobileButton(Icon.info, Translation.get("Ozone"), () -> Manifest.modsMenu.show()));
			}else {
				if (!SharedBoot.isCore()) {
					VarsPatch.menu.button(Translation.get("Update"), Icon.refresh, Updater::showUpdateDialog).growX().update(t -> {
						if (Updater.releaseMap != null)
							t.setText("Update" + Utility.repeatThisString("!", Random.getInt(5)));
					}).bottom();
				}
				VarsPatch.menu.button(Translation.get("Ozone"), Icon.file, Manifest.modsMenu::show).growX().bottom();
			}
		}
	}
	
	void h(Table gameTable) {
		for (Field f : Manifest.getSettings()) {
			String name = f.getDeclaringClass().getName() + "." + f.getName();
			gameTable.left();
			try {
				f.setAccessible(true);
				Class<?> type = f.getType();
				if (type.equals(boolean.class)) {
					gameTable.check(Translation.get(name), (Boolean) f.get(null), b -> {
						try {
							f.set(null, b);
						}catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}).left();
					gameTable.row();
					continue;
				}
				gameTable.label(() -> Translation.get(name) + ": ").left().growX().row();
				gameTable.field(f.get(null).toString(), s -> {
					try {
						Object o = Reflect.parseStringToPrimitive(s, f.getType());
						if (o != null) f.set(null, o);
					}catch (NumberFormatException t) {
						Vars.ui.showException("Failed to parse", t);//100% user fault
					}catch (Throwable t) {
						Vars.ui.showException(t);
						Sentry.captureException(t);
					}
				}).growX().left().row();
			}catch (Throwable t) {
				Sentry.captureException(t);
				Log.err(t);
				Log.err("Failed to load settings");
			}
		}
	}
	
	@Override
	public ArrayList<Class<? extends Module>> dependOnModule() {
		return new ArrayList<>(Arrays.asList(Translation.class, VarsPatch.class));
	}
}
