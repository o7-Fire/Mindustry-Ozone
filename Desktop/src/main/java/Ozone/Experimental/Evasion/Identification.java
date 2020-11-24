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

package Ozone.Experimental.Evasion;

import arc.Core;
import arc.struct.ObjectMap;
import io.sentry.Sentry;
import mindustry.Vars;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Identification {
    public static void changeID() throws NoSuchFieldException, IllegalAccessException {
            //does it pass by reference
            ObjectMap<String, Object> values = (ObjectMap<String, Object>) Core.settings.getClass().getDeclaredField("values").get(Core.settings);
            String[] keys = values.keys().toSeq().toArray(String[].class);
            List<String> key = Arrays.stream(keys).filter(s -> s.startsWith("usid-") || s.startsWith("uuid")).collect(Collectors.toList());
            for(String s : key) Core.settings.put(s, null);

    }
}
