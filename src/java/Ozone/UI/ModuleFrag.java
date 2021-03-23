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

import Atom.Reflect.FieldTool;
import Ozone.Internal.AbstractModule;
import Ozone.Internal.ModuleInterfaced;
import Ozone.Manifest;
import Ozone.Patch.Translation;
import mindustry.gen.Icon;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class ModuleFrag extends ScrollableDialog {
	{
		icon = Icon.layers;
	}
	
	protected void setup() {
		TreeMap<String, ModuleInterfaced> t = new TreeMap<>();
		for (Map.Entry<Class<? extends ModuleInterfaced>, ModuleInterfaced> s : Manifest.module.entrySet())
			t.put(s.getValue().getName(), s.getValue());
		for (Map.Entry<String, ModuleInterfaced> s : t.entrySet())
			ad(s.getValue());
	}
	
	void stub() {
		table.button("----", () -> {}).growX();
	}
	
	void ad(AbstractModule s) {
		table.button(Translation.get(s.getName()), () -> {}).growX().tooltip(FieldTool.getFieldDetails(s));
		try {
			for (Class<? extends ModuleInterfaced> m : s.dependOnModule()) {
				ModuleInterfaced mod = Manifest.module.get(m);
				table.button(Translation.get(mod.getName()), () -> {}).growX().tooltip(FieldTool.getFieldDetails(mod)).disabled(true);
			}
		}catch (IOException e) {
			throw new RuntimeException(e);
		}
		table.row();
	}
}
