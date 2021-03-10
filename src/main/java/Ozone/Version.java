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

package Ozone;

public class Version {
	public static String desktop;
	public static String core;
	
	static {
		try {
			desktop = Propertied.Manifest.get("Version.Desktop");
			core = Propertied.Manifest.get("Version.Core");
		}catch (Throwable ignored) {}
		if (core == null) core = "0.0.0-[Manifest Failed]";
		if (desktop == null) desktop = "0.12.0-[Manifest Failed]";
	}
}
