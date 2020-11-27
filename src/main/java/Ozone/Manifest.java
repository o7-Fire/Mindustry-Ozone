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
import arc.Core;
import arc.math.Interp;
import arc.net.Client;
import arc.scene.actions.Actions;
import arc.scene.event.Touchable;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.net.ArcNetProvider;
import mindustry.net.Net;
import mindustry.ui.Styles;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class Manifest {

    public static OzoneMenu menu;
    public static CommandsListFrag commFrag;
    public static ArrayList<Class<?>> settings = new ArrayList<>();
    public static String lastServer = "";

    public static String getMindustryVersion() {
        return Version.build + (Version.revision == 0 ? "" : "." + Version.revision);
    }

    public static String getCurrentServerIP() {
        if (!Vars.net.active()) return lastServer;
        try {
            Net.NetProvider n = Reflect.getField(Vars.net.getClass(), "provider", Vars.net);
            if (!(n instanceof ArcNetProvider)) return null;
            ArcNetProvider arc = (ArcNetProvider) n;
            Client c = Reflect.getField(arc.getClass(), "client", arc);
            return c.getRemoteAddressTCP().getHostName() + ":" + c.getRemoteAddressTCP().getPort();
        }catch (Throwable ignored) { }
        return lastServer;

    }

    public static void toast(String text) {
        Table table = new Table();
        table.touchable = Touchable.disabled;
        table.setFillParent(true);
        table.actions(Actions.fadeOut(4.0F, Interp.fade), Actions.remove());
        table.bottom().add(text).style(Styles.outlineLabel).padBottom(80);
        Core.scene.add(table);
    }

    public static ArrayList<Field> getSettings() {
        ArrayList<Field> f = new ArrayList<>();
        //must static class
        for (Class<?> c : settings)
            f.addAll(Arrays.asList(c.getDeclaredFields()));
        return f;
    }
}
