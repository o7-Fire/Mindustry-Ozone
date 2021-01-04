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

package Main;

import Atom.Time.Countdown;
import Atom.Utility.Random;
import Ozone.Commands.Commands;
import Ozone.Desktop.Bootstrap.SharedBootstrap;
import Ozone.Desktop.Patch.Updater;
import Ozone.Test.Test;
import arc.util.Log;
import arc.util.Strings;
import mindustry.core.Version;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static arc.util.ColorCodes.*;
import static arc.util.Log.format;
import static arc.util.Log.logger;

public class OzoneTest {
	public static Test tests = new Test() {};
	protected static String[] tags = {"&lc&fb[D]&fr", "&lb&fb[I]&fr", "&ly&fb[W]&fr", "&lr&fb[E]", ""};
	protected static DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"), autosaveDate = DateTimeFormatter.ofPattern("MM-dd-yyyy_HH-mm-ss");
	
	static {
		logger = (level1, text) -> {
			String result = bold + lightBlack + "[" + dateTime.format(LocalDateTime.now()) + "] " + reset + format(tags[level1.ordinal()] + " " + text + "&fr");
			System.out.println(result);
		};
		Log.info("Startup in " + Countdown.result(SharedBootstrap.startup));
		Log.info("Preparing Test");
		tests.add("Java Logic", () -> {
			assert 1 == 1;
			assert "b".equals("b");
			long a = Random.getInt();
			long b = Random.getInt(Integer.MAX_VALUE - 1);
			//if by chance its same, its a ~~miracle~~ bug
			assert a != b : "2 Random Integer is same how ??: " + a;
		});
		tests.add("Strip Colors & Version Class Patch", () -> {
			Log.info(Strings.stripColors(Version.combined()));
		});
		tests.add("Random Generation", () -> {
			long s = System.currentTimeMillis();
			ArrayList<Integer> h = new ArrayList<>();
			while ((System.currentTimeMillis() - s) < 200) {
				h.add(Random.getInt());
			}
			Log.info("Generated " + h.size() + " random number in " + (System.currentTimeMillis() - s) + "ms");
		});
		tests.add("Commands Test, Events Test, DesktopPatcher Registering", () -> {
			Commands.init();
		});
		
		
	}
	
	public static void main(String[] args) {
		Log.info("Running Test");
		Test.setLog(new Logggg());
		Countdown.start();
		Updater.init();
		Log.info("Test Result: \n" + Test.getResult(tests.runSync()));
		Countdown.stop();
		Log.info("Finished in " + Countdown.result());
		System.exit(0);
	}
	
	private static class Logggg extends Atom.Utility.Log {
		@Override
		public void info(Object s) {
			Log.info(s);
		}
		
		@Override
		protected void output(Object raw) {
			arc.util.Log.info(raw);
		}
	}
}
