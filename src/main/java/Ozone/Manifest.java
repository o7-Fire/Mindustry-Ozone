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

package Ozone;

import Atom.Reflect.Reflect;
import Ozone.UI.CommandsListFrag;
import Ozone.UI.OzoneMenu;
import arc.net.Client;
import arc.struct.ObjectMap;
import arc.util.io.PropertiesUtils;
import mindustry.Vars;
import mindustry.net.ArcNetProvider;
import mindustry.net.Net;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class Manifest {
    public static final String atomHash;
    public static final String atomFile = "Atomic-" + atomHash + ".jar", libs = "libs";
    public static final String jitpack = "https://jitpack.io/com/github/o7-Fire/Atomic-Library/";
    public static final String date = new Date().toString();
    public static String atomType = "Atomic";
    public static final String atomDownloadLink = "https://jitpack.io/com/github/o7-Fire/Atomic-Library/" + Manifest.atomType + "/" + Manifest.atomHash + "/" + Manifest.atomType + "-" + Manifest.atomHash + ".jar";
    public static OzoneMenu menu;
    public static CommandsListFrag commFrag;
    public static ArrayList<Class<?>> settings = new ArrayList<>();
    public static String lastServer = "";

    static {
        ObjectMap<String, String> Manifest = new ObjectMap<>();
        PropertiesUtils.load(Manifest, new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream("Manifest.properties"))));
        atomHash = Manifest.get("AtomHash");

    }

    public static String getCurrentServerIP() {
        if (!Vars.net.active()) return lastServer;
        try {
            Net.NetProvider n = Reflect.getField(Vars.net.getClass(), "provider", Vars.net);
            if (!(n instanceof ArcNetProvider)) return null;
            ArcNetProvider arc = (ArcNetProvider) n;
            Client c = Reflect.getField(arc.getClass(), "client", arc);
            return c.getRemoteAddressTCP().getHostName();
        }catch (Throwable ignored) {

        }
        return lastServer;

    }

    public static ArrayList<Field> getSettings() {
        ArrayList<Field> f = new ArrayList<>();
        //must static class
        for (Class<?> c : settings)
            f.addAll(Arrays.asList(c.getDeclaredFields()));
        return f;
    }
}
