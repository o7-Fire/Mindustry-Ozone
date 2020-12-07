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

package Ozone.Desktop.Patch;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {
    private static String gAuth = "";


    public static String getArtifactLocation(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + gAuth);
        connection.setInstanceFollowRedirects(false);
        String main = connection.getHeaderField("Location");
        connection.disconnect();
        if (main == null)
            throw new NullPointerException("Cannot get artifact location");
        return main;
    }


    public static void async() {

    }
}
