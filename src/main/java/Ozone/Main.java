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
import Atom.Utility.Encoder;
import Atom.Utility.Pool;
import Ozone.Internal.Module;
import Shared.SharedBoot;
import arc.Events;
import arc.util.Log;
import io.sentry.Sentry;
import mindustry.game.EventType;
import mindustry.graphics.LoadRenderer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class Main {
	
	
	private static boolean init = false;
	static int iteration = 0;
	
	
	public static void loadContent() {
	
	
	}
	
	private static LoadRenderer renderer = null;
	
	public static <T> Collection<Class<? extends T>> getExtended(String packag, Class<T> type) {
		Collection<Class<? extends T>> raw = null;
		try {
			raw = Reflect.getExtendedClass(packag, type, Main.class.getClassLoader());
		}catch (Throwable e) {
			if (!Atom.Manifest.internalRepo.resourceExists("reflections/core-reflections.json"))
				throw new RuntimeException(e);
			else {
				try {
					InputStream is = Atom.Manifest.internalRepo.getResourceAsStream("reflections/core-reflections.json");
					raw = Reflect.getExtendedClassFromJson(Encoder.readString(is), type);
				}catch (Throwable t) {
					throw new RuntimeException(t);
				}
			}
		}
		try {
			ArrayList<Class<? extends T>> real = new ArrayList<>();
			for (Class<? extends T> c : raw)
				real.add((Class<? extends T>) Main.class.getClassLoader().loadClass(c.getName()));
			return real;
		}catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	public static Collection<Class<? extends Module>> getModule() {
		return getExtended("Ozone", Module.class);
	}
	
	public static void earlyInit() {
		Log.infoTag("Ozone", "Hail o7");
		Log.debug("Registering module\n");
		register();
		for (Map.Entry<Class<? extends Module>, Module> s : Manifest.module.entrySet()) {
			try {
				Log.debug("Early Init: @", s.getValue().getName());
				s.getValue().earlyInit();
			}catch (Throwable e) {
				Sentry.captureException(e);
				Log.err(e);
			}
		}
	}
	
	public static void preInit() {
		for (Map.Entry<Class<? extends Module>, Module> s : Manifest.module.entrySet()) {
			try {
				Log.debug("Pre Init: @", s.getValue().getName());
				s.getValue().preInit();
			}catch (Throwable e) {
				Sentry.captureException(e);
				Log.err(e);
			}
		}
	}
	
	public static void register() {
		for (Class<? extends Module> m : getModule()) {
			try {
				Log.debug("Registering @", m.getName());
				Module mod = m.getDeclaredConstructor().newInstance();
				mod.setRegister();
				
				Manifest.module.put(m, mod);
			}catch (Throwable e) {
				Sentry.captureException(e);
				Log.err(e);
			}
		}
	}
	
	public static void init() throws IOException {
		if (init) return;
		init = true;
		
		
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
		Log.info("Startup in " + (System.currentTimeMillis() - SharedBoot.startup) + " ms");
	}
	
	private static void loadModule() throws IOException {
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
				}
			}
		}
		if (!antiRecurse) throw new RuntimeException("Recursion/Deadlock/Bug !!!");
		for (Map.Entry<Class<? extends Module>, Module> s : Manifest.module.entrySet())
			if (!s.getValue().loaded()) loadModule();
	}
	
	
}
