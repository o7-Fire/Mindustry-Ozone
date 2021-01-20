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
import Atom.Utility.Pool;
import Ozone.Internal.Module;
import arc.Events;
import arc.util.Log;
import io.sentry.Sentry;
import mindustry.game.EventType;

import java.util.Map;

public class Main {
	
	
	private static boolean init = false;
	static int iteration = 0;

	
	public static void loadContent() {
	
	
	}
	
	public static void init() {
		if (init) return;
		init = true;
		Log.infoTag("Ozone", "Hail o7");
		Log.debug("Registering module\n");
		for (Class<? extends Module> m : Reflect.getExtendedClass("Ozone", Module.class)) {
			try {
				Log.debug("Registering @", m.getName());
				Module mod = m.getDeclaredConstructor().newInstance();
				mod.setRegister();
				Log.debug("@ Registered", mod.getName());
				Manifest.module.put(m, mod);
			}catch (Throwable e) {
				Sentry.captureException(e);
				Log.err(e);
				throw new RuntimeException(e);
			}
		}
		Log.debug("Finished Registering \n");
		Log.debug("Initializing \n");
		loadModule();
		Log.debug("Finished Initializing \n");
		Log.debug("Initialized @ module in @ iteration", Manifest.module.size(), iteration);
		Log.debug("Posting module \n");
		Events.on(EventType.ClientLoadEvent.class, gay -> {
			for (Map.Entry<Class<? extends Module>, Module> s : Manifest.module.entrySet())
				if (!s.getValue().posted()) {
					try {
						Log.debug("Posting @", s.getValue().getName());
						s.getValue().postInit();
						s.getValue().setPosted();
						Log.debug("@ Posted", s.getValue().getName());
					}catch (Throwable throwable) {
						Sentry.captureException(throwable);
						Log.err(throwable);
						Log.err("Error while posting module @", s.getKey().getName());
						throw new RuntimeException(throwable);
					}
					
				}
		});
		Log.debug("Post completed");
		for (Map.Entry<Class<? extends Module>, Module> s : Manifest.module.entrySet())
			Pool.submit(() -> {
				try {
					s.getValue().loadAsync();
				}catch (Throwable t) {
					Log.err(t);
					Sentry.captureException(t);
				}
			});
	}
	
	private static void loadModule() {
		iteration++;
		boolean antiRecurse = false;
		for (Map.Entry<Class<? extends Module>, Module> s : Manifest.module.entrySet()) {
			if (s.getValue().canLoad()) {
				try {
					Log.debug("Initializing @", s.getValue().getName());
					s.getValue().init();
					s.getValue().setLoaded();
					Log.debug("@ Initialized", s.getValue().getName());
					antiRecurse = true;
				}catch (Throwable throwable) {
					Sentry.captureException(throwable);
					Log.err(throwable);
					Log.err("Error while loading module @", s.getKey().getName());
					throw new RuntimeException(throwable);
				}
			}
		}
		if (!antiRecurse) throw new RuntimeException("Recursion/Deadlock/Bug !!!");
		for (Map.Entry<Class<? extends Module>, Module> s : Manifest.module.entrySet())
			if (!s.getValue().loaded()) loadModule();
	}
	
	
}
