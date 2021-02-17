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


import Ozone.Experimental.Evasion.Identification;
import Ozone.Propertied;
import Ozone.Settings.SettingsManifest;
import arc.Core;
import arc.audio.Sound;
import arc.scene.style.TextureRegionDrawable;
import arc.struct.ObjectMap;
import arc.util.Log;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;

import java.io.IOException;
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
	
	protected void setup() {
		ad("Player Name", Vars.player.name);
		ad("UUID", Core.settings.getString("uuid"));
		ad("Current Millis", System.currentTimeMillis());
		ad("Current Nanos", System.nanoTime());
		try {
			ad("Compilation Time Total (ms)", ManagementFactory.getCompilationMXBean().getTotalCompilationTime());
			ad("isCompilationTimeMonitoringSupported", ManagementFactory.getCompilationMXBean().isCompilationTimeMonitoringSupported());
		}catch (Throwable ignored) {}
		ad(Propertied.Manifest);
		
		try { ad(SettingsManifest.getMap()); }catch (IOException ignored) { }
		uid();
		
	}
	
	
	void uid() {
		ad(System.getenv());
		try {
			ArrayList<String> yikes = Identification.getKeys();
			for (String k : yikes) {
				ad(k, Core.settings.getString(k));
			}
		}catch (Throwable t) {
			Log.err(t);
			Sentry.captureException(t);
			t.printStackTrace();
		}
		
		for (Map.Entry<Object, Object> s : System.getProperties().entrySet())
			ad(s.getKey().toString(), s.getValue().toString());
		for(Field f : Sounds.class.getFields()){
			table.button(f.getName()+".ogg", Icon.play, ()->{
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
	}
	
	
}
