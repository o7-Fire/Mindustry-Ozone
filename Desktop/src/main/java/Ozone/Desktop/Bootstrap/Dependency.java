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

package Ozone.Desktop.Bootstrap;

import Ozone.Desktop.Propertied;
import io.sentry.Sentry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Dependency {
    public static ArrayList<Dependency> dependencies = new ArrayList<>();
    public static ArrayList<String> url = new ArrayList<>();
    static ArrayList<String> integrated = new ArrayList<>(Arrays.asList(
            "arc-core", "backend-sdl", "core", "desktop", "annotations"
    )),
            standalone = new ArrayList<>(Arrays.asList(
                    "Atomic", "Desktop"
            ));

    static {
        try {
            String last = "";
            HashMap<String, String> hash = new HashMap<>();
            for (String s : new String(Propertied.getResource("dependencies").readAllBytes()).split("\n")) {
                if (last.equals("version")) {
                    dependencies.add(new Dependency(hash.get("groupId"), hash.get("artifactId"), hash.get("version")));
                    hash = new HashMap<>();
                }
                if (last.isEmpty()) {
                    String[] sys = s.split("=");
                    last = sys[0];
                    if (last.equals("url")) {
                        url.add(sys[0]);
                    }else
                        hash.put(sys[0], sys[1]);
                }
            }


        }catch (Throwable t) {
            t.printStackTrace();
            Sentry.captureException(t);
            throw new RuntimeException("Failed to parse dependencies", t);
        }
    }

    public String groupId, artifactId, version;
    public Type type;

    public Dependency(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        if (integrated.contains(artifactId))
            type = Type.Integrated;
        else if (groupId.equals("com.github.o7-Fire.Atomic-Library"))
            type = Type.Standalone;
        else
            type = Type.Runtime;

    }

    public String getDownload(String url) {
        return String.format("%s%s/%s/%s/%s-%s.jar", url,
                groupId.replace('.', '/'), artifactId, version,
                artifactId, version);

    }

    public enum Type {Standalone, Runtime, Integrated}
}
