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
import Shared.WarningHandler;
import Shared.WarningReport;
import arc.Events;
import arc.util.Log;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import mindustry.game.EventType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

public class Main {
	
	
	private static boolean init = false;
	static int iteration = 0;
	public static Consumer<String> update = null;//to update on renderer
	
	public static void loadContent() {
	
	
	}
	
	
	public static void update(String s) {
		if (update != null) {
			try {
				//update.accept(s);
			}catch (Throwable t) {}
		}
		
		Log.debug(s);
	}
	
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
				ITransaction tr = Sentry.startTransaction(s.getValue().getName(), "Early Init");
				update("Early Init: " + s.getValue().getName());
				s.getValue().earlyInit();
				tr.finish();
			}catch (Throwable t) {
				Sentry.captureException(t);
				new WarningReport(t).setProblem("Error while early init module " + s.getKey().getName() + ": " + t.toString()).report();
			}
		}
	}
	
	public static void preInit() {
		for (Map.Entry<Class<? extends Module>, Module> s : Manifest.module.entrySet()) {
			try {
				ITransaction tr = Sentry.startTransaction(s.getValue().getName(), "Pre Init");
				update("Pre Init: " + s.getValue().getName());
				s.getValue().preInit();
				tr.finish();
			}catch (Throwable t) {
				Sentry.captureException(t);
				new WarningReport(t).setProblem("Error while pre init module " + s.getKey().getName() + ": " + t.toString()).report();
			}
		}
	}
	
	public static void register() {
		for (Class<? extends Module> m : getModule()) {
			try {
				ITransaction tr = Sentry.startTransaction(m.getName(), "Register");
				update("Registering: " + m.getName());
				Module mod = m.getDeclaredConstructor().newInstance();
				mod.setRegister();
				Manifest.module.put(m, mod);
				tr.finish();
			}catch (Throwable e) {
				WarningHandler.handle(e);
			}
		}
	}
	
	public static void init() throws IOException {
		if (init) return;
		init = true;
		
		
		update("Finished Registering \n");
		update("Initializing \n");
		loadModule();
		update("Finished Initializing \n");
		update("Initialized " + Manifest.module.size() + " module in " + iteration + " iteration");
		update("Posting module \n");
		Events.on(EventType.ClientLoadEvent.class, gay -> {
			for (Map.Entry<Class<? extends Module>, Module> s : Manifest.module.entrySet())
				if (!s.getValue().posted()) {
					try {
						ITransaction tr = Sentry.startTransaction(s.getValue().getName(), "Post");
						update("Posting " + s.getValue().getName());
						s.getValue().postInit();
						s.getValue().setPosted();
						tr.finish();
					}catch (Throwable throwable) {
						Sentry.captureException(throwable);
						new WarningReport(throwable).setProblem("Error while posting module " + s.getKey().getName() + ": " + throwable.toString()).report();
					}
					
				}
		});
		update("Post completed");
		for (Map.Entry<Class<? extends Module>, Module> s : Manifest.module.entrySet())
			Pool.submit(() -> {
				try {
					ITransaction tr = Sentry.startTransaction(s.getValue().getName(), "Async");
					s.getValue().loadAsync();
					tr.finish();
				}catch (Throwable t) {
					Sentry.captureException(t);
					new WarningReport(t).setProblem("Error while loading async module " + s.getKey().getName() + ": " + t.toString()).report();
				}
			});
		
	}
	
	private static void loadModule() throws IOException {
		iteration++;
		boolean antiRecurse = false;
		for (Map.Entry<Class<? extends Module>, Module> s : Manifest.module.entrySet()) {
			if (s.getValue().canLoad()) {
				try {
					ITransaction tr = Sentry.startTransaction(s.getValue().getName(), "Init");
					update("Initializing " + s.getValue().getName());
					s.getValue().init();
					s.getValue().setLoaded();
					antiRecurse = true;
					tr.finish();
				}catch (Throwable t) {
					new WarningReport(t).setProblem("Error while initializing module " + s.getKey().getName() + ": " + t.toString()).report();
				}
			}
		}
		if (!antiRecurse) throw new RuntimeException("Recursion/Deadlock/Bug !!!");
		for (Map.Entry<Class<? extends Module>, Module> s : Manifest.module.entrySet())
			if (!s.getValue().loaded()) loadModule();
	}
	
	
}
