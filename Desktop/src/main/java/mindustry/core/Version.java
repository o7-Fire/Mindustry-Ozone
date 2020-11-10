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

package mindustry.core;

import arc.Core;
import arc.Files.FileType;
import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.util.OS;
import arc.util.Strings;
import arc.util.io.PropertiesUtils;

public class Version {
    /**
     * Build type. 'official' for official releases; 'custom' or 'bleeding edge' are also used.
     */
    public static String type = "unknown";
    /**
     * Build modifier, e.g. 'alpha' or 'release'
     */
    public static String modifier = "unknown";
    /**
     * Number specifying the major version, e.g. '4'
     */
    public static int number;
    /**
     * Build number, e.g. '43'. set to '-1' for custom builds.
     */
    public static int build = 0;
    /**
     * Revision number. Used for hotfixes. Does not affect server compatibility.
     */
    public static int revision = 0;
    /**
     * Whether version loading is enabled.
     */
    public static boolean enabled = true;

    public static void init() {
        if (!enabled) return;

        Fi file = OS.isAndroid || OS.isIos ? Core.files.internal("version.properties") : new Fi("version.properties", FileType.internal);

        ObjectMap<String, String> map = new ObjectMap<>();
        PropertiesUtils.load(map, file.reader());

        type = map.get("type");
        number = Integer.parseInt(map.get("number", "4"));
        modifier = map.get("modifier");
        if (map.get("build").contains(".")) {
            String[] split = map.get("build").split("\\.");
            try {
                build = Integer.parseInt(split[0]);
                revision = Integer.parseInt(split[1]);
            }catch (Throwable e) {
                e.printStackTrace();
                build = -1;
            }
        }else {
            build = Strings.canParseInt(map.get("build")) ? Integer.parseInt(map.get("build")) : -1;
        }
    }

    /**
     * get menu version without colors
     */
    public static String combined() {
        if (build == -1) {
            return "custom build";
        }
        return (type.equals("official") ? modifier : type) + " build " + build + (revision == 0 ? "" : "." + revision) + " [ [royal]Ozone[accent]:[pink]" + Ozone.Watcher.Version.semantic + "[accent]:[green]" + Premain.Version.semantic + "[white] ]";
    }
}
