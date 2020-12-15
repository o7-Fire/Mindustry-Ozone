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

import Ozone.Test.Test;
import mindustry.Vars;
import mindustry.gen.Icon;

import java.util.ArrayList;

public class DebugMenuDialog extends OzoneBaseDialog {
    private volatile boolean running;

    public DebugMenuDialog() {
        super("Debug Menu");
        icon = Icon.pause;
    }

    void setup() {
        cont.clear();
        for (Class<? extends Test> t : Test.getRawTestKit())
            cont.button(t.getSimpleName(), Icon.productionSmall, () -> {
                start(t.getSimpleName());
                new Thread(() -> {
                    try {
                        ArrayList<Test.Result> results = t.getConstructor().newInstance().run();
                        StringBuilder sb = new StringBuilder();
                        stop();
                        if (results.isEmpty()) sb.append("No Result Found");
                        for (Test.Result r : results)
                            sb.append(r.reason).append(": ").append(r.success ? "Success" : "Failed").append(" in ").append(r.duration).append("ms\n");
                        Vars.ui.showInfo(sb.toString());
                    }catch (Throwable tw) {
                        stop();
                        Vars.ui.showException(tw);
                    }
                }).start();


            }).growX().disabled(running);
    }

    void start(String name) {
        running = true;
        Vars.ui.loadfrag.show("Starting: " + name);
        setup();
    }

    void stop() {
        running = false;
        setup();
        Vars.ui.loadfrag.hide();
    }
}
