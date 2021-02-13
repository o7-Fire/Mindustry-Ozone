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

import Shared.LoggerMode;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Icon;

public class LogView extends ScrollableDialog {
	
	volatile String see = "";
	
	@Override
	protected void ctor() {
		super.ctor();
		
	}
	
	@Override
	protected void setup() {
		cont.table(t -> {
			t.field("", s -> {
				see = s;
			}).growX().tooltip("Javascript console");
			t.button(Icon.add, () -> {
				Log.info(">" + see);
				Log.info(Vars.mods.getScripts().runConsole(see));
				init();
			});
		}).growX();
		
		cont.row();
		Seq<String> log = new Seq<>(LoggerMode.logBuffer);
		log.forEach(this::ad);
		
	}
	
	
}
