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

import Atom.Reflect.Reflect;
import Ozone.Manifest;
import arc.Core;
import arc.util.Log;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.ui.dialogs.SettingsMenuDialog;

import java.lang.reflect.Field;

public class SettingsDialog extends SettingsMenuDialog {
	public SettingsDialog() {
		super();
		SettingsTable gameTable = Reflect.getField(SettingsMenuDialog.class, "game", this);
		if (gameTable == null) {
			Log.errTag("Ozone", "Can't get SettingsTable");
			return;
		}
		gameTable.row();
		h(gameTable);
		hidden(Manifest::saveSettings);
		gameTable.button("Save Ozone Settings", Manifest::saveSettings).growX();
	}
	
	static void h(SettingsTable gameTable) {
		for (Field f : Manifest.getSettings()) {
			String name = f.getDeclaringClass().getName() + "." + f.getName();
			try {
				f.setAccessible(true);
				Class<?> type = f.getType();
				if (type.equals(boolean.class)) {
					gameTable.check(Core.bundle.get(name), (Boolean) f.get(null), b -> {
						try {
							f.set(null, b);
						}catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}).left().growX();
					gameTable.row();
					continue;
				}
				gameTable.label(() -> Core.bundle.get(name)).growX();
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
				}).growX();
				gameTable.row();
			}catch (Throwable t) {
				Sentry.captureException(t);
				Log.err(t);
				Log.err("Failed to load settings");
			}
		}
	}
}
