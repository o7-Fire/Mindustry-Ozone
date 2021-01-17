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

import Atom.Utility.Pool;
import Atom.Utility.Random;
import Ozone.Manifest;
import Ozone.Settings.BaseSettings;
import arc.Core;
import arc.scene.style.Drawable;
import arc.scene.ui.Label;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import io.sentry.Sentry;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;

import java.util.Map;
import java.util.concurrent.Callable;

public abstract class ScrollableDialog extends OzoneDialog {
	Table table = new Table();
	ScrollPane scrollPane = new ScrollPane(table);
	
	public ScrollableDialog(String title, DialogStyle style) {
		super(title, style);
	}
	
	public ScrollableDialog(String title) {
		super(title);
	}
	
	abstract void setup();
	
	protected void init() {
		scrollPane = new ScrollPane(table);
		cont.clear();
		table.clear();
		setup();
		cont.add(scrollPane).growX().growY();
	}
	
	void ad(Map<String, ?> map) {
		for (Map.Entry<String, ?> s : map.entrySet())
			ad(s.getKey(), s.getValue());
	}
	
	void ad(String name, Drawable i, BaseDialog bd) {
		table.button(name, i, bd::show).growX();
		table.row();
	}
	
	void ad(OzoneDialog od) {
		ad(od.icon(), od);
	}
	
	void ad(Drawable i, BaseDialog od) {
		ad(od.title.getText().toString(), i, od);
	}
	
	void ad(BaseDialog bd) {
		ad(bd.title.getText().toString(), bd);
	}
	
	void ad(String name, BaseDialog od) {
		table.button(name, Icon.info, od::show).growX();
		table.row();
	}
	
	void ad(String title, Callable<Object> callable) {
		Pool.submit(() -> {
			try {
				ad(title, callable.call());
			}catch (Throwable e) {
				e.printStackTrace();
				Sentry.captureException(e);
			}
		});
	}
	
	void ad(String text) {
		table.add(text).growX();
		table.row();
	}
	
	void ad(String title, Object value) {
		if (value == null) value = "null";
		if (BaseSettings.colorPatch) title = "[" + Random.getRandomHexColor() + "]" + title;
		Label l = new Label(title + ":");
		table.add(l).growX();
		String finalValue = String.valueOf(value);
		table.row();
		table.field(finalValue, s -> {
			setup();
			Core.app.setClipboardText(finalValue);
			Manifest.toast("Copied");
		}).expandX().growX();
		table.row();
	}
}
