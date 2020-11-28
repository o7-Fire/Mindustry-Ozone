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

package Ozone.Desktop.UI;

import Ozone.Desktop.Bootstrap.Dependency;
import Ozone.Desktop.Bootstrap.LibraryLoader;
import Ozone.Desktop.Propertied;
import Ozone.Experimental.Evasion.Identification;
import Ozone.Manifest;
import arc.Core;
import arc.scene.ui.Label;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Interval;
import arc.util.Log;
import io.sentry.Sentry;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.gen.Icon;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnvironmentInformation extends OzoneBaseDialog {
    Table table = new Table();
    ScrollPane scrollPane = new ScrollPane(table);
    Interval timer = new Interval();

    public EnvironmentInformation() {
        super("Environment Information");
        icon = Icon.info;
        buttons.button("Refresh", Icon.refresh, this::generate).size(210f, 64f);

    }

    void setup() {
        table = new Table();
        scrollPane = new ScrollPane(table);
        cont.clear();
        generate();
        cont.add(scrollPane).growX().growY();
    }

    void update() {

    }

    void generate() {
        table.clearChildren();

        add("Player Name", Vars.player.name);
        add("UUID", Core.settings.getString("uuid"));
        //add("Current Millis", System.currentTimeMillis());
        add(Propertied.Manifest);
        add(Version.h);
        dep();
        uid();
    }

    void dep() {
        try {
            for (URL u : ((LibraryLoader) this.getClass().getClassLoader()).getURLs()) {
                add("Library", u.toExternalForm());
            }
        }catch (Throwable ignored) {}
        for (Dependency d : Dependency.dependencies)
            try {
                add(d.toString(), d.getDownload());
            }catch (Throwable i) {
                add(d.toString(), i.toString());
            }
    }

    void uid() {
        try {
            ObjectMap<String, Object> values = Identification.getValue();
            ArrayList<String> yikes = new ArrayList<>();
            for (String s : values.keys()) yikes.add(s);
            String[] keys = yikes.toArray(new String[0]);
            List<String> key = Arrays.stream(keys).filter(s -> s.startsWith("usid-")).collect(Collectors.toList());
            for (String k : key) {
                add(k, Core.settings.getString(k));
            }
        }catch (Throwable t) {
            Log.err(t);
            Sentry.captureException(t);
            t.printStackTrace();
        }
        for (Map.Entry<Object, Object> s : System.getProperties().entrySet())
            add(s.getKey().toString(), s.getValue().toString());
    }

    void add(Map<String, String> map) {
        for (Map.Entry<String, String> s : map.entrySet())
            add(s.getKey(), s.getValue());
    }

    void add(String title, String value) {
        if (value == null) value = "null";
        Label l = new Label(title + ":");
        table.add(l).growX();
        String finalValue = value;
        table.row();
        table.field(value, s -> {
            generate();
            Core.app.setClipboardText(finalValue);
            Manifest.toast("Copied");
        }).expandX().disabled(true).growX();
        /*
        table.button(Icon.copy, () -> {
            Core.app.setClipboardText(finalValue);
            Manifest.toast("Copied");
        }).right();

         */
        table.row();
    }
}
