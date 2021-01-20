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

package Ozone.Desktop.UI;

import Ozone.Desktop.Bootstrap.Dependency;
import Ozone.Desktop.Bootstrap.OzoneLoader;
import Ozone.Desktop.Propertied;
import Ozone.Experimental.Evasion.Identification;
import Ozone.Settings.SettingsManifest;
import Ozone.UI.ScrollableDialog;
import arc.Core;
import arc.audio.Sound;
import arc.scene.style.TextureRegionDrawable;
import arc.struct.ObjectMap;
import arc.util.Log;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
		ad("Compilation Time Total (ms)", () -> ManagementFactory.getCompilationMXBean().getTotalCompilationTime());
		ad("isCompilationTimeMonitoringSupported", () -> ManagementFactory.getCompilationMXBean().isCompilationTimeMonitoringSupported());
		ad(Propertied.Manifest);
		ad(Version.h);
		try { ad(SettingsManifest.getMap()); }catch (IOException ignored) { }
		dep();
		uid();
	}
	
	
	void dep() {
		try {
			for (URL u : ((OzoneLoader) this.getClass().getClassLoader()).getURLs()) {
				ad("Library-URL", u.toExternalForm());
			}
		}catch (Throwable ignored) {
		}
		for (Dependency d : Dependency.dependencies)
			try {
				ad(d.type.name(), d.getDownload());
			}catch (Throwable i) {
				ad(d.type.name(), i.toString());
			}
		if (!b) try {
			Dependency.save();
			b = true;
		}catch (Throwable ignored) {
		
		}
	}
	
	void uid() {
		ad(System.getenv());
		try {
			ObjectMap<String, Object> values = Identification.getValue();
			ArrayList<String> yikes = new ArrayList<>();
			for (String s : values.keys()) yikes.add(s);
			String[] keys = yikes.toArray(new String[0]);
			List<String> key = Arrays.stream(keys).filter(s -> s.startsWith("usid-")).collect(Collectors.toList());
			for (String k : key) {
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
