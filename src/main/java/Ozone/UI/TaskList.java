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

import Atom.Time.Timer;
import Ozone.Commands.Commands;
import Ozone.Commands.Task.Task;
import Ozone.Commands.TaskInterface;
import mindustry.gen.Icon;

import java.util.concurrent.TimeUnit;

public class TaskList extends ScrollableDialog {
	Timer timer = new Timer(TimeUnit.MICROSECONDS, 200);
	
	public TaskList() {
		super("Task List");
		addCloseButton();
		buttons.button("Refresh", Icon.refresh, this::init).size(210f, 64f);
		init();
	}
	
	@Override
	protected void setup() {
		ad("Commands Task");
		for (Task t : Commands.commandsQueue)
			ad(t.toString());
		ad("Task Interface");
		for (Task t : TaskInterface.taskQueue)
			ad(t.toString());
	}
	
	@Override
	protected void update() {
		if (timer.get()) init();
	}
}
