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

import Shared.WarningHandler;
import arc.Core;
import arc.graphics.Color;
import arc.scene.Action;
import arc.scene.Scene;
import arc.scene.style.Drawable;
import arc.scene.ui.Dialog;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Collapser;
import arc.util.Align;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

public abstract class OzoneDialog extends BaseDialog {
	protected Drawable icon = Icon.commandRallySmall;
	
	public OzoneDialog() {
		this("gay");
		title.setText(this.getClass().getSimpleName());
	}
	
	public OzoneDialog(String title, DialogStyle style) {
		super(title, style);
		ctor();
	}
	
	public OzoneDialog(String title) {
		super(title);
		ctor();
	}
	
	protected void ctor() {
		update(this::update);
		addCloseButton();
		
	}
	
	
	protected Cell<TextButton> addNavButton(String name, Drawable icon, Runnable doSmth) {
		if (Vars.mobile) return buttons.button(name, icon, doSmth).growX();
		else return buttons.button(name, icon, doSmth).size(210f, 64f);
		
	}
	
	@Override
	public void addCloseButton() {
		addNavButton("@back", Icon.left, this::hide);
		addCloseListener();
	}
	
	public String getTitle() {
		if (!title.getText().toString().equals("gay")) return title.getText().toString();
		return this.getClass().getSimpleName();
	}
	
	public Drawable icon() {
		return icon;
	}
	
	protected Dialog catchE(Throwable t) {
		try {
			WarningHandler.handleMindustry(t);
			hide();
			Vars.ui.loadfrag.hide();
		}catch (Throwable ignored) {}
		String text = t.getClass().getName();
		Throwable exc = t;
		try {
			return new Dialog("") {{
				String message = Strings.getFinalMessage(exc);
				setFillParent(true);
				cont.margin(15);
				cont.add("@error.title").colspan(2);
				cont.row();
				cont.image().width(300f).pad(2).colspan(2).height(4f).color(Color.scarlet);
				cont.row();
				cont.add((text.startsWith("@") ? Core.bundle.get(text.substring(1)) : text) + (message == null ? "" : "\n[lightgray](" + message + ")")).colspan(2).wrap().growX().center().get().setAlignment(Align.center);
				cont.row();
				
				Collapser col = new Collapser(base -> base.pane(t -> t.margin(14f).add(Strings.neatError(exc)).color(Color.lightGray).left()), true);
				
				cont.button("@details", Styles.togglet, col::toggle).size(180f, 50f).checked(b -> !col.isCollapsed()).fillX().right();
				cont.button("@ok", this::hide).size(110, 50).fillX().left();
				cont.row();
				cont.add(col).colspan(2).pad(2);
				closeOnBack();
			}}.show();
		}catch (Throwable ignored) { }
		return null;
	}
	
	@Override
	public Dialog show(Scene stage, Action action) {
		try {
			return super.show(stage, action);
		}catch (Throwable t) {
			return catchE(t);
		}
	}
	
	
	protected void update() {
	
	}
	
}
