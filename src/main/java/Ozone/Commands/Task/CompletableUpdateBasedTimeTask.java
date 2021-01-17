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

package Ozone.Commands.Task;

import arc.func.Prov;

public class CompletableUpdateBasedTimeTask extends UpdateBasedTimeTask {
	protected Prov<Boolean> completed;
	
	public CompletableUpdateBasedTimeTask(Runnable r, long ms, Prov<Boolean> completec) {
		super(r, ms);
		completed = completec;
	}
	
	@Override
	public void taskCompleted() {
		if (completed.get()) super.taskCompleted();
	}
	
	
	@Override
	public boolean isCompleted() {
		return completed.get();
	}
	
	@Override
	public void update() {
		if (!completed.get()) super.update();
	}
}
