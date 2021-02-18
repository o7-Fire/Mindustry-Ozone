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

package Ozone.Internal;

import Atom.File.RepoInternal;
import Atom.Utility.Encoder;
import arc.graphics.Pixmap;
import arc.util.Log;

import java.io.IOException;
import java.net.URL;

public class Repo extends Atom.File.Repo implements Module {
	@Override
	public URL getResource(String s) {
		URL u = null;
		try {
			u = RepoInternal.class.getClassLoader().getResource(s);
		}catch (Throwable ignored) {}
		try {
			if (u == null) ClassLoader.getSystemResource(s);
		}catch (Throwable ignored) {}
		try {
			if (u == null) super.getResource(s);
		}catch (Throwable ignored) {}
		return u;
	}
	
	@Override
	public void init() throws Throwable {
		addRepo(new URL("https://raw.githubusercontent.com/o7-Fire/Mindustry-Ozone/master"));
		addRepo(new URL("https://o7.ddns.net/ozone"));
	}
	
	public String getString(String path) throws IOException {
		return Encoder.readString(getResource(path).openStream());
	}
	
	public Pixmap getPixmap(String path) {
		URL u = getResource(path);
		Pixmap p = null;
		try {
			p = new Pixmap(Encoder.readAllBytes(u.openStream()));
		}catch (Throwable a) {
			Log.debug("Failed to load @ cause: @", path, a.toString());
		}
		return p;
	}
}
