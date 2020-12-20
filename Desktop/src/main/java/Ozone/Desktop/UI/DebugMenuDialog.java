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

package Ozone.Desktop.UI;

import Atom.Utility.Pool;
import Ozone.Test.Test;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.gen.Icon;

import java.util.ArrayList;

public class DebugMenuDialog extends OzoneBaseDialog {
	private volatile boolean running;
	
	public DebugMenuDialog() {
		super("Debug Menu");
		icon = Icon.pause;
		Test.setLog(new Log());
	}
	
	public static void showResult(ArrayList<Test.Result> results) {
		StringBuilder sb = new StringBuilder();
		if (results.isEmpty()) sb.append("No Result Found");
		for (Test.Result r : results)
			sb.append(r.reason).append(":\n").append(r.success ? "Success" : "Failed").append(" in ").append(r.duration).append("ms\n");
		Vars.ui.showInfo(sb.toString());
	}
	
	void setup() {
		cont.clear();
		for (Class<? extends Test> t : Test.getRawTestKit()) {
			SubTestDialog subTestDialog = new SubTestDialog(t);
			cont.button(t.getSimpleName(), Icon.productionSmall, () -> {
				start(t.getSimpleName());
				new Thread(() -> {
					try {
						ArrayList<Test.Result> results = t.getConstructor().newInstance().run();
						stop();
						showResult(results);
						
					}catch (Throwable tw) {
						stop();
						Vars.ui.showException(tw);
					}
				}).start();
			}).tooltip("Run " + t.getSimpleName()).growX().disabled(running);
			cont.button(Icon.list, subTestDialog::show).tooltip("Subtest");
			cont.row();
		}
		
	}
	
	void start(String name) {
		running = true;
		Vars.ui.loadfrag.show("Starting: " + name);
		setup();
	}
	
	void stop() {
		running = false;
		setup();
		Vars.ui.loadfrag.hide();
	}
	
	private static class SubTestDialog extends OzoneBaseDialog {
		public Class<? extends Test> testClass;
		Test test = null;
		Throwable t;
		
		public SubTestDialog(Class<? extends Test> h) {
			super("Sub Test", false);
			testClass = h;
			try {
				test = h.getDeclaredConstructor().newInstance();
			}catch (Throwable te) {
				t = te;
				t.printStackTrace();
				Sentry.captureException(te);
			}
			setup();
			addCloseButton();
			shown(this::setup);
		}
		
		void setup() {
			cont.clear();
			if (test == null) {
				cont.add(t.toString()).growX().growY();
				return;
			}
			if (test.getSubTest().isEmpty()) cont.add("This TestKit doesn't have subtest registered").growX().growY();
			for (Test.SubTest s : test.getSubTest()) {
				cont.button(s.name, Icon.productionSmall, () -> {
					Vars.ui.loadfrag.show(s.name);
					Pool.submit(() -> {
						try {
							ArrayList<Test.Result> results = new ArrayList<>();
							results.add(Test.test(s));
							Vars.ui.loadfrag.hide();
							showResult(results);
						}catch (Throwable throwable) {
							Vars.ui.loadfrag.hide();
							Sentry.captureException(throwable);
							throwable.printStackTrace();
							Vars.ui.showException(throwable);
						}
					});
					
				}).growX();
				cont.row();
			}
		}
		
	}
	
	private static class Log extends Atom.Utility.Log {
		@Override
		protected void output(Object raw) {
			arc.util.Log.info(raw);
		}
	}
}
