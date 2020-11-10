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

import java.io.File;

public class UpdateLibrary {
    public static void main(String[] args) throws Throwable {
        File atomic = new File("libs/Atomic.jar");
        atomic.delete();
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    if (atomic.exists()) System.exit(0);
                } catch (Throwable t) {

                }
            }
        }).start();
    }
}
