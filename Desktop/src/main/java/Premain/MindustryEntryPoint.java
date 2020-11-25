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

package Premain;

import Ozone.Desktop.Bootstrap.SharedBootstrap;
import io.sentry.Sentry;

import java.io.File;
import java.nio.file.Files;

public class MindustryEntryPoint {
    public static void main(String[] args) {
        try {
            System.out.println("Initializing Ozone Environment");
            SharedBootstrap.classloaderNoParent();
            SharedBootstrap.loadStandalone();
            SharedBootstrap.loadMods();
            SharedBootstrap.loadClasspath();
            SharedBootstrap.libraryLoader.addURL(new File(args[0]));
            SharedBootstrap.loadMain("Main.OzoneMindustry", args);
        } catch (Throwable t) {
            try {
                Files.write(new File(MindustryEntryPoint.class.getName() + ".txt").toPath(), t.toString().getBytes());
            } catch (Throwable ignored) {
            }
            t.printStackTrace();
            if (t.getCause() != null) t = t.getCause();
            Sentry.captureException(t);
            System.exit(1);
        }
    }
}
