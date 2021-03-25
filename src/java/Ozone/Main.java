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

import Atom.Reflect.Reflect;
import Atom.Utility.Pool;
import Ozone.Internal.AbstractModule;
import Ozone.Internal.InformationCenter;
import Ozone.Internal.ModuleInterfaced;
import Shared.SharedBoot;
import Shared.WarningHandler;
import Shared.WarningReport;
import arc.Events;
import arc.util.Log;
import mindustry.game.EventType;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static Ozone.Internal.InformationCenter.reflectJson;

public class Main {
	private static boolean init = false;
	private static int iteration = 0;
	
	public static void loadContent() {
	
	
	}
	
	
	public static void update(String s) {
		Log.debug(s);
	}
	
	public static <T> Collection<Class<? extends T>> getExtendedClass(String packag, Class<T> type) {
		Collection<Class<? extends T>> raw = null;
		
		try {
			if (SharedBoot.isCore()) {
				String reflect = reflectJson();
				raw = (Reflect.getExtendedClassFromJson(reflect, type));
			}
		}catch (Throwable t) {
			WarningHandler.handleProgrammerFault(t);
		}
		if (raw == null) try {
			//if (SharedBoot.hardDebug) throw new RuntimeException("eat pant");
			if (InformationCenter.getReflection() != null) raw = InformationCenter.getReflection().getSubTypesOf(type);
		}catch (Throwable e) {
			WarningHandler.handleProgrammerFault(e);
			
		}
		if (raw == null) try {
			raw = Reflect.getExtendedClass(packag, type, Main.class.getClassLoader());
		}catch (Throwable e) {
			WarningHandler.handleProgrammerFault(e);
		}
		try {
			ArrayList<Class<? extends T>> real = new ArrayList<>();
			if (raw == null) return real;
			for (Class<? extends T> c : raw) {
				Class<? extends T> ce = (Class<? extends T>) Main.class.getClassLoader().loadClass(c.getName());
				if (!real.contains(ce)) real.add(ce);
			}
			return real;
		}catch (VirtualMachineError e) {
			throw new RuntimeException("Eat shit", e);
		}catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	public static Collection<Class<? extends ModuleInterfaced>> getModule() {
		return getExtendedClass(Main.class.getPackage().getName(), ModuleInterfaced.class);
	}
	
	public static void earlyInit() {
		Log.infoTag("Ozone", "Hail o7");
		Log.debug("Registering module\n");
		try {
			Atom.Manifest.internalRepo.addRepo(InformationCenter.getCurrentJar());
		}catch (MalformedURLException t) {
			WarningHandler.handleMindustry(t);
		}
		register();

		for (Map.Entry<Class<? extends ModuleInterfaced>, ModuleInterfaced> s : Manifest.module.entrySet()) {
			try {
				update("Early Init: " + s.getValue().getName());
				s.getValue().earlyInit();
			}catch (Throwable t) {
				WarningHandler.handleMindustry(t);
				new WarningReport(t).setProblem("Error while early init module " + s.getKey().getName() + ": " + t.toString()).report();
			}
		}
	}
	
	public static void preInit() {
		for (Map.Entry<Class<? extends ModuleInterfaced>, ModuleInterfaced> s : Manifest.module.entrySet()) {
			try {
				update("Pre Init: " + s.getValue().getName());
				s.getValue().preInit();
			}catch (Throwable t) {
				WarningHandler.handleMindustry(t);
				new WarningReport(t).setProblem("Error while pre init module " + s.getKey().getName() + ": " + t.toString()).report();
			}
		}
	}
	
	public static void register() {
		for (Class<? extends ModuleInterfaced> m : getModule()) {
			try {
				if (m == AbstractModule.class) continue;
				update("Registering: " + m.getName());
				ModuleInterfaced mod = m.getDeclaredConstructor().newInstance();
				mod.setRegister();
				Manifest.module.put(m, mod);
			}catch (Throwable t) {
				WarningHandler.handleMindustry(t);
				new WarningReport(t).setProblem("Error registering " + m.getName() + t.getMessage()).report();
			}
		}
	}
	
	public static void init() throws IOException, ExecutionException, InterruptedException {
		if (init) return;
		init = true;
		System.setProperty("Mindustry.Ozone.Loaded", Version.core + ":" + Version.desktop);
		//if (true)
		if (Manifest.module.size() < 16)
			throw new IllegalStateException("Ozone Module Only register: " + Manifest.module.size() + " Modules");
		update("Finished Registering \n");
		update("Initializing \n");
		loadModule();
		update("Finished Initializing \n");
		update("Initialized " + Manifest.module.size() + " module in " + iteration + " iteration");
		update("Posting module \n");
		Events.on(EventType.ClientLoadEvent.class, gay -> {
			for (Map.Entry<Class<? extends ModuleInterfaced>, ModuleInterfaced> s : Manifest.module.entrySet())
				if (!s.getValue().posted()) {
					try {
						update("Posting " + s.getValue().getName());
						s.getValue().setPosted();
						s.getValue().postInit();
						
					}catch (Throwable t) {
						WarningHandler.handleMindustry(t);
						new WarningReport(t).setProblem("Error while posting module " + s.getKey().getName() + ": " + t.toString()).report();
					}
					
				}
		});
		update("Post completed");
		for (Map.Entry<Class<? extends ModuleInterfaced>, ModuleInterfaced> s : Manifest.module.entrySet()) {
			Future f = Pool.submit(() -> {
				try {
					s.getValue().loadAsync();
				}catch (Throwable t) {
					WarningHandler.handleOnce(WarningHandler::handleMindustry, t);//fancy isn't it
					new WarningReport(t).setProblem("Error while loading async module " + s.getKey().getName() + ": " + t.toString()).report();
				}
			});
			if (SharedBoot.test) f.get();
		}
		
	}
	
	private static boolean loadModule(ModuleInterfaced module) throws Throwable {
		boolean dep = module.canLoadWithDep();
		if (dep) for (Class<? extends ModuleInterfaced> cc : module.dependOnModule()) {
			ModuleInterfaced m = Manifest.getModule(cc);
			if (m == null) throw new NullPointerException("Cant find: " + cc.getCanonicalName() + " on module");
			if (m.loaded()) continue;
			if (!loadModule(m)) throw new RuntimeException("Failed to load: " + m.getName());
		}
		if (module.canLoad() || dep) {
			update("Initializing " + module.getName());
			module.setLoaded();
			module.init();
			return true;
		}
		return false;
	}
	
	private static void loadModule() throws IOException {
		iteration++;
		boolean loadAnything = false;
		Throwable cause = null;
		
		for (Map.Entry<Class<? extends ModuleInterfaced>, ModuleInterfaced> s : Manifest.module.entrySet()) {
			boolean dep = s.getValue().canLoadWithDep();
			if (s.getValue().canLoad() || dep) {
				ModuleInterfaced module = s.getValue();
				try {
					loadAnything = loadModule(module);
				}catch (Throwable t) {
					cause = t;
					WarningHandler.handleMindustry(t);
					new WarningReport(t).setProblem("Error while initializing module " + s.getKey().getName() + ": " + t.toString()).report();
				}
			}
		}
		
		if (!loadAnything) throw new RuntimeException("Recursion/Deadlock/Bug !!!", cause);
		for (Map.Entry<Class<? extends ModuleInterfaced>, ModuleInterfaced> s : Manifest.module.entrySet())
			if (!s.getValue().loaded()) loadModule();
	}
	
}
