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
import Ozone.Watcher.Version;
import io.sentry.Scope;
import io.sentry.Sentry;
import io.sentry.protocol.User;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Map;


public class SharedBootstrap {

    public static LibraryLoader libraryLoader;
    public static boolean customBootstrap;
    private static boolean runtime, classpath, atomic;


    static {
        Sentry.init(options -> {
            options.setDsn("https://cd76eb6bd6614c499808176eaaf02b0b@o473752.ingest.sentry.io/5509036");
            options.setRelease("Ozone." + Version.semantic + ":" + "Desktop." + Settings.Version.semantic);
            options.setEnvironment(Propertied.Manifest.getOrDefault("Github-CI", "no").equals("Github-CI") ? "release" : "dev");
        });
        Sentry.configureScope(SharedBootstrap::registerSentry);
    }

    public static void classloaderNoParent() {
        SharedBootstrap.libraryLoader = new LibraryLoader(new URL[]{SharedBootstrap.class.getProtectionDomain().getCodeSource().getLocation()}, null);
    }

    public static void registerSentry(Scope scope) {
        User user = new User();
        String id = "null";
        try {
            String usr = System.getProperty("user.name");
            id = String.valueOf(ByteBuffer.wrap(MessageDigest.getInstance("SHA-256").digest(usr.getBytes())).getLong());//one way hash
        } catch (Throwable e) {
            Sentry.captureException(e);
        }
        user.setId(id);
        scope.setUser(user);
        scope.setTag("Ozone.Desktop.Version", Settings.Version.semantic);
        scope.setTag("Ozone.Core.Version", Version.semantic);
        scope.setTag("Operating.System", System.getProperty("os.name") + " x" + System.getProperty("sun.arch.data.model"));
        scope.setTag("Java.Version", System.getProperty("java.version"));
        for (Map.Entry<String, String> e : Propertied.Manifest.entrySet())
            scope.setTag(e.getKey(), e.getValue());
        StringBuilder sb = new StringBuilder("Library List:\n");
        for (URL u : libraryLoader.getURLs()) sb.append("-").append(u.toString()).append("\n");
        scope.setContexts("Loaded.Library", sb.toString());
    }

    protected static void loadAtomic() throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (atomic) throw new IllegalStateException("Atom dependency already loaded");
        atomic = true;
        ArrayList<String> se = (ArrayList<String>) libraryLoader.loadClass("Main.LoadAtom").getMethod("main", String[].class).invoke(null, (Object) new String[0]);
        for (String s : se)
            libraryLoader.addURL(new URL(s));
    }

    public static void loadRuntime() throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (runtime) throw new IllegalStateException("Runtime dependency already loaded");
        runtime = true;
        for (Dependency d : Dependency.dependencies) {
            if (!d.type.equals(Dependency.Type.runtime)) continue;
            libraryLoader.addURL(new URL(d.getDownload()));
        }
        loadAtomic();
    }

    public static String getJitpack(String orgRepo, String type, String hash) {
        return "https://jitpack.io/com/github/" + orgRepo.replace('.', '/') + "/" + type + "/" + hash + "/" + type + "-" + hash + ".jar";
    }

    public static void loadClasspath() throws MalformedURLException {
        if (classpath) throw new IllegalStateException("Classpath dependency already loaded");
        classpath = true;
        for (String s : System.getProperty("java.class.path").split(System.getProperty("os.name").toUpperCase().contains("WIN") ? ";" : ":"))
            //if(s.contains("gson"))continue;else
            libraryLoader.addURL(new File(s));
    }

    public static void loadMain(String classpath, String[] arg) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SharedBootstrap.libraryLoader.loadClass(classpath).getMethod("main", String[].class).invoke(null, (Object) arg);

    }


}