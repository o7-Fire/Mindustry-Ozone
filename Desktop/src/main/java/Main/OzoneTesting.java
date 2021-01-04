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
import Ozone.Commands.Commands;
import Ozone.Desktop.Bootstrap.SharedBootstrap;
import Ozone.Desktop.Patch.Updater;
import Ozone.Test.OzoneTest;
import Ozone.Test.Test;
import arc.util.Log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static arc.util.ColorCodes.*;
import static arc.util.Log.format;
import static arc.util.Log.logger;

public class OzoneTesting {
	public static Test tests;
	protected static String[] tags = {"&lc&fb[D]&fr", "&lb&fb[I]&fr", "&ly&fb[W]&fr", "&lr&fb[E]", ""};
	protected static DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"), autosaveDate = DateTimeFormatter.ofPattern("MM-dd-yyyy_HH-mm-ss");
	
	static {
		logger = (level1, text) -> {
			String result = bold + lightBlack + "[" + dateTime.format(LocalDateTime.now()) + "] " + reset + format(tags[level1.ordinal()] + " " + text + "&fr");
			System.out.println(result);
		};
		Log.info("Startup in " + Countdown.result(SharedBootstrap.startup));
		Log.info("Preparing Test");
		tests = new OzoneTest();
		tests.add("Commands Test, Events Test, DesktopPatcher Registering", Commands::init);
	}
	
	public static void main(String[] args) {
		Log.info("Running Test\n");
		Test.setLog(new Logggg());
		Countdown.start();
		Updater.init();
		ArrayList<Test.Result> r = tests.runSync();
		Log.info("\b");
		Log.info("Test Result: ");
		for (String s : Test.getResult(r).split("\n")) Log.info(s);
		Countdown.stop();
		Log.info("Finished in " + Countdown.result());
		for (Test.Result rs : r) if (!rs.success) System.exit(1);
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
