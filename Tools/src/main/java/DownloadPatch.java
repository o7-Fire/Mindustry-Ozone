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

import Ozone.Pre.Download;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DownloadPatch {
    public static void main(String[] args) throws Throwable, InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();
        String trg = "https://raw.githubusercontent.com/Anuken/Mindustry/master/core/src/mindustry/";
        File target = new File("Tools/src/main/java/mindustry/");
        target.mkdirs();
        ArrayList<File> files = recurse(new File("Desktop/src/main/java/mindustry"));
        ArrayList<String> list = new ArrayList<>();
        files.forEach(f -> {
            list.add(f.getPath().substring(32));
        });
        ArrayList<Future> f = new ArrayList<>();
        for (String s : list)
            f.add(es.submit(() -> {
                try {
                    Download d = new Download(new URL(trg + s), new File(target, s));
                    d.print(see -> System.out.println(see));
                    d.run();
                }catch (Throwable ignored) {}
            }));
        for (Future fe : f)
            fe.get();
    }

    public static ArrayList<File> recurse(File f) {
        ArrayList<File> files = new ArrayList<>();
        for (File c : Objects.requireNonNull(f.listFiles())) {
            if (c.isDirectory())
                files.addAll(recurse(c));
            else
                files.add(c);
        }
        return files;
    }
}
