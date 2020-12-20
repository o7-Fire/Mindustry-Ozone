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

package Ozone.UI;

import Atom.Utility.Random;
import Ozone.Commands.Commands;
import Ozone.Commands.Task.CommandsSpam;
import Settings.Core;
import arc.scene.Group;
import arc.scene.event.Touchable;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.fragments.Fragment;

import java.util.ArrayList;
import java.util.Map;

import static mindustry.Vars.state;

public class CommandsListFrag extends Fragment {
	public boolean visible = false;
	private Table logs = new Table().marginRight(30f).marginLeft(20f);//Add this ?
	private Table content = new Table().marginRight(30f).marginLeft(20f);
	private float h = 70F;
	private TextField sField;
	private String commands = "";
	
	
	@Override
	public void build(Group parent) {
		
		parent.fill(cont -> {
			cont.visible(() -> visible);
			cont.update(() -> {
				if (!state.is(GameState.State.playing)) {
					visible = false;
				}
			});
			
			
			cont.table(Tex.buttonTrans, pane -> {
				pane.labelWrap(Commands.commandsList.size() + " Commands in total").marginLeft(20);
				pane.row();
				sField = pane.field(commands, (res) -> commands = res).fillX().growX().get();
				pane.button(Icon.exportSmall, () -> Vars.ui.showTextInput("Commands", "How many times you want to run this", 2, "1", true, c -> Vars.ui.showTextInput("Commands", "Delay ? in tick, 10 frame is the lowest standard, 1 frame if you persist", 6, "100", true, d -> Commands.commandsQueue.addLast(new CommandsSpam(c, d, commands)))));
				pane.row();
				pane.pane(content).growX().grow().get().setScrollingDisabled(true, false);
				pane.row();
				pane.table(menu -> {
					menu.defaults().growX().height(50f).fillY();
					menu.button(arc.Core.bundle.get("close"), this::toggle);
				}).margin(0f).pad(15f).growX();
				
			}).touchable(Touchable.enabled).margin(14f);
		});
		rebuild();
	}
	
	public void rebuild() {
		content.clear();
		for (Map.Entry<String, Commands.Command> cl : Commands.commandsList.entrySet()) {
			Table table = new Table();
			boolean allowed = cl.getValue().icon != null;
			String name = (Core.colorPatch ? "[" + Random.getRandomHexColor() + "]" : "") + cl.getKey() + "[white]";
			if (allowed)
				table.button(name, cl.getValue().icon, () -> cl.getValue().method.accept(new ArrayList<>())).growX();
			else table.button(name, Icon.boxSmall, () -> {
			}).tooltip("Disabled, commands require user input").disabled(true).growX();
			table.button(Icon.info, () -> Vars.ui.showInfo(cl.getValue().description));
			content.add(table).width(350f).maxHeight(h + 14);
			content.row();
		}
	}
	
	
	public void toggle() {
		visible = !visible;
		
		if (visible) {
			rebuild();
		}else {
			sField.clearText();
			arc.Core.scene.setKeyboardFocus(null);
		}
	}
	
}
