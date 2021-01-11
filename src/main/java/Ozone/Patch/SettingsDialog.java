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
				if (f.getType().equals(boolean.class)) {
					gameTable.check(Core.bundle.get(name), (Boolean) f.get(null), b -> {
						try {
							f.set(null, b);
						}catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}).left().growX();
				}
				gameTable.row();
			}catch (Throwable t) {
				Sentry.captureException(t);
				Log.err(t);
				Log.err("Failed to load settings");
			}
		}
		/*
		for (Field f : Manifest.getSettings()) {
			try {
				f.setAccessible(true);
				if (boolean.class.equals(f.getType())) {
					gameTable.pref(new SettingsTable.Setting() {
						@Override
						public void add(SettingsTable settingsTable) {
							try {
								settingsTable.check(Core.bundle.get(f.getDeclaringClass().getName() + "." + f.getName()), f.getBoolean(null), s -> {
									try {
										f.set(null, s);
										Core.settings.put("ozone." + f.getName(), s);
									}catch (IllegalAccessException e) {
										Vars.ui.showException(e);
									}
								}).growX().row();
							}catch (Throwable e) {
								Sentry.captureException(e);
								Log.err(e);
							}
						}
					});
					gameTable.row();
				}else if (String.class.equals(f.getType())) {
					gameTable.pref(new SettingsTable.Setting() {
						@Override
						public void add(SettingsTable settingsTable) {
							try {
								settingsTable.label(() -> Core.bundle.get("String") + " " + Core.bundle.get("setting.ozone." + f.getName() + ".name")).pad(6.0F).left();
								settingsTable.field(Core.settings.getString("ozone." + f.getName(), (String) f.get(null)), s -> {
									try {
										f.set(null, s);
										Core.settings.put("ozone." + f.getName(), s);
									}catch (Throwable e) {
										Vars.ui.showException(e);
									}
								}).pad(6.0F).left();
								settingsTable.row();
							}catch (Throwable e) {
								Vars.ui.showException(e);
							}
						}
					});
					
				}else if (int.class.equals(f.getType())) {
					gameTable.pref(new SettingsTable.Setting() {
						@Override
						public void add(SettingsTable settingsTable) {
							try {
								settingsTable.label(() -> Core.bundle.get("Integer") + " " + Core.bundle.get("setting.ozone." + f.getName() + ".name")).pad(6.0F).left();
								settingsTable.field(String.valueOf(Core.settings.getInt("ozone" + f.getName(), f.getInt(null))), s -> {
									try {
										f.setInt(null, Integer.parseInt(s));
										Core.settings.put("ozone." + f.getName(), s);
									}catch (Throwable e) {
										Vars.ui.showException(e);
									}
								}).pad(6.0F).left();
								settingsTable.row();
							}catch (Throwable e) {
								Vars.ui.showException(e);
							}
						}
					});
				}else if (long.class.equals(f.getType())) {
					gameTable.pref(new SettingsTable.Setting() {
						@Override
						public void add(SettingsTable settingsTable) {
							try {
								settingsTable.label(() -> Core.bundle.get("Long") + " " + Core.bundle.get("setting.ozone." + f.getName() + ".name")).pad(6.0F).left();
								settingsTable.field(String.valueOf(Core.settings.getLong("ozone" + f.getName(), f.getLong(null))), s -> {
									try {
										f.setLong(null, Long.parseLong(s));
										Core.settings.put("ozone." + f.getName(), s);
									}catch (Throwable e) {
										Vars.ui.showException(e);
									}
								}).pad(6.0F).left();
								settingsTable.row();
							}catch (Throwable e) {
								Vars.ui.showException(e);
							}
						}
					});
				}else if (float.class.equals(f.getType())) {
					gameTable.pref(new SettingsTable.Setting() {
						@Override
						public void add(SettingsTable settingsTable) {
							try {
								settingsTable.label(() -> Core.bundle.get("Float") + " " + Core.bundle.get("setting.ozone." + f.getName() + ".name")).pad(6.0F).left();
								settingsTable.field(String.valueOf(Core.settings.getFloat("ozone" + f.getName(), f.getFloat(null))), s -> {
									try {
										f.setFloat(null, Float.parseFloat(s));
										Core.settings.put("ozone." + f.getName(), s);
									}catch (Throwable e) {
										Vars.ui.showException(e);
									}
								}).pad(6.0F).left();
								settingsTable.row();
							}catch (Throwable e) {
								Vars.ui.showException(e);
							}
						}
					});
				}
			}catch (Throwable t) {
				Sentry.captureException(t);
				Log.errTag("Ozone-Ozone.Settings", "Couldn't create settings for: ozone." + f.getName());
				Log.err(t);
			}
		}
		*/
	}
}
