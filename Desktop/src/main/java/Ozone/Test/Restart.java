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

package Ozone.Test;

import Atom.Reflect.Reflect;
import Ozone.Desktop.Bootstrap.DesktopBootstrap;
import Premain.InstallerEntryPoint;
import arc.Core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

public class Restart extends Test {
	public Restart() {
		subTests.add(new SubTest("Restart App", this::restart));
	}
	
	private void restart() throws FileNotFoundException {
		File jar = new File(Restart.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String[] cp = System.getProperty("java.class.path").split(System.getProperty("os.name").toUpperCase().contains("WIN") ? ";" : ":");
		ArrayList<String> h = new ArrayList<>(Arrays.asList(cp));
		String main = DesktopBootstrap.mainClass;
		if (main == null) throw new NullPointerException("DesktopBootstrap.mainClass is null");
		Core.app.exit();
		if (main.equals(InstallerEntryPoint.class.getName())) {
			Reflect.restart(jar, h);
		}else {
			h.add(jar.getAbsolutePath());
			Reflect.restart(main, h);
		}
	}
}
