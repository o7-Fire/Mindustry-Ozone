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

package Main;

import Atom.File.FileUtility;
import Atom.String.WordGenerator;
import Atom.Time.Time;
import Ozone.Test.Test;
import Shared.LoggerMode;
import Shared.OzoneMods;
import Shared.SharedBoot;
import arc.Core;
import arc.files.Fi;
import arc.mock.MockApplication;
import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Player;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class OzoneTesting {
	public static Test tests;
	protected static String[] tags = {"&lc&fb[D]&fr", "&lb&fb[I]&fr", "&ly&fb[W]&fr", "&lr&fb[E]", ""};
	protected static DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"), autosaveDate = DateTimeFormatter.ofPattern("MM-dd-yyyy_HH-mm-ss");
	protected static OzoneMods ozone;
	
	static {
		LoggerMode.loadLogger();
		SharedBoot.finishStartup();
		Log.info("Preparing Test");
		prepare();
		tests = new Test();
		tests.add("Make new Ozone", () -> {
			ozone = new Ozone();
		});
		tests.add("Init Ozone", () -> {
			ozone.init();
		});
		tests.add("Load Content Ozone", () -> {
			ozone.loadContent();
		});
		/*
		tests.add("CommandsCenter, DesktopCommands, Patch, Events", () -> {
			CommandsCenter.register();
			assert CommandsCenter.commandsList.size() > 5 : "CommandsCenter list is less than 5";
		});
		tests.add("Translation, DesktopTranslation, Patch, Events", () -> {
			TranslationDesktop.register();
			Translation.register();
			Log.info("Translation Patch Size: " + Interface.bundle.size);
			assert Interface.bundle.size > 5 : "Bundle list is less than 5";
		});
		
		tests.add("Color Patcher, CommandsCenter Description, Translation Patch", () -> {
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
		
		 */
	}
	
	public static void prepare() {
		Vars.player = Player.create();
		Vars.player.name = WordGenerator.newWord(16);
		Core.app = new MockApplication();
		try {
			Vars.init();
		}catch (Throwable ignored) {}
		Vars.dataDirectory = new Fi(FileUtility.getTempDir());
		
	}
	
	public static void main(String[] args) {
		Log.info("Running Test\n");
		Test.setLog(new Logggg());
		Time t = new Time();
		ArrayList<Test.Result> r = tests.runSync();
		Log.info("\b");
		Log.info("Test Result: ");
		for (String s : Test.getResult(r).split("\n")) Log.info(s);
		Log.info("Finished in " + t.convert(TimeUnit.MILLISECONDS).elapsedS());
		
		for (Test.Result rs : r) if (!rs.success) System.exit(1);
	}
	
	private static class TestFinished extends Throwable {
		public TestFinished() {
			super("Sentry Test");
		}
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
