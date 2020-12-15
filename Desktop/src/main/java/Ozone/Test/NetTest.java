package Ozone.Test;/*
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

import Atom.File.FileUtility;
import Ozone.Desktop.Pre.DownloadSwing;
import Ozone.Pre.Download;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;

public class NetTest extends Test {

    public URL url;

    public static File file() {
        File f = FileUtility.temp();
        System.out.println(f.getAbsolutePath());
        return f;
    }

    @Override
    public ArrayList<Result> run() {
        ping();
        addTest(() -> url = new URL("https://github.com/Anuken/Mindustry/releases/download/v121.2/Mindustry.jar"), "URL conversion");
        addTest(this::downloadGUI, "Downloading with Swing");
        addTest(this::download, "Downloading classic way");
        return testResult;
    }

    public void ping() {
        long s = System.currentTimeMillis();
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            boolean reachable = address.isReachable(10000);
            testResult.add(new Result("www.google.com" + (reachable ? "" : "un") + "reachable", reachable, s));
        }catch (Throwable e) {
            testResult.add(new Result(new RuntimeException("Unable to ping google.com", e), s));
        }

    }

    public void downloadGUI() {
        DownloadSwing d = new DownloadSwing(url, file());
        d.display();
        d.run();
    }

    public void download() {
        Download d = new Download(url, file());
        d.run();
    }
}
