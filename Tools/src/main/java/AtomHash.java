import Atom.Utility.Encoder;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;

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

public class AtomHash {
	public static void main(String[] args) throws Throwable {
		File f = new File("../Atom"), gradle = new File("gradle.properties");
		System.out.println(f.getAbsolutePath());
		System.out.println(gradle.getAbsolutePath());
		if (f.exists() && gradle.exists()) {
			String s = new String(Runtime.getRuntime().exec("git rev-parse HEAD", new String[0], f).getInputStream().readAllBytes());
			System.out.println(s);
			HashMap<String, String> map = Encoder.parseProperty(gradle.toURI().toURL().openStream());
			map.put("atomHash", s);
			Files.writeString(gradle.toPath(), Encoder.property(map));
		}
	}
}
