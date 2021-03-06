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

import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class Task {
	protected ArrayList<Consumer<Object>> onTaskCompleted = new ArrayList<>();
	protected int tick = 1;
	protected int currentTick = 0;
	public String name = null;
	
	public String getName() {
		return name == null ? this.getClass().getSimpleName() : name;
	}
	
	public void onTaskCompleted(Consumer<Object> v) {
		onTaskCompleted.add(v);
	}
	
	public void tellUser(String s) {
		CommandsCenter.tellUser(s);
	}
	
	public void onCompleted() {
		for (Consumer<Object> s : onTaskCompleted)
			s.accept(new Object());//wtf ?
	}
	
	public void interrupt() {
	
	}
	
	protected void setTick(int tick1) {
		tick = tick1;
	}
	
	protected boolean tick() {
		if (currentTick < tick) {
			currentTick++;
			return true;
		}else {
			currentTick = 0;
			return false;
		}
	}
	
	public boolean isCompleted() {
		return true;
	}
	
	public void update() {
	
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ":" + this.getName();
	}
}
