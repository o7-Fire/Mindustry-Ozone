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


import Ozone.Experimental.Evasion.Identification;
import Ozone.Internal.InformationCenter;
import Ozone.Patch.Updater;
import Ozone.Propertied;
import Ozone.Settings.SettingsManifest;
import Ozone.Watcher.LogicCodeEScanner;
import Ozone.Watcher.MostWanted;
import Shared.WarningHandler;
import arc.Core;
import arc.audio.Sound;
import arc.scene.style.TextureRegionDrawable;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.ui.dialogs.BaseDialog;
import net.jpountz.lz4.LZ4Factory;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

public class EnvironmentInformation extends ScrollableDialog {
	
	boolean b;
	
	public EnvironmentInformation() {
		super("Environment Information");
		icon = Icon.info;
		shown(this::init);
	}
	
	@Override
	protected void ctor() {
		super.ctor();
		BaseDialog mostWanted = new ScrollableDialog("Most Wanted") {
			
			@Override
			protected void setup() {
				synchronized (MostWanted.mostWanted) {
					for (String s : MostWanted.mostWanted)
						table.add(s).growX().row();
				}
			}
		};
		addNavButton("Most Wanted", Icon.players, mostWanted::show);
	}
	
	protected void setup() {
		ad("Player Name", Vars.player.name);
		ad("UUID", Core.settings.getString("uuid"), s -> {
			if (Identification.getRandomUID().length() == s.length()) Core.settings.put("uuid", s);
		});
		ad("Current Millis", System.currentTimeMillis());
		ad("Current Nanos", System.nanoTime());
		ad("Current Jar", InformationCenter.getCurrentJar().getAbsolutePath());
		ad("Ozone Settings", SettingsManifest.settingsFile.getAbsolutePath());
		ad("Fastest LZ4 Decompressor", LZ4Factory.fastestInstance().fastDecompressor().getClass().getName());
		ad("Fastest LZ4 Compressor", LZ4Factory.fastestInstance().fastCompressor().getClass().getName());
		try {
			ad("Compilation Time Total (ms)", ManagementFactory.getCompilationMXBean().getTotalCompilationTime());
			ad("isCompilationTimeMonitoringSupported", ManagementFactory.getCompilationMXBean().isCompilationTimeMonitoringSupported());
		}catch (Throwable ignored) {}
		ad("Updater Task", Updater.future.isDone() ? "Done" : "Fetching...");
		ad(Propertied.Manifest);
		ad(SettingsManifest.getMap());
		uid();
		
	}
	
	
	void uid() {
		ad(System.getenv());
		try {
			ArrayList<String> yikes = Identification.getKeys();
			for (String k : yikes) {
				ad(k, Core.settings.getString(k), s -> {
					if (Identification.getRandomUID().length() == s.length()) Core.settings.put(k, s);
				});
			}
		}catch (Throwable t) {
			WarningHandler.handleMindustry(t);
		}
		/*
		try {
			for (Thread t : Thread.getAllStackTraces().keySet()) {
				
				
				table.button(t.getName(), () -> {
					try {
						new ScrollableDialog("Stacktrace") {
							@Override
							protected void setup() {
								StringBuilder sb = new StringBuilder();
								sb.append("[").append(t.getId()).append("]").append("\n");
								if (t.isDaemon()) sb.append("[Daemon]").append("\n");
								sb.append(t.isAlive() ? "[Alive]" : "[Dead]").append("\n");
								sb.append(t.isInterrupted() ? "[Uninterrupted]" : "[Interrupted]").append("\n");
								sb.append("[").append(t.getThreadGroup()).append("]").append("\n");
								sb.append("[").append(t.getState()).append("]").append("\n");
								sb.append("[").append(t.getContextClassLoader()).append("]").append("\n");
								sb.append("[").append(t.getName()).append("]");
								int i = 0;
								for (StackTraceElement s : t.getStackTrace())
									sb.append(i++).append(". ").append(s.toString()).append("\n");
								table.add(sb).growX().growY();
							}
						}.show();
					}catch (Throwable te) {
						Vars.ui.showException(te);
					}
				}).tooltip("Stacktrace").growX();
				table.row();
			}
		}catch (Throwable t) {
			Log.err(t);
			Sentry.captureException(t);
			t.printStackTrace();
		}
		
		 */
		for (Map.Entry<Object, Object> s : System.getProperties().entrySet())
			ad(s.getKey().toString(), s.getValue().toString());
		for (Field f : Sounds.class.getFields()) {
			table.button(f.getName() + ".ogg", Icon.play, () -> {
				try {
					Sound s = (Sound) Sounds.class.getField(f.getName()).get(null);
					s.play(100);
				}catch (Throwable e) {
					Vars.ui.showException(e);
				}
			}).growX();
			table.row();
		}
		for (ObjectMap.Entry<String, TextureRegionDrawable> s : Icon.icons.entries()) {
			table.button(s.key, s.value, () -> {}).growX().disabled(true);
			table.row();
		}
		ad(LogicCodeEScanner.database);
	}
	
	
}
