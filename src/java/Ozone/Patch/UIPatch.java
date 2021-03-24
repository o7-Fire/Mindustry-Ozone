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
import Atom.Utility.Digest;
import Atom.Utility.Random;
import Atom.Utility.Utility;
import Ozone.Experimental.Evasion.Identification;
import Ozone.Internal.AbstractModule;
import Ozone.Internal.InformationCenter;
import Ozone.Internal.Interface;
import Ozone.Manifest;
import Ozone.UI.*;
import Shared.SharedBoot;
import Shared.WarningHandler;
import arc.Core;
import arc.Events;
import arc.scene.ui.Dialog;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import io.sentry.Sentry;
import io.sentry.UserFeedback;
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

import static mindustry.Vars.ui;

public class UIPatch extends AbstractModule {
	public static Dialog.DialogStyle ozoneStyle;
	
	{
		dependsOn.add(Translation.class);
		dependsOn.add(VarsPatch.class);
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
						WarningHandler.handleMindustry(t);
					}
				}).growX().left().row();
			}catch (Throwable t) {
				WarningHandler.handleMindustry(t);
				Log.err("Failed to load settings");
			}
		}
	}
	
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
		if (SharedBoot.test) return;
		Manifest.taskList = new TaskList();
		Manifest.warning = new Warning();
		Manifest.bundleViewer = new BundleViewer();
		Manifest.commFrag = new CommandsListFrag();
		Manifest.worldInformation = new WorldInformation();
		Manifest.playSettings = new OzonePlaySettings();
		Manifest.menu = new OzoneMenu(Translation.get("ozone.hud"), ozoneStyle);
		Manifest.envInf = new EnvironmentInformation();
		Manifest.logView = new LogView();
		Manifest.uiDebug = new UILayout();
		Manifest.experiment = new ExperimentDialog();
		ModsMenu.add(new VirtualControllerDialog());
		if (SharedBoot.debug) {
			ModsMenu.add(new ModuleFrag());
			ModsMenu.add(new CommandsListDebug());
		}
		Manifest.modsMenu = new ModsMenu();
		Manifest.commFrag.build(Vars.ui.hudGroup);
		ui.settings.game.row();
		ui.settings.game.table(gameTable -> {
			gameTable.row();
			gameTable.table(this::h).growX().row();
			Vars.ui.settings.hidden(Manifest::saveSettings);
			gameTable.button("Ozone Mods Menu", Manifest.modsMenu::show).growX().row();
			gameTable.button("Save Ozone Settings", Manifest::saveSettings).growX().row();
			gameTable.button("Reset UID", () -> {
				try {
					Identification.changeID();
					Vars.ui.showInfo("Successful");
				}catch (Throwable t) {
					Vars.ui.showException(t);
					WarningHandler.handleProgrammerFault(t);
					
				}
			}).growX();
		}).center();
		ui.logic.buttons.button("Show Hash", Icon.list, () -> {
			new ScrollableDialog("Hash Code") {
				@Override
				protected void setup() {
					String src = ui.logic.canvas.save();
					int hash = src.hashCode();
					long lhash = Digest.longHash(src);
					table.button(hash + "", () -> {
						Interface.copy(hash + "");
					}).tooltip("Copy").growY();
					table.button(lhash + "", () -> {
						Interface.copy(lhash + "");
					}).tooltip("Copy").growY();
				}
			}.show();
		}).size(210f, 64f);
		ui.logic.buttons.button("Report to Ozone-Sentry", Icon.fileText, () -> {
			Interface.showInput("Reason ?", s -> {
				String src = ui.logic.canvas.save();
				long Lhash = Digest.longHash(src);
				int hash = src.hashCode();
				UserFeedback feedback = new UserFeedback(Sentry.captureMessage("Logic-Code-Report-" + hash));
				feedback.setName("Reporter-" + Vars.player.name.hashCode());
				StringBuilder sb = new StringBuilder();
				sb.append("LHash:").append(Lhash).append("\n");
				sb.append("Hash:").append(hash).append("\n");
				sb.append("Reason:").append(s).append("\n");
				if (Vars.net.active())
					sb.append("server:").append(InformationCenter.getCurrentServerIP()).append(":").append(InformationCenter.getCurrentServerPort());
				feedback.setComments(sb.toString());
				Sentry.captureUserFeedback(feedback);
				Interface.toast("Sent: " + "Hash-" + hash);
			});
		}).size(210f, 64f);
		
		Events.on(EventType.ResizeEvent.class, c -> {
			onResize();
		});
		onResize();
	}
	
}
