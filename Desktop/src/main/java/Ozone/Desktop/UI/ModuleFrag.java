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

import Ozone.Internal.Module;
import Ozone.Manifest;
import Ozone.UI.ScrollableDialog;

import java.util.HashMap;
import java.util.Map;

public class ModuleFrag extends ScrollableDialog {
	
	@Override
	protected void setup() {
		HashMap<Class<? extends Module>, Module> see = new HashMap<>(Manifest.module);
		for (Map.Entry<Class<? extends Module>, Module> s : see.entrySet())
			ad(s);
	}
	
	void stub() {
		table.button("----", () -> {}).growX();
	}
	
	void ad(Map.Entry<Class<? extends Module>, Module> s) {
		table.button(s.getValue().getName(), () -> {}).disabled(!s.getValue().dependOnModule().isEmpty()).growX().row();
	}
}
