/*
 * Copyright 2020 Itzbenz
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

import arc.Core;
import arc.scene.style.Drawable;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;

public class OzoneBaseDialog extends BaseDialog {
	public Drawable icon = Icon.book;
	
	public OzoneBaseDialog(String title, DialogStyle style) {
		this(title, style, true);
	}
	
	public OzoneBaseDialog(String title, DialogStyle style, boolean setup) {
		super(title, style);
		if (!setup) return;
		addCloseButton();
		setup();
		shown(this::setup);
		onResize(this::setup);
		update(this::update);
	}
	
	
	public OzoneBaseDialog(String title) {
		this(title, Core.scene.getStyle(DialogStyle.class));
	}
	
	public OzoneBaseDialog(String title, boolean setup) {
		this(title, Core.scene.getStyle(DialogStyle.class), setup);
	}
	
	public OzoneBaseDialog() {
		this("Goddam fill this title");
	}
	
	void update() {
	
	}
	
	void setup() {
	
	}
}
