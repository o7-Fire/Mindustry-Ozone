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

import Atom.Reflect.Reflect;
import arc.assets.Loadable;
import mindustry.gen.Building;
import mindustry.gen.Player;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public interface ModuleInterfaced extends Loadable {
	HashMap<Class, HashSet<Class<? extends ModuleInterfaced>>> dependencyMap = new HashMap<>();
	ithoughtinterfacevariableisnotstatic dependsOn = new ithoughtinterfacevariableisnotstatic();
	HashSet<Class<? extends ModuleInterfaced>> registered = new HashSet<>(), moduleLoaded = new HashSet<>(), modulePost = new HashSet<>();
	HashSet<Class<? extends ModuleInterfaced>> empty = new HashSet<>();
	
	default void earlyInit() throws Throwable {
	
	}
	
	default void preInit() throws Throwable {
	
	}
	
	default void update() throws Throwable {
	
	}
	
	default void onTileConfig(@Nullable Player player, Building building, @Nullable Object value) {
	
	}
	
	default void reset() throws Throwable {//User reset, world reset
	
	}
	
	default void init() throws Throwable {
	
	}
	
	default void postInit() throws Throwable {
	
	}
	
	default void onWoldUnload() throws Throwable {//disconnect world
	
	}
	
	default void onWorldLoad() throws Throwable {//connect world
	
	}
	
	default void setRegister() {//invoked from outside
		try {
			reg(registered);
		}catch (IllegalStateException i) {
			throw new IllegalStateException("AbstractModule: \"" + getName() + "\" already registered");
		}
	}
	
	default void setLoaded() {//invoked from outside
		try {
			reg(moduleLoaded);
		}catch (IllegalStateException i) {
			throw new IllegalStateException("AbstractModule: \"" + getName() + "\" already loaded");
		}
	}
	
	default void setPosted() {
		try {
			reg(modulePost);
		}catch (IllegalStateException i) {
			throw new IllegalStateException("AbstractModule: \"" + getName() + "\" already posted");
		}
	}
	
	default boolean loaded() {
		return moduleLoaded.contains(getClass());
	}
	
	default boolean registered() {
		return registered.contains(getClass());
	}
	
	default boolean posted() {
		return modulePost.contains(getClass());
	}
	
	default HashSet<Class<? extends ModuleInterfaced>> dependOnModule() throws IOException {
		HashSet<Class<? extends ModuleInterfaced>> depends = dependencyMap.get(this.getClass());
		if (depends == null) return empty;
		depends.remove(this.getClass());
		return depends;
	}
	
	default boolean canLoadWithDep() {
		if (loaded()) return false;
		if (!registered.contains(this.getClass())) return false;
		return true;
	}
	
	default boolean canLoad() throws IOException {
		if (!canLoadWithDep()) return false;
		HashSet<Class<? extends ModuleInterfaced>> dep = dependOnModule();
		dep.remove(this.getClass());
		return dep.isEmpty();
	}
	
	default void reg(HashSet<Class<? extends ModuleInterfaced>> ar) throws IllegalStateException {
		if (ar.contains(this.getClass())) throw new IllegalStateException();
		ar.add(this.getClass());
	}
	
	class ithoughtinterfacevariableisnotstatic {
		ClassLoader classLoader = this.getClass().getClassLoader();
		
		public boolean add(Class<? extends ModuleInterfaced> aClass) {
			return add(aClass, Reflect.getCallerClass());
		}
		
		protected boolean add(Class<? extends ModuleInterfaced> aClass, String caller) {
			try {
				Class c = classLoader.loadClass(caller);
				Class sup = c.getSuperclass();
				if (!dependencyMap.containsKey(c)) dependencyMap.put(c, new HashSet<>());
				HashSet<Class<? extends ModuleInterfaced>> hashSet = dependencyMap.get(c);
				if (sup.isInstance(sup)) {
					hashSet.add(sup);
				}
				return hashSet.add(aClass);
			}catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			
		}
		
		public boolean addAll(Collection<? extends Class<? extends ModuleInterfaced>> c) {
			for (Class<? extends ModuleInterfaced> e : c)
				add(e, Reflect.getCallerClass());
			return true;
		}
	}
}