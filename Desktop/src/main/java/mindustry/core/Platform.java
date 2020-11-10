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

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mindustry.core;

import arc.Core;
import arc.files.Fi;
import arc.func.Cons;
import arc.math.Rand;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Structs;
import arc.util.serialization.Base64Coder;
import mindustry.Vars;
import mindustry.mod.Scripts;
import mindustry.net.ArcNetProvider;
import mindustry.net.Net.NetProvider;
import mindustry.type.Publishable;
import mindustry.ui.dialogs.FileChooser;
import rhino.Context;

import java.net.URL;
import java.net.URLClassLoader;

public interface Platform {
    default Class<?> loadJar(Fi jar, String mainClass) throws Exception {
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jar.file().toURI().toURL()}, this.getClass().getClassLoader());
        return classLoader.loadClass(mainClass);
    }

    default void updateLobby() {
    }

    default void inviteFriends() {
    }

    default void publish(Publishable pub) {
    }

    default void viewListing(Publishable pub) {
    }

    default void viewListingID(String mapid) {
    }

    default Seq<Fi> getWorkshopContent(Class<? extends Publishable> type) {
        return new Seq(0);
    }

    default void openWorkshop() {
    }

    default NetProvider getNet() {
        return new ArcNetProvider();
    }

    default Scripts createScripts() {
        return new Scripts();
    }

    default Context getScriptContext() {
        Context c = Context.enter();
        c.setOptimizationLevel(9);
        return c;
    }

    default void updateRPC() {
    }

    default String getUUID() {
        String uuid = Core.settings.getString("uuid", "");
        if (uuid.isEmpty()) {
            byte[] result = new byte[8];
            (new Rand()).nextBytes(result);
            uuid = new String(Base64Coder.encode(result));
            Core.settings.put("uuid", uuid);
            return uuid;
        }else {
            return uuid;
        }
    }

    default void shareFile(Fi file) {
    }

    default void export(String name, String extension, Platform.FileWriter writer) {
        if (!Vars.ios) {
            Vars.platform.showFileChooser(false, extension, (file) -> {
                Vars.ui.loadAnd(() -> {
                    try {
                        writer.write(file);
                    }catch (Throwable var3) {
                        Vars.ui.showException(var3);
                        Log.err(var3);
                    }

                });
            });
        }else {
            Vars.ui.loadAnd(() -> {
                try {
                    Fi result = Core.files.local(name + "." + extension);
                    writer.write(result);
                    Vars.platform.shareFile(result);
                }catch (Throwable var4) {
                    Vars.ui.showException(var4);
                    Log.err(var4);
                }

            });
        }

    }

    default void showFileChooser(boolean open, String extension, Cons<Fi> cons) {
        (new FileChooser(open ? "@open" : "@save", (file) -> {
            return file.extEquals(extension);
        }, open, (file) -> {
            if (!open) {
                cons.get(file.parent().child(file.nameWithoutExtension() + "." + extension));
            }else {
                cons.get(file);
            }

        })).show();
    }

    default void showMultiFileChooser(Cons<Fi> cons, String... extensions) {
        if (Vars.mobile) {
            this.showFileChooser(true, extensions[0], cons);
        }else {
            (new FileChooser("@open", (file) -> {
                return Structs.contains(extensions, file.extension().toLowerCase());
            }, true, cons)).show();
        }

    }

    default void hide() {
    }

    default void beginForceLandscape() {
    }

    default void endForceLandscape() {
    }

    public interface FileWriter {
        void write(Fi var1) throws Throwable;
    }
}
