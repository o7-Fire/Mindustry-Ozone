/*
 * Copyright 2020 Itzbenz
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

package Ozone.Patch;

import mindustry.ai.Pathfinder;
import mindustry.game.Team;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//Dirty hack
public class Hack {
	
	public static int pathCost(Team team, int tile, int type) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Object o = Pathfinder.costTypes.get(type);
		Method m = o.getClass().getMethod("getCost", Team.class, int.class);
		m.setAccessible(true);
		return (int) m.invoke(o, team, tile);
	}
}
