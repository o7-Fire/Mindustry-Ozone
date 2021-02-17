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

package Ozone;

import Atom.Reflect.Reflect;
import Ozone.Internal.Module;
import Ozone.Settings.SettingsManifest;
import Ozone.UI.*;
import arc.Core;
import arc.math.Interp;
import arc.net.Client;
import arc.scene.actions.Actions;
import arc.scene.event.Touchable;
import arc.scene.ui.layout.Table;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.net.ArcNetProvider;
import mindustry.net.Net;
import mindustry.ui.Styles;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("Unchecked")
public class Manifest {
	public static OzonePlaySettings playSettings;
	public static OzoneMenu menu;
	public static CommandsListFrag commFrag;
	public static WorldInformation worldInformation;
	public static TaskList taskList;
	
	public static ExperimentDialog experiment;
	public static UILayout uiDebug;
	public static ModsMenu modsMenu;
	public static EnvironmentInformation envInf;
	public static LogView logView;
	
	public static ModuleFrag moduleFrag;
	public static ArrayList<Class<?>> settings = new ArrayList<>();//for GUI purpose
	public static String lastServer = "";
	
	public static final HashMap<Class<? extends Module>, Module> module = new HashMap<>();
	
	
	public static <T extends Module> T getModule(Class<? extends Module> clazz) {
		try {
			return (T) module.get(clazz);
		}catch (Throwable ignored) {}
		return null;
	}
	
	public static String getCurrentServerIP() {
		if (!Vars.net.active()) return lastServer;
		try {
			Net.NetProvider n = Reflect.getField(Vars.net.getClass(), "provider", Vars.net);
			if (!(n instanceof ArcNetProvider)) return null;
			ArcNetProvider arc = (ArcNetProvider) n;
			Client c = Reflect.getField(arc.getClass(), "client", arc);
			return c.getRemoteAddressTCP().getHostName() + ":" + c.getRemoteAddressTCP().getPort();
		}catch (Throwable ignored) { }
		return lastServer;
		
	}
	
	public static void saveSettings() {
		for (Class<?> c : Manifest.settings) {
			try {
				c.getDeclaredMethod("save").invoke(null);
			}catch (Throwable t) {
				Sentry.captureException(t);
			}
		}
		try {
			SettingsManifest.saveMap();
		}catch (Throwable e) {
			Sentry.captureException(e);
			Vars.ui.showException(e);
		}
	}
	
	public static void toast(String text) {
		Table table = new Table();
		table.touchable = Touchable.disabled;
		table.setFillParent(true);
		table.actions(Actions.fadeOut(4.0F, Interp.fade), Actions.remove());
		table.bottom().add(text).style(Styles.outlineLabel).padBottom(80);
		Core.scene.add(table);
	}

	public static ArrayList<Field> getSettings() {
		ArrayList<Field> f = new ArrayList<>();
		//must static class
		for (Class<?> c : settings) {
			for (Field ff : (c.getDeclaredFields()))
				if (!f.contains(ff)) f.add(ff);
		}
		return f;
	}
}
