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

import Ozone.Manifest;
import arc.Core;
import arc.scene.ui.Label;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Interval;
import mindustry.Vars;
import mindustry.gen.Icon;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnvironmentInformation extends OzoneBaseDialog {
    Table table = new Table();
    ScrollPane scrollPane = new ScrollPane(table);
    Interval timer = new Interval();

    public EnvironmentInformation() {
        super("Environment Information");
        icon = Icon.info;
    }

    void setup() {
        table = new Table();
        scrollPane = new ScrollPane(table);
        cont.clear();
        generate();
        cont.add(scrollPane).growX().growY();
    }

    void update() {
        if (isShown() && timer.get(20f)) {
            generate();
        }
    }

    void generate() {
        table.clearChildren();
        add("Player Name", Vars.player.name);
        add("UUID", Core.settings.getString("uuid"));
        try {
            Field f = Core.settings.getClass().getDeclaredField("values");
            f.setAccessible(true);
            ObjectMap<String, Object> values = (ObjectMap<String, Object>) f.get(Core.settings);
            ArrayList<String> yikes = new ArrayList<>();
            for (String s : values.keys()) yikes.add(s);
            String[] keys = yikes.toArray(new String[0]);
            List<String> key = Arrays.stream(keys).filter(s -> s.startsWith("usid-")).collect(Collectors.toList());
            for (String k : key) {
                add(k, Core.settings.getString(k));
            }
        }catch (Throwable t) {
            t.printStackTrace();
        }
    }

    void add(String title, String value) {
        if (value == null) value = "null";
        Label l = new Label(title + ":");
        table.add(l).growX();
        String finalValue = value;
        table.field(value, s -> {}).disabled(true).growX();
        table.button(Icon.copy, () -> {
            Core.app.setClipboardText(finalValue);
            Manifest.toast("Copied");
        }).right();
        table.row();
    }
}
