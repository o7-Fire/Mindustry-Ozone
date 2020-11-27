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
import arc.math.Rand;
import arc.struct.ObjectMap;
import arc.util.serialization.Base64Coder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Identification {
    public static ObjectMap<String, Object> getValue() throws NoSuchFieldException, IllegalAccessException {
        Field f = Core.settings.getClass().getDeclaredField("values");
        f.setAccessible(true);
        return (ObjectMap<String, Object>) f.get(Core.settings);
    }

    public static void changeID() throws NoSuchFieldException, IllegalAccessException {
        ObjectMap<String, Object> values = getValue();
        ArrayList<String> yikes = new ArrayList<>();
        for (String s : values.keys()) yikes.add(s);
        String[] keys = yikes.toArray(new String[0]);
        List<String> key = Arrays.stream(keys).filter(s -> s.startsWith("usid-") || s.startsWith("uuid")).collect(Collectors.toList());

        for (String s : key) Core.settings.put(s, getRandomUID());

    }

    public static String getRandomUID() {
        byte[] bytes = new byte[8];
        (new Rand()).nextBytes(bytes);
        return new String(Base64Coder.encode(bytes));
    }

    public static String getUsid(String ip) {
        if (ip.contains("/")) {
            ip = ip.substring(ip.indexOf("/") + 1);
        }

        if (Core.settings.getString("usid-" + ip, (String) null) != null) {
            return Core.settings.getString("usid-" + ip, (String) null);
        }else {
            String result = getRandomUID();
            Core.settings.put("usid-" + ip, result);
            return result;
        }
    }
}
