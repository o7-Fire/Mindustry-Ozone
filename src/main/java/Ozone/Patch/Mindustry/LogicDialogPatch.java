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

package Ozone.Patch.Mindustry;

import Ozone.Internal.InformationCenter;
import Ozone.Internal.Interface;
import Ozone.UI.ScrollableDialog;
import arc.func.Cons;
import arc.scene.Action;
import io.sentry.Sentry;
import io.sentry.UserFeedback;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.logic.LogicDialog;

public class LogicDialogPatch extends LogicDialog {
	protected String src = "";
	
	public LogicDialogPatch() {
		super();
		buttons.button("Show Hash", Icon.list, () -> {
			new ScrollableDialog("Hash Code") {
				@Override
				protected void setup() {
					int hash = src.hashCode();
					table.button(hash + "", () -> {
						Interface.copy(hash + "");
					}).tooltip("Copy").growX().growY();
				}
			}.show();
		}).size(210f, 64f);
		buttons.button("Report to Ozone-Sentry", Icon.fileText, () -> {
			Interface.showInput("Reason ?", s -> {
				int hash = src.hashCode();
				UserFeedback feedback = new UserFeedback(Sentry.captureMessage("Logic-Code-Report-" + hash));
				feedback.setName("Reporter-" + Vars.player.name.hashCode());
				StringBuilder sb = new StringBuilder();
				sb.append("Hash:").append(hash).append("\n");
				sb.append("Reason:").append(s).append("\n");
				if (Vars.net.active())
					sb.append("server:").append(InformationCenter.getCurrentServerIP()).append(":").append(InformationCenter.getCurrentServerPort());
				feedback.setComments(sb.toString());
				Sentry.captureUserFeedback(feedback);
				Interface.toast("Sent: " + "Hash-" + hash);
			});
		}).size(210f, 64f);
	}
	
	@Override
	public void hide(Action action) {
		super.hide(action);
		src = "";
	}
	
	@Override
	public void show(String code, Cons<String> modified) {
		src = code;
		super.show(code, s -> {
			src = s;
			modified.get(s);
		});
		
	}
}