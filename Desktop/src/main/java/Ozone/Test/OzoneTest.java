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

package Ozone.Test;

import Atom.Utility.Random;
import Ozone.Desktop.Propertied;
import arc.util.Strings;
import mindustry.core.Version;

public class OzoneTest extends Test {
	public OzoneTest() {
		add("Java Logic", () -> {
			assert 1 == 1;
			assert "b".equals("b");
			long a = Random.getInt();
			long b = Random.getInt(Integer.MAX_VALUE - 1);
			//if by chance its same, its a ~~miracle~~ bug
			assert a != b : "2 Random Integer is same how ??: " + a;
		});
		
		add("Strip Colors & Version Class Patch", () -> {
			arc.util.Log.info(Strings.stripColors(Version.combined()));
		});
		
		add("Random Generation", () -> {
			long s = System.currentTimeMillis();
			int i = 0;
			while ((System.currentTimeMillis() - s) < 200) {
				java.util.Random random = new java.util.Random();
				random.nextInt(1000000000);
				i++;
			}
			arc.util.Log.info("Generated " + i + " random number in " + (System.currentTimeMillis() - s) + "ms");
		});
		
		add("Manifest Validation", () -> {
			Log.info(Propertied.Manifest.size() + " Manifest");
			assert Propertied.Manifest.size() == 11 : "Invalid Manifest";
		});
	}
}
