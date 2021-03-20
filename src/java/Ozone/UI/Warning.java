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

import Atom.Utility.Utility;
import Ozone.Experimental.ThreadStackTrace;
import Ozone.Internal.Interface;
import Shared.WarningHandler;
import Shared.WarningReport;
import arc.graphics.Color;
import arc.scene.style.TextureRegionDrawable;
import mindustry.gen.Icon;

import java.util.HashSet;

public class Warning extends ScrollableDialog {
	
	public WarningReport example = new WarningReport("Life", "to prevent suffering", "die", WarningReport.Level.debug);
	
	{
		icon = Icon.warning;
		example.report();
	}
	
	@Override
	protected void ctor() {
		super.ctor();
		addNavButton("Troubleshoot", Icon.warning, () -> Interface.openLink("https://github.com/o7-Fire/Mindustry-Ozone/wiki/Troubleshoot"));
	}
	
	@Override
	protected void setup() {
		for (WarningReport c : new HashSet<>(WarningHandler.listOfProblem))
			ad(c);
	}
	
	protected void ad(WarningReport c) {
		TextureRegionDrawable icon = Icon.warning;
		switch (c.level) {
			case info:
				icon = Icon.info;
				break;
			case debug:
				icon = Icon.zoom;
				break;
			default:
		}
		
		table.button(c.headlines(), icon, () -> {
			
			new ScrollableDialog(c.headlines()) {
				@Override
				protected void setup() {
					
					table.add("Level:").growX().row();
					table.add(c.level.colorized() + Utility.capitalizeEnforce(c.level.name())).growX().row();
					table.add("Problem:").growX().row();
					table.field(c.problem, s -> {}).disabled(true).growX().row();
					table.add("Why i should fix it:").growX().row();
					table.field(c.whyItsAProblem, s -> {}).disabled(true).growX().row();
					table.add("How to fix it:").growX().row();
					table.field(c.howToFix, s -> {}).disabled(true).growX().row();
					table.add("Thread:").growX().row();
					table.field(c.thread + "", s -> {}).disabled(true).growX().row();
					table.button("Stacktrace", () -> {
						ThreadStackTrace.showStacktrace(c.caller);
					}).growX().row();
					
				}
			}.show();
		}).color(Color.valueOf(c.level.color)).growX();
		table.button(Icon.cancel, () -> {
			try {
				WarningHandler.listOfProblem.remove(c);
			}catch (Throwable t) {
				WarningHandler.handle(t);
			}
			init();
		}).tooltip("Dismiss");
		table.row();
	}
}


