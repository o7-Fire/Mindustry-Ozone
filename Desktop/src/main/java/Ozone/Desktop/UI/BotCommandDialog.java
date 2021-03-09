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

package Ozone.Desktop.UI;

import Bot.BotClient;
import Ozone.UI.OzoneBaseDialog;

public class BotCommandDialog extends OzoneBaseDialog {
	BotClient botClient;
	
	public BotCommandDialog(BotClient b) {
		super("Bot Commands");
		botClient = b;
		addRefreshButton();
		setup();
	}
	
	protected void setup() {
		cont.clear();
		if (botClient == null) return;
		if (!botClient.connected()) {
			cont.labelWrap("[red]RMI not connected").growX().growY();
			return;
		}
		cont.button("Follow Me", () -> {
		
		}).growX();
		cont.row();
		cont.button("Do Nothing", () -> {
		
		}).growX();
	}
}
