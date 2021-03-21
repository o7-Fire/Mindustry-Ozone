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


import Atom.Reflect.Reflect;
import Ozone.Commands.CommandsCenter;
import Ozone.Manifest;
import Ozone.Patch.Translation;
import Ozone.Settings.BaseSettings;
import Ozone.Watcher.PlayTime;
import arc.Core;
import arc.input.KeyCode;
import arc.scene.style.Drawable;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import arc.util.Interval;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;

public class OzoneMenu extends BaseDialog {
	Interval interval = new Interval();
	private TextField commandsField;
	private String commands = "";
	private Table tB;
	
	public OzoneMenu(String title, DialogStyle style) {
		super(title, style);
		this.keyDown((key) -> {
			if (key == KeyCode.escape || key == KeyCode.back) {
				arc.Core.app.post(this::hide);
			}else if (key == KeyCode.enter) {
				CommandsCenter.call(BaseSettings.commandsPrefix + commands);
				commands = "";
				commandsField.clearText();
			}
		});
		this.shown(this::setup);
		this.onResize(this::setup);
		update(this::update);
		addCloseButton();
	}
	
	public static void showHud() {
		if (!Vars.ui.hudfrag.shown) toggleHUD();
	}
	
	public static void toggleHUD() {
		try {
			Reflect.getMethod(null, "toggleMenus", Vars.ui.hudfrag).invoke(Vars.ui.hudfrag);
		}catch (Throwable ignored) {
		}
	}
	
	void update() {
		if (!isShown()) return;
		if (!visible) return;
		if (!interval.get(40)) return;
		if (!Vars.mobile) arc.Core.scene.setKeyboardFocus(commandsField);
		
	}
	
	@Override
	public void hide() {
		super.hide();
		showHud();
	}
	
	public void setup() {
		
		
		cont.top();
		cont.clear();
		cont.row();
		cont.table(t -> tB = t).growX();
		cont.row();
		tB.button(Translation.get("ozone.commandsUI"), Icon.commandRally, () -> {
			arc.Core.app.post(this::hide);
			Manifest.commFrag.toggle();
		}).growX();
		ad(Manifest.worldInformation, Icon.fileTextFill);
		tB.row();
		ad(Manifest.taskList, Icon.list);
		ad(Manifest.modsMenu, Icon.file);
		cont.table((s) -> {
			s.left();
			s.label(() -> Translation.get("Commands") + ": ");
			commandsField = s.field(commands, (res) -> commands = res).fillX().growX().get();
			s.button(Icon.zoom, () -> {
				CommandsCenter.call(BaseSettings.commandsPrefix + commands);
				commands = "";
				commandsField.clearText();
			});
		}).growX();
		cont.row();
		cont.table((s) -> {
			s.right();
			s.button("Mark Tiles", Icon.pencil, () -> {
				hide();
				Core.app.post(PlayTime::markTiles);
			}).growX();
			s.button(Translation.get("Settings"), Icon.settings, () -> {
				Manifest.playSettings.show();
			}).growX();
		}).growX();
		
	}
	
	void ad(Table t, BaseDialog baseDialog, Drawable d) {
		t.button(Translation.get(baseDialog.getClass().getName()), d, () -> {
			hide();
			baseDialog.show();
		}).growX();
	}
	
	void ad(BaseDialog baseDialog, Drawable d) {
		tB.button(Translation.get(baseDialog.getClass().getName()), d, () -> {
			hide();
			baseDialog.show();
		}).growX();
	}
	
	void ad(BaseDialog baseDialog) {
		tB.button(Translation.get(baseDialog.getClass().getName()), () -> {
			hide();
			baseDialog.show();
		}).growX();
	}
	
	
}
