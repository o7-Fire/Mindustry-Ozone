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

/* o7 Inc 2021 Copyright

  Licensed under the o7 Inc License, Version 1.0.1, ("the License");
  You may use this file but only with the License. You may obtain a
  copy of the License at
  
  https://github.com/o7-Fire/Mindustry-Ozone/Licenses
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the license for the specific language governing permissions and
  limitations under the License.
*/

package Ozone.Commands.Task;

import Ozone.Commands.CommandsCenter;
import Ozone.Settings.BaseSettings;
import mindustry.Vars;

//TODO Restructure this
public class CommandsSpam extends Task {
	private int howManyTimes = 1;
	private int currentCycle = 1;
	private String commands = "";
	
	public CommandsSpam(String howMany, String delay, String commands) {
		try {
			if (!howMany.isEmpty()) howManyTimes = Integer.parseInt(howMany);
			int delays;
			if (delay.isEmpty()) delays = 0;
			else delays = Integer.parseInt(delay);
			if (!CommandsCenter.call(BaseSettings.commandsPrefix + commands)) {
				currentCycle++;
				howManyTimes = 1;
				Vars.ui.showErrorMessage(commands + " is not a valid commands");
			}else this.commands = commands;
			setTick(delays);
		}catch (NumberFormatException c) {
			Vars.ui.showException(c);
			currentCycle = 1;
		}
	}

	@Override
	public boolean isCompleted() {
		return currentCycle > howManyTimes;
	}

	@Override
	public void update() {
		if (tick()) return;
		currentCycle++;
		if (currentCycle > howManyTimes) return;
		CommandsCenter.call(BaseSettings.commandsPrefix + commands);
	}
}
