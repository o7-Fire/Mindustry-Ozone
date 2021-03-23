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

package Ozone.Watcher;

import Atom.Reflect.FieldTool;
import Atom.Utility.Random;
import Ozone.Internal.AbstractModule;
import Ozone.Internal.InformationCenter;
import Ozone.Internal.Repo;
import Shared.SharedBoot;
import Shared.WarningReport;
import arc.Core;
import arc.util.Strings;
import info.debatty.java.stringsimilarity.experimental.Sift4;
import info.debatty.java.stringsimilarity.interfaces.StringDistance;
import io.sentry.Sentry;
import io.sentry.UserFeedback;
import mindustry.Vars;
import mindustry.gen.Groups;
import mindustry.gen.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class MostWanted extends AbstractModule {
	public static final ArrayList<String> mostWanted = new ArrayList<>();
	public static final HashSet<Integer> reported = new HashSet<>();
	public static StringDistance provider = new Sift4();
	int tick = 0;
	
	@Override
	public void loadAsync() {
		try {
			ArrayList<String> ss = Repo.getRepo().readArrayString("src/MostWanted.txt");
			Core.app.post(() -> {
				for (String s : ss)
					mostWanted.add(s.toUpperCase());
			});
			
			if (SharedBoot.debug)
				new WarningReport().setProblem("Loaded: " + ss.size() + " most wanted player").setWhyItsAProblem("Top most wanted criminals").setLevel(WarningReport.Level.info).report();
		}catch (IOException e) {
		
		}
	}
	
	@Override
	public void update() throws Throwable {
		tick++;
		if (tick < 60) return;
		if (Vars.state.isGame() && Vars.net.active() && reported.size() != 0) {
			Player p = Random.getRandom(Groups.player);
			if (p == null) return;
			if (reported.contains(p.id)) return;
			String s = Strings.stripColors(Strings.stripGlyphs(p.name())).toUpperCase();
			
			for (String se : mostWanted) {
				if (provider.distance(s, se) < 10) {
					UserFeedback report = new UserFeedback(Sentry.captureMessage("Wanted-On-Server"));
					report.setName(se);
					report.setComments(FieldTool.getFieldDetails(p, true) + "CurrentServer: " + InformationCenter.getCurrentServerIP() + ":" + InformationCenter.getCurrentServerPort() + "\n");
					Sentry.captureUserFeedback(report);
				}
			}
			reported.add(p.id);
			
			
		}
	}
	
	@Override
	public void init() throws Throwable {
	
	
	}
	
	@Override
	public void reset() throws Throwable {
		reported.clear();
	}
}
