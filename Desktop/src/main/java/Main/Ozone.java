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

package Main;

import Ozone.Desktop.Manifest;
import Ozone.Desktop.Patch.DesktopPatcher;
import Ozone.Desktop.UI.BotControllerDialog;
import Ozone.Desktop.UI.DebugMenuDialog;
import Ozone.Desktop.UI.EnvironmentInformation;
import Ozone.Desktop.UI.ModsMenu;
import Ozone.Event.DesktopEvent;
import Ozone.Main;
import arc.Core;
import arc.Events;
import mindustry.Vars;
import mindustry.mod.Mod;

/**
 * @author Itzbenz
 */
public class Ozone extends Mod {
	
	public Ozone() {
		Events.on(DesktopEvent.InitUI.class, s -> {
			if (!Vars.headless) {
				Manifest.botControllerDialog = new BotControllerDialog();
				Manifest.modsMenu = new ModsMenu();
				Manifest.envInf = new EnvironmentInformation();
				Manifest.dbgMenu = new DebugMenuDialog();
			}
		});
		
		if (Core.settings != null) {
			Core.settings.put("crashreport", false);
			Core.settings.put("uiscalechanged", false);//shut
		}
	}
	
	
	@Override
	public void init() {
		DesktopPatcher.register();
		Main.init();
	}
	
	@Override
	public void loadContent() {
		DesktopPatcher.async();
		Main.loadContent();
	}

    /*
    public Ozone() {


        //just in case
        library.mkdirs();
        try {
            //Check library
            Preload.incites(atom, Manifest.atomDownloadLink, this);
            //no error thrown ? good
            libraryURLLoaded = true;
        } catch (FileNotFoundException t) {
            libraryURLLoaded = false;
            Log.err("Cant download Atom library: " + t.toString());
        } catch (Throwable t) {
            //Exception just summoned
            libraryURLLoaded = false;
            t.printStackTrace();
            //this shit ain't reliable
            Log.err(t);
            Log.infoTag("Ozone", "Cant load Atom library, try using method 2");
        }
        //we already download it or not ?
        libraryExists = atom.exists();
        //oh already loaded ? no need to continue
        if (libraryURLLoaded) return;
        try {
            //oh we already download it, but its not yet loaded
            if (libraryExists) {
                //make new classloader
                classloader = new URLClassLoader(new URL[]{ozone.toURI().toURL(), atom.toURI().toURL()}, ClassLoader.getSystemClassLoader());
                Class<?> main = classloader.loadClass("MainBackup.OzoneBackup");
                mainMod = (Mod) main.getDeclaredConstructor().newInstance();
                //oh we did it nice
                return;
            } else {
                //wtf man network error ? can't download ? library removed ?
            }
        } catch (Throwable t) {
            //gabe itch
            Log.err(t);
        }
        //sike inform users

    }



    @Override
    public void init() {
        //Library loaded properly ?
        if (libraryURLLoaded) {
            DesktopPatcher.register();
            Main.init();
        } else if (libraryExists && mainMod != null) {//hmmm seem to use method 2
            //alternative
            mainMod.init();
        }
    }

    //same as above
    @Override
    public void loadContent() {
        if (libraryURLLoaded)
            Main.loadContent();
        else if (libraryExists && mainMod != null) {
            mainMod.init();
        }
    }

    */
}
