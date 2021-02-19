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

import Atom.Utility.Random;
import Ozone.Internal.Interface;
import Ozone.Manifest;
import Ozone.Settings.BaseSettings;
import arc.Core;
import arc.scene.ui.Label;
import arc.struct.ObjectMap;
import mindustry.gen.Icon;

public class BundleViewer extends ScrollableDialog {
	String see = "";
	
	{
		icon = Icon.bookOpen;
		
	}
	
	@Override
	protected void setup() {
		cont.table(t -> {
			t.field("", s -> {
				see = s.toLowerCase();
			}).growX().tooltip("Search");
			//lol
			t.button(Icon.zoom, this::setup).tooltip("Search");//refresh button in disguise
		}).growX();
		cont.row();
		ad(Interface.bundle);
		ad(Core.bundle.getProperties());
	}
	
	void ad(ObjectMap<String, String> map) {
		for (ObjectMap.Entry<String, String> s : map.entries())
			if (see.isEmpty() || (see.contains(s.key.toLowerCase()) || see.contains(s.value.toLowerCase()))) {
				ad(s.key, s.value, map);
			}
	}
	
	protected void ad(Object title, Object value, ObjectMap<String, String> bund) {
		if (value == null) value = "null";
		if (BaseSettings.colorPatch) title = "[" + Random.getRandomHexColor() + "]" + title;
		Label l = new Label(title + ":");
		table.add(l).growX();
		String finalValue = String.valueOf(value);
		table.row();
		table.field(finalValue, s -> {
			bund.put(finalValue, s);
			Core.app.setClipboardText(finalValue);
			Manifest.toast("Copied");
		}).expandX().growX();
		table.row();
	}
}
