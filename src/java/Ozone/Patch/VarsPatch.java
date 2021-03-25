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

package Ozone.Patch;

import Atom.Reflect.FieldTool;
import Atom.Reflect.Reflect;
import Ozone.Gen.Callable;
import Ozone.Internal.AbstractModule;
import Ozone.Internal.InformationCenter;
import Ozone.Internal.RepoCached;
import Ozone.Patch.Mindustry.DesktopInputPatched;
import Ozone.Patch.Mindustry.MobileInputPatched;
import Ozone.Patch.Mindustry.NetPatched;
import Ozone.Settings.BaseSettings;
import Shared.SharedBoot;
import Shared.WarningReport;
import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import mindustry.Vars;
import mindustry.graphics.LoadRenderer;
import mindustry.graphics.Pal;
import mindustry.input.DesktopInput;
import mindustry.input.MobileInput;

import java.lang.reflect.Field;

public class VarsPatch extends AbstractModule {
	public static Table menu;
	
	@Override
	public void earlyInit() throws Throwable {
		if (BaseSettings.debugMode) SharedBoot.debug = true;
		
		if (SharedBoot.debug) {
			Log.level = Log.LogLevel.debug;
			BaseSettings.debugMode = true;
		}
		Field f = Vars.class.getDeclaredField("maxSchematicSize");
		try {
			
			FieldTool.setFinalStatic(f, 1200);
		}catch (Throwable t) {
			new WarningReport().setProblem("Failed to patch final field maxSchematicSize : " + t.getMessage()).setWhyItsAProblem("No schematic hack size").setHowToFix("try user java 8").setLevel(WarningReport.Level.warn).report();
		}
		//java 8
		try {
			Field orange = LoadRenderer.class.getDeclaredField("orange"), color = LoadRenderer.class.getDeclaredField("color");
			Color c = new Color(Pal.darkMetal).lerp(Color.black, 0.5f);
			FieldTool.setFinalStatic(color, c);
			FieldTool.setFinalStatic(orange, "[#" + c + "]");
		}catch (Throwable t) {
			new WarningReport().setProblem("Failed to patch final field LoadRenderer : " + t.getMessage()).setWhyItsAProblem("No loading screen color hack").setHowToFix("try use java 8").setLevel(WarningReport.Level.warn).report();
		}
	}
	
	{
		dependsOn.add(RepoCached.class);
	}
	
	@Override
	public void postInit() throws Throwable {
	
	}
	
	@Override
	public void init() throws Throwable {
		
		Log.infoTag("Ozone", "Patching");
		Vars.enableConsole = true;
		if (BaseSettings.debugMode) Log.level = (Log.LogLevel.debug);
		Log.debug("Ozone-Debug: @", "Debugs, peoples, debugs");
		if (Vars.ui != null) {
			try {
				mindustry.Vars.ui.chatfrag.addMessage("gay", "no");
			}catch (NoSuchMethodError t) {
				System.setProperty("Ozone-Foo", "true");
			}
		}
		
		if (Vars.control != null) {
			if (Vars.control.input instanceof MobileInput) {
				Log.debug("its mobile input");
				Vars.control.input = new MobileInputPatched();
			}else if (Vars.control.input instanceof DesktopInput) {
				Log.debug("its desktop input");
				Vars.control.input = new DesktopInputPatched();
			}else Log.warn("Vars.control.input not patched");
		}
		try {
			menu = Reflect.getField(Vars.ui.menufrag.getClass(), "container", Vars.ui.menufrag);
		}catch (Throwable ignored) {}
		if (Vars.net != null) {
			Vars.net = new NetPatched(Vars.net);
			InformationCenter.callable = new Callable(Vars.net);
		}
		Log.infoTag("Ozone", "Patching Complete");
		
		
	}
	
}
