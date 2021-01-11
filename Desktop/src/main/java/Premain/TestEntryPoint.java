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

package Premain;

import Ozone.Desktop.Bootstrap.SharedBootstrap;
import io.sentry.Sentry;

import java.util.Arrays;

public class TestEntryPoint {
	public static void main(String[] args) {
		if (System.getProperty("ozoneTest") == null) System.setProperty("ozoneTest", "true");
		try {
			SharedBootstrap.classloaderNoParent();
			SharedBootstrap.loadRuntime();
			SharedBootstrap.loadClasspath();
			SharedBootstrap.loadMindustry(Arrays.asList(args));
			SharedBootstrap.loadMain("Main.OzoneTesting", args);
			System.exit(0);
		}catch (Throwable t) {
			Catch.write(t);
			t.printStackTrace();
			if (t.getCause() != null) t = t.getCause();
			Sentry.captureException(t);
			System.exit(1);
		}
	}
}
