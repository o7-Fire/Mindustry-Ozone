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
import Ozone.Event.Internal;
import Ozone.Internal.Interface;
import Ozone.Patch.Translation;
import Ozone.Test.OzoneTest;
import Ozone.Test.Test;
import arc.Events;
import arc.struct.ObjectMap;
import arc.util.Log;
import io.sentry.Sentry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TreeMap;

import static arc.util.ColorCodes.*;
import static arc.util.Log.format;
import static arc.util.Log.logger;

public class OzoneTesting {
	public static Test tests;
	protected static String[] tags = {"&lc&fb[D]&fr", "&lb&fb[I]&fr", "&ly&fb[W]&fr", "&lr&fb[E]", ""};
	protected static DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"), autosaveDate = DateTimeFormatter.ofPattern("MM-dd-yyyy_HH-mm-ss");
	
	static {
		logger = (level1, text) -> {
			Sentry.addBreadcrumb(text, level1.name());
			String result = bold + lightBlack + "[" + dateTime.format(LocalDateTime.now()) + "] " + reset + format(tags[level1.ordinal()] + " " + text + "&fr");
			System.out.println(result);
		};
		Log.info("Startup in " + Countdown.result(SharedBootstrap.startup));
		Log.info("Preparing Test");
		tests = new OzoneTest();
		tests.add("Commands, DesktopCommands, Patch, Events", () -> {
			Events.run(Internal.Init.CommandsRegister, Ozone.Desktop.Patch.Commands::Init);
			Commands.init();
			assert Commands.commandsList.size() > 5 : "Commands list is less than 5";
		});
		tests.add("Translation, DesktopTranslation, Patch, Events", () -> {
			Events.run(Internal.Init.TranslationRegister, Ozone.Desktop.Patch.Translation::Init);
			Translation.register();
			Log.info("Translation Patch Size: " + Interface.bundle.size);
			assert Interface.bundle.size > 5 : "Bundle list is less than 5";
		});
		
		tests.add("Color Patcher, Commands Description, Translation Patch", () -> {
			TreeMap<String, String> modified = new TreeMap<>(), unmodified = new TreeMap<>();
			for (ObjectMap.Entry<String, String> s : Interface.bundle) {
				unmodified.put(s.key, s.value);
			}
			Log.info("Starting Color Patch");
			for (String s : unmodified.keySet()) {
				modified.put(s, Random.getRandomHexColor() + unmodified.get(s) + "[white]");
			}
			for (String k : unmodified.keySet())
				assert !modified.get(k).equals(unmodified.get(k)) : "Modified and Unmodified are same: " + k + ":" + unmodified.get(k);
		});
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
