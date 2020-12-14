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

package Ozone.Desktop;

import io.sentry.Sentry;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Propertied {
    public static HashMap<String, String> Manifest;
    static {
        Manifest = read("Manifest.properties");
    }

    public static InputStream getResource(String name) {
        InputStream is = ClassLoader.getSystemResourceAsStream(name);
        if (is == null) is = Propertied.class.getProtectionDomain().getClassLoader().getResourceAsStream(name);
        return is;
    }

    public static HashMap<String, String> read(String name) {
        HashMap<String, String> temp;
        try {
            temp = parse(new String(getResource(name).readAllBytes()));
        }catch (Throwable g) {
            temp = new HashMap<>();
            Sentry.captureException(g);
        }
        return temp;
    }

    public static String reverseParse(HashMap<String, String> se) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> s : se.entrySet())
            sb.append(s.getKey()).append("=").append(s.getValue()).append("\n");
        return sb.toString();
    }

    public static HashMap<String, String> parse(String se) {
        HashMap<String, String> te = new HashMap<>();
        for (String s : se.split("\n")) {
            if (s.endsWith("\r"))
                s = s.substring(0, s.length() - 1);
            if (!s.startsWith("#"))
                te.put(s.split("=")[0], s.split("=")[1]);
        }
        return te;
    }
}
