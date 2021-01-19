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

package Ozone.Internal;

import Ozone.Manifest;
import arc.assets.Loadable;

import java.util.ArrayList;
import java.util.Map;

import static Ozone.Internal.InformationCenter.*;

public interface Module extends Loadable {
	
	default void init() {
	
	}
	
	default void postInit() {
	
	}
	
	
	default void register() {//invoked from outside
		reg(moduleRegistered);
	}
	
	default void loaded() {//invoked from outside
		reg(moduleLoaded);
	}
	
	default void posted() {reg(modulePost);}
	
	default ArrayList<Class<? extends Module>> dependOnModule() {
		return new ArrayList<>();
	}
	
	default boolean canLoad() {
		if (moduleLoaded.contains(getName())) return false;
		if (!moduleRegistered.contains(getName())) return false;
		ArrayList<Class<? extends Module>> dep = dependOnModule();
		for (Map.Entry<Class<? extends Module>, Module> s : Manifest.module.entrySet()) dep.remove(s.getKey());
		return dep.isEmpty();
	}
	
	default void reg(ArrayList<String> ar) {
		if (ar.contains(getName())) throw new RuntimeException("Module: \"" + getName() + "\" already registered");
		ar.add(getName());
	}
}
