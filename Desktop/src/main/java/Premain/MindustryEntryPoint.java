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

package Premain;

import Ozone.Desktop.Bootstrap.DesktopBootstrap;
import Shared.InfoBox;
import io.sentry.Sentry;

import java.util.ArrayList;
import java.util.Arrays;

//if you have intellij just run this class, it will load mindustry
//oh also add the jar to the mods folder for modLoader to invoke
public class MindustryEntryPoint {
	public static void main(String[] args) {
		try {
			
			System.out.println("Initializing Ozone Environment");
			DesktopBootstrap.classloaderNoParent();
			DesktopBootstrap.loadRuntime();
			DesktopBootstrap.loadClasspath();
			main(new ArrayList<>(Arrays.asList(args)));
			System.exit(0);
		}catch (Throwable t) {
			t.printStackTrace();
			if (t.getCause() != null) t = t.getCause();
			InfoBox.write(t);
			Sentry.captureException(t);
			InfoBox.errorBox(t.toString(), "Ozone Environment");
			System.exit(1);
		}
	}
	
	
	public static void main(ArrayList<String> args) throws Throwable {
		DesktopBootstrap.loadMindustry(args);
		DesktopBootstrap.loadMain("Main.OzoneMindustry", args.toArray(new String[0]));
	}
	
}
