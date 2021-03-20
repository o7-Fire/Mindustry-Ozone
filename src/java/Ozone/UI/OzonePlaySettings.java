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

import mindustry.ui.dialogs.BaseDialog;

import java.util.function.Consumer;

public class OzonePlaySettings extends ScrollableDialog {
	public static MarkerTileMode tileMode = MarkerTileMode.NothingShit;
	
	public static <T extends Enum<T>> void chooseEnum(Consumer<Enum<T>> cons, Enum<T>[] tEnum) {
		chooseEnum("Choose", cons, tEnum);
	}
	
	public static <T extends Enum<T>> void chooseEnum(String title, Consumer<Enum<T>> cons, Enum<T>[] tEnum) {
		BaseDialog b = new BaseDialog(title);
		b.buttons.center();
		b.buttons.table(t -> {
			for (Enum<T> m : tEnum)
				t.button(m.name(), () -> {
					cons.accept(m);
					b.hide();
				}).growX();
		}).growX().center();
		
		b.buttons.row();
		b.addCloseButton();
		b.show();
	}
	
	@Override
	protected void setup() {
		table.button("Marker Tile Mode: " + tileMode.name(), () -> {
			chooseEnum(c -> {
				tileMode = MarkerTileMode.valueOf(c.name());
				init();
			}, MarkerTileMode.values());
		}).growX();
	}
	
	public enum MarkerTileMode {
		Pathfinding, NothingShit
	}
}
