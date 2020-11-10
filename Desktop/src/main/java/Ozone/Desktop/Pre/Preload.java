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

package Ozone.Desktop.Pre;


//have you load library today ?
public class Preload {
    /*
    private static volatile boolean init = false;


    public static boolean checkAtomic(String AtomDownload, File atom) {
        //try to download if doesn't exists
        if (atom.exists() && EntryPoint.desktopAtomic.exists()) return true;
            try {
                //Inform user
                Log.infoTag("Ozone", "Downloading Library");
                //there is no "no" option
                SDL.SDL_ShowSimpleMessageBox(64, "Ozone", "Atomic Library doesn't exists/not fully downloaded. Click OK to continue");
                //how to download a file synchronously & show Progress
                URL jitpack = new URL(AtomDownload);
                DownloadSwing download = new DownloadSwing(jitpack, atom);
                download.display();
                download.run();
                jitpack = new URL(EntryPoint.desktopAtomicURL);
                download = new DownloadSwing(jitpack, EntryPoint.desktopAtomic);
                download.display();
                download.run();
                //its exists
                if (atom.exists()) {
                    SDL.SDL_ShowSimpleMessageBox(64, "Ozone", "Atom library has been downloaded: " + atom.getAbsolutePath());
                } else {
                    throw new FileNotFoundException(atom.getAbsolutePath() + " reason: idk i have no idea, prob a bug");
                }
                //if its reach to here, then its must not exists and there no internet connection ? wtf
            } catch (Throwable t) {
                //oh no internet error
                SDL.SDL_ShowSimpleMessageBox(16, "Ozone", "Atom library can't be downloaded: " + t.toString());
                Log.err(t);
            }
        //reliable shit
        return atom.exists();
    }

    public static void incites(File atom, String AtomDownload, Object clz) throws NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException, FileNotFoundException, ClassNotFoundException {
        //don't run more than once
        if (init) return;
        init = true;
        //check if we are on right classloader, sometime java is full of surprise
        if (!(clz.getClass().getClassLoader() instanceof URLClassLoader))
            throw new RuntimeException(clz + " Classloader is not URLClassloader, how it could be ???");
        //check library needed to load Ozone
        if (!checkAtomic(AtomDownload, atom))
            throw new FileNotFoundException("Atom Library can't be downloaded/not found");
        //Inform users
        Log.infoTag("Ozone", "Loading library");
        //add Atom to URL classloader to be used
        //good ol reflection

        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        Log.infoTag("Ozone", "wArNiNg: An IlLeGaL rEfLeCtIvE aCcEsS oPeRaTiOn HaS oCcUrReD");
        method.setAccessible(true);
        Log.infoTag("Ozone", "Loading: " + atom.getAbsolutePath());
        Log.infoTag("Ozone", "Loading: " + EntryPoint.desktopAtomic.getAbsolutePath());
        method.invoke(clz.getClass().getClassLoader(), atom.toURI().toURL());
        method.invoke(clz.getClass().getClassLoader(), EntryPoint.desktopAtomic.toURI().toURL());
        Class.forName("Atom.DesktopManifest");
        for (File lib : Atom.Manifest.getLibs()) {
            if (!lib.exists()) continue;
            if (!getExtension(lib).equals("jar")) continue;
            Log.infoTag("Ozone", "Loading: " + lib.getAbsolutePath());
            method.invoke(clz.getClass().getClassLoader(), lib.toURI().toURL());
        }

        Log.infoTag("Ozone", "Library loaded by using java.net.URLClassLoader.addURL(java.net.URL)");
        Log.infoTag("Ozone", "Ignore that warning if you see it, its a java shenanigans");
        //shit we did it without any error
    }

    public static String getExtension(File f) {
        String extension = "";
        int i = f.getName().lastIndexOf('.');
        if (i > 0) {
            extension = f.getName().substring(i + 1);
        }
        return extension;
    }

     */
}
