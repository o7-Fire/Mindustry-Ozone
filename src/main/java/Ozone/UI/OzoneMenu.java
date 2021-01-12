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


import Atom.Reflect.Reflect;
import Ozone.Commands.Commands;
import Ozone.Manifest;
import Ozone.Settings.BaseSettings;
import arc.input.KeyCode;
import arc.scene.ui.TextField;
import arc.util.Interval;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;

public class OzoneMenu extends BaseDialog {
	Interval interval = new Interval();
	private TextField commandsField;
	private String commands = "";
	
	public OzoneMenu(String title, DialogStyle style) {
		super(title, style);
		this.keyDown((key) -> {
			if (key == KeyCode.escape || key == KeyCode.back) {
				arc.Core.app.post(this::hide);
			}else if (key == KeyCode.enter) {
				Commands.call(BaseSettings.commandsPrefix + commands);
				commands = "";
				commandsField.clearText();
			}
		});
		this.shown(this::setup);
		this.onResize(this::setup);
		update(this::update);
	}
	
	void update() {
		if (!isShown()) return;
		if (!interval.get(40)) return;
		try {
			if (!Vars.ui.hudfrag.shown) Reflect.getMethod(null, "toggleMenus", Vars.ui.hudfrag).invoke(Vars.ui.hudfrag);
		}catch (Throwable ignored) { }
		if (!Vars.ui.chatfrag.shown()) {
			Vars.ui.chatfrag.toggle();
		}
		arc.Core.scene.setKeyboardFocus(commandsField);
	}
	
	@Override
	public void hide() {
		super.hide();
		try {
			if (Vars.ui.chatfrag.shown()) {
				Vars.ui.chatfrag.toggle();
			}
			if (!Vars.ui.hudfrag.shown) Reflect.getMethod(null, "toggleMenus", Vars.ui.hudfrag).invoke(Vars.ui.hudfrag);
		}catch (Throwable ignored) {}
	}
	
	public void setup() {
		
		
		cont.top();
		cont.clear();
		//   cont.button(Ozone.Core.bundle.get("ozone.javaEditor"), Icon.pencil, () -> {
		//      Ozone.Core.app.post(this::hide);
		//     Manifest.commFrag.toggle();
		// }).size(Ozone.Core.graphics.getWidth() / 6, Ozone.Core.graphics.getHeight() / 12);
		cont.row();
		cont.button(arc.Core.bundle.get("ozone.commandsUI"), Icon.commandRally, () -> {
			arc.Core.app.post(this::hide);
			Manifest.commFrag.toggle();
		}).growX();
		cont.row();
		cont.button("World Information", Icon.fileTextFill, () -> Manifest.worldInformation.show()).growX();
		cont.row();
		cont.table((s) -> {
			s.left();
			s.label(() -> arc.Core.bundle.get("Commands") + ": ");
			commandsField = s.field(commands, (res) -> commands = res).fillX().growX().get();
			s.button(Icon.zoom, () -> {
				Commands.call(BaseSettings.commandsPrefix + commands);
				commands = "";
				commandsField.clearText();
			});
		}).growX();
		
		try {
			if (Vars.ui.hudfrag.shown) Reflect.getMethod(null, "toggleMenus", Vars.ui.hudfrag).invoke(Vars.ui.hudfrag);
		}catch (Throwable ignored) {
		}
	}
}
