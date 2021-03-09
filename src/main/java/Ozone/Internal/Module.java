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

/* o7 Inc 2021 Copyright

  Licensed under the o7 Inc License, Version 1.0.1, ("the License");
  You may use this file but only with the License. You may obtain a
  copy of the License at
  
  https://github.com/o7-Fire/Mindustry-Ozone/Licenses
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the license for the specific language governing permissions and
  limitations under the License.
*/

package Ozone.Internal;

import Ozone.Manifest;
import arc.assets.Loadable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static Ozone.Internal.InformationCenter.*;

public interface Module extends Loadable {
	default void earlyInit() throws Throwable {
	
	}
	
	default void preInit() throws Throwable {
	
	}
	
	default void reset() throws Throwable {
	
	}
	
	
	default void init() throws Throwable {
	
	}
	
	default void postInit() throws Throwable {
	
	}
	
	
	default void setRegister() {//invoked from outside
		try {
			reg(moduleRegistered);
		}catch (IllegalStateException i) {
			throw new RuntimeException("Module: \"" + getName() + "\" already registered");
		}
	}
	
	default void setLoaded() {//invoked from outside
		try {
			reg(moduleLoaded);
		}catch (IllegalStateException i) {
			throw new RuntimeException("Module: \"" + getName() + "\" already loaded");
		}
	}
	
	default void setPosted() {
		try {
			reg(modulePost);
		}catch (IllegalStateException i) {
			throw new RuntimeException("Module: \"" + getName() + "\" already posted");
		}
	}
	
	default boolean loaded() {
		return moduleLoaded.contains(getName());
	}
	
	default boolean registered() {
		return moduleRegistered.contains(getName());
	}
	
	default boolean posted() {
		return modulePost.contains(getName());
	}
	
	default List<Class<? extends Module>> dependClean() throws IOException {
		ArrayList<Class<? extends Module>> ar = new ArrayList<>(dependOnModule());
		for (Class<? extends Module> c : new ArrayList<>(ar))
			if (c.getName().equals(this.getClass().getName())) ar.remove(c);
		
		return ar;
	}
	
	default List<Class<? extends Module>> dependOnModule() throws IOException {
		return new ArrayList<>();
	}
	
	default boolean canLoad() throws IOException {
		if (loaded()) return false;
		if (!moduleRegistered.contains(getName())) return false;
		ArrayList<Class<? extends Module>> dep = new ArrayList<>(dependClean());
		for (Map.Entry<Class<? extends Module>, Module> s : Manifest.module.entrySet())
			if (s.getValue().loaded()) dep.remove(s.getKey());
		return dep.isEmpty();
	}
	
	default void reg(ArrayList<String> ar) throws IllegalStateException {
		if (ar.contains(getName())) throw new IllegalStateException();
		ar.add(getName());
	}
}
