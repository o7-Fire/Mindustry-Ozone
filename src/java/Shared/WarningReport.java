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

package Shared;

import Atom.Utility.Utility;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class WarningReport implements Serializable {
	public String problem = "", whyItsAProblem = "", howToFix = "";
	public Level level = Level.debug;
	public StackTraceElement[] caller;
	public transient Thread thread;
	
	public WarningReport(String s) {
		this(s, "don't know", "¯\\_(ツ)_/¯", Level.info);
	}
	
	public WarningReport() {
		caller = Thread.currentThread().getStackTrace();
		thread = Thread.currentThread();
	}
	
	public WarningReport(String problem, String whyItsAProblem, String howToFix, Level level) {
		this();
		this.problem = problem;
		this.whyItsAProblem = whyItsAProblem;
		this.howToFix = howToFix;
		this.level = level;
	}
	
	public WarningReport(Throwable t) {
		if (t instanceof VirtualMachineError) throw new RuntimeException(t);//no thx
		setProblem(t.getLocalizedMessage() + "-" + t.getStackTrace()[0]).setWhyItsAProblem("Because its java").setHowToFix("only god know how").setLevel(Level.err);
	}
	
	public void report() {
		WarningHandler.handle(this);
		
	}
	
	public WarningReport setProblem(String problem) {
		this.problem = problem;
		return this;
	}
	
	public WarningReport setWhyItsAProblem(String whyItsAProblem) {
		this.whyItsAProblem = whyItsAProblem;
		return this;
	}
	
	public WarningReport setHowToFix(String howToFix) {
		this.howToFix = howToFix;
		return this;
	}
	
	public WarningReport setLevel(Level level) {
		this.level = level;
		return this;
	}
	
	public String headlines() {
		return Utility.capitalizeEnforce("[" + level.name() + "]") + "-" + problem;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WarningReport that = (WarningReport) o;
		return Objects.equals(problem, that.problem) && Objects.equals(whyItsAProblem, that.whyItsAProblem) && Objects.equals(howToFix, that.howToFix) && level == that.level;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(problem, whyItsAProblem, howToFix, level);
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("WarningReport{").append('\n');
		sb.append("problem='").append(problem).append('\n');
		sb.append(", whyItsAProblem='").append(whyItsAProblem).append('\n');
		sb.append(", howToFix='").append(howToFix).append('\n');
		sb.append(", level=").append(level).append('\n');
		sb.append(", caller=").append(Arrays.toString(caller));
		sb.append(", thread=").append(thread).append('\n');
		sb.append('}');
		return sb.toString();
	}
	
	public enum Level {
		debug("#0ff713"), info("#0fe8f7"), warn("#f7f70f"), err("#ff1e00"), none("#ffffff");
		
		public String color;
		
		Level(String color) {
			this.color = color;
		}
		
		public String colorized() {
			return "[" + color + "]";
		}
	}
}
