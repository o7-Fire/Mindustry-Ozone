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

import Atom.Time.Timer;

import java.util.concurrent.TimeUnit;

public abstract class UpdateBasedTimeTask extends Task {//need to be implemented before used
	Runnable run;
	Timer timer = new Timer(TimeUnit.MILLISECONDS, 100);
	
	public UpdateBasedTimeTask() {
	
	}
	
	public UpdateBasedTimeTask(Runnable r, long ms) {
		run = r;
		timer = new Timer(TimeUnit.MILLISECONDS, ms);
	}
	
	public void run() {
		if (run != null) run.run();
	}
	
	public void update() {//don't override this
		if (timer.get()) run();
	}
	
}
