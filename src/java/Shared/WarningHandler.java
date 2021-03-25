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

import Atom.Reflect.Reflect;
import Ozone.Manifest;
import arc.util.Log;
import io.sentry.Sentry;
import mindustry.Vars;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;

public class WarningHandler {
	public static ArrayList<WarningReport> listOfProblem = new ArrayList<>();
	public static HashSet<String> handled = new HashSet<>();
	
	public static boolean isLoaded() {//safest class
		return System.getProperty("Mindustry.Ozone.Loaded") != null;
	}
	
	
	//what the fuck are you trying to handle error or making error reporting app
	public static void handleOnce(Consumer<Throwable> c, Throwable t) {
		String s = t.toString();
		if (handled.contains(s)) return;
		handled.add(s);
		c.accept(t);
	}
	
	public static void handleMindustry(Throwable t) {
		String s = "Ozone-Handler";
		try { s = Reflect.getCallerClassStackTrace().toString(); }catch (Throwable ignored) {}
		handleMindustry(t, s);
	}
	
	public static void handleMindustryUserFault(Throwable t) {
		String s = "YourFault.jar";
		if (t instanceof RuntimeException) {
			while (t.getCause() != null) t = t.getCause();
			t = new Throwable(t);//idiot outcry
		}
		s = t.getClass().getSimpleName();
		handleOzone(t);
		handleStealthMindustry(t, s);
		try {
			Vars.ui.showException(t);
		}catch (Throwable ignored) {}
	}
	
	public static void handleJava(Throwable t) {
		if (t instanceof VirtualMachineError) throw new RuntimeException(t);
	}
	
	public static void handleOzone(Throwable t) {
		handleJava(t);
		if (t instanceof RuntimeException) if (SharedBoot.test) throw (RuntimeException) t;
	}
	
	public static void handleProgrammerFaultOnce(Throwable t) {
	
	}
	
	public static void handleProgrammerFault(Throwable t) {
		handleJava(t);
		try {
			Sentry.captureException(t);
		}catch (Throwable ignored) {}
		try {
			if (SharedBoot.debug) {
				t.printStackTrace();
				handleStealthMindustry(t, "Ozone-Debug");
			}
		}catch (Throwable ignored) {}
		
	}
	
	public static void handleStealthMindustry(Throwable t, String s) {
		try {
			Log.errTag(s, t.toString());
		}catch (Throwable ignored) {}
		
	}
	
	public static void handleMindustry(Throwable t, String s) {
		handleOzone(t);
		handleProgrammerFault(t);
		
	}
	
	public static void handle(WarningReport wr) {
		//Pool.daemon(()->{
		if (listOfProblem.contains(wr)) return;
		if (!wr.level.equals(WarningReport.Level.debug) || SharedBoot.debug) {
			
			try {Sentry.addBreadcrumb(wr.headlines(), wr.level.name());}catch (Throwable ignored) {}
			try {Manifest.toast(wr.headlines()); }catch (Throwable ignored) {}
			listOfProblem.add(wr);
			try {
				Log.logger.log(Log.LogLevel.values()[wr.level.ordinal()], wr.headlines());
			}catch (Throwable ignored) { System.out.println(wr.headlines());}
			
		}
		//}).start();
		
	}
	
	public static void handle(Throwable t) {
		handle(t, false);
	}
	
	public static void handle(Throwable t, boolean silent) {
		//Pool.daemon(()->{
		if (SharedBoot.debug) t.printStackTrace();
		WarningHandler.handleMindustry(t);
		if (!silent) {
			try { Log.err(t);}catch (Throwable ignored) {}
		}
		try {
			listOfProblem.add(new WarningReport(t).setLevel(silent ? WarningReport.Level.warn : WarningReport.Level.err));
		}catch (Throwable ignored) {}
		if (t.getClass() == RuntimeException.class) if (SharedBoot.test) throw (RuntimeException) t;
		//}).start();
		
	}
}
