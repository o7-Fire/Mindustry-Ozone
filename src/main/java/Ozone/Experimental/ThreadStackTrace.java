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

package Ozone.Experimental;

import Atom.Reflect.Reflect;
import Ozone.UI.ScrollableDialog;
import mindustry.Vars;

public class ThreadStackTrace implements Experimental {
	public static void showStacktrace(StackTraceElement[] stackTraceElements) {
		StackTraceElement te = Reflect.getCallerClassStackTrace();
		try {
			new ScrollableDialog("Stacktrace") {
				@Override
				protected void setup() {
					StringBuilder sb = new StringBuilder();
					int i = 0;
					for (StackTraceElement s : stackTraceElements)
						sb.append(i++).append(". ").append(s.toString()).append("\n");
					table.add(sb).growX().growY();
					table.row();
					table.add("Caller class:").growX().row();
					table.field(te.toString(), s -> {}).disabled(true).growX();
				}
			}.show();
		}catch (Throwable t) {
			Vars.ui.showException(t);
		}
	}
	
	@Override
	public void run() {
		showStacktrace(Thread.currentThread().getStackTrace());
	}
	
}
