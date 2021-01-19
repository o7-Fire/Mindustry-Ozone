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

import arc.scene.style.Drawable;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;

public abstract class OzoneDialog extends BaseDialog {
	protected Drawable icon = Icon.commandRallySmall;
	
	public OzoneDialog() {
		this("gay");
		title.setText(this.getClass().getSimpleName());
	}
	
	public OzoneDialog(String title, DialogStyle style) {
		super(title, style);
		ctor();
	}
	
	public OzoneDialog(String title) {
		super(title);
		ctor();
	}
	
	protected void ctor() {
		update(this::update);
		addCloseButton();
		
	}
	
	public Drawable icon() {
		return icon;
	}
	
	protected void update() {
	
	}
}
