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

package Ozone.Bot;

import Ozone.Internal.AbstractModule;
import Ozone.Internal.Interface;
import arc.Events;
import mindustry.game.EventType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class VirtualController extends AbstractModule {
	public static Set<VirtualPlayer> virtualPlayers = Collections.synchronizedSet(new HashSet<>());
	
	public static void create() {
		
		Interface.showInput("Virtual Player Name", s -> {
			VirtualPlayer v = new VirtualPlayer();
			v.name = s;
			virtualPlayers.add(v);
			
		});
	}
	
	@Override
	public void init() throws Throwable {
		Events.run(EventType.Trigger.update, this::update);
	}
	
	public void update() {
		for (VirtualPlayer v : virtualPlayers)
			v.update();
	}
	
	public static VirtualPlayer clone(VirtualPlayer or) throws CloneNotSupportedException {
		VirtualPlayer v = or.clone();
		virtualPlayers.add(v);
		return v;
	}
	
	public static void delete(VirtualPlayer v) {
		virtualPlayers.remove(v);
	}
}
