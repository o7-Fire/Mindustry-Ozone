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
import Atom.Time.Timer;
import Atom.Utility.Random;
import Ozone.Commands.Task.Task;
import Ozone.Commands.TaskInterface;
import Ozone.Settings.BaseSettings;
import arc.struct.Queue;
import mindustry.Vars;
import mindustry.gen.Icon;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TaskList extends ScrollableDialog {
	Timer timer = new Timer(TimeUnit.MICROSECONDS, 200);
	int last = 0;
	
	{
		icon = Icon.menu;
	}
	
	public TaskList() {
		super("Task List");
		
		
	}
	
	@Override
	public void setup() {
		
		
		for (Map.Entry<Integer, Queue<Task>> tm : TaskInterface.taskQueue.entrySet()) {
			table.labelWrap(tm.getKey() + ":").growX().row();
			int i = 0;
			for (Task t : tm.getValue()) {
				ad(i++, t);
			}
		}
	}
	
	void ad(int i, Task t) {
		String title = i + "";
		if (BaseSettings.colorPatch) title = "[" + Random.getRandomHexColor() + "]" + title;
		String finalTitle = title;
		table.table(te -> {
			te.button(finalTitle + "[white]. " + t.toString(), () -> {
				Vars.ui.showInfo(FieldTool.getFieldDetails(t, t.getClass(), true, 400));
			}).growX().tooltip(FieldTool.getFieldDetails(t));
			te.button(Icon.pencil, t::interrupt).tooltip("Interrupt");
		}).growX();
		
		table.row();
	}
	
	@Override
	protected void update() {
		if (timer.get()) {
			init();
		}
		
	}
}
