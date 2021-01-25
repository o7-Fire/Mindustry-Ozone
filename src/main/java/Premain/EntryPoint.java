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

import Atom.Reflect.FieldTool;
import Ozone.Bootstrap.OzoneBootstrap;
import Ozone.Main;
import Ozone.Patch.Updater;
import Shared.LoggerMode;
import arc.Core;
import arc.graphics.Color;
import arc.util.Log;
import io.sentry.Sentry;
import mindustry.graphics.LoadRenderer;
import mindustry.graphics.Pal;
import mindustry.mod.Mod;

import java.lang.reflect.Field;

public class EntryPoint extends Mod {
	static {
		try {
			Log.info("Ozone Standalone");
			OzoneBootstrap.init();
			LoggerMode.loadLogger();
		}catch (Throwable t) {
			t.printStackTrace();
			Log.err(t);
			Sentry.captureException(t);
		}
		try {
			Updater.async();
		}catch (Throwable ignored) {}
		//java 8
		try {
			Field orange = LoadRenderer.class.getDeclaredField("orange"), color = LoadRenderer.class.getDeclaredField("color");
			Color c = new Color(Pal.darkMetal).lerp(Color.black, 0.5f);
			FieldTool.setFinalStatic(color, c);
			FieldTool.setFinalStatic(orange, "[#" + c + "]");
		}catch (Throwable ignored) {}
	}
	
	public EntryPoint() {
		if (Core.settings != null) {
			Core.settings.put("crashreport", true);
			Core.settings.put("uiscalechanged", false);//shut
		}
	}
	
	@Override
	public void init() {
		try {
			Main.init();
		}catch (Throwable t) {
			t.printStackTrace();
			Sentry.captureException(t);
			Log.err(t);
		}
	}
	
	@Override
	public void loadContent() {
		try {
			Main.loadContent();
		}catch (Throwable t) {
			t.printStackTrace();
			Sentry.captureException(t);
			Log.err(t);
		}
	}
}
