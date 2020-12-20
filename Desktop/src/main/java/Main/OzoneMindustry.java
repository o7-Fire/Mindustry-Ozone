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

import Ozone.Desktop.Bootstrap.SharedBootstrap;
import arc.Files;
import arc.backend.sdl.SdlApplication;
import arc.backend.sdl.SdlConfig;
import arc.backend.sdl.jni.SDL;
import arc.func.Cons;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.desktop.DesktopLauncher;

//basically its a patcher
public class OzoneMindustry extends DesktopLauncher {
	public static long start = System.currentTimeMillis() / 1000;
	public static String[] arg;
	public static boolean disableRPC;
	
	public OzoneMindustry(String[] args) {
		super(args);
	}
	
	public static void main(String[] args) {
		//  new Thread(()-> {
		arg = args;
		SharedBootstrap.customBootstrap = true;
		try {
			Vars.loadLogger();
			new SdlApplication(new DesktopLauncher(args), new SdlConfig() {
				{
					this.title = "Mindustry-Ozone";
					this.maximized = true;
					this.stencil = 8;
					this.width = 900;
					this.height = 700;
					this.setWindowIcon(Files.FileType.internal, "icons/path47.png");
				}
			});
		}catch (Throwable var2) {
			var2.printStackTrace();
			handleCrash(var2);
			throw var2;
		}
		// }).start();
		
	}
	
	static void handleCrash(Throwable e) {
		try {
			Cons<Runnable> dialog = Runnable::run;
			boolean badGPU = false;
			String finalMessage = Strings.getFinalMessage(e);
			String total = Strings.getCauses(e).toString();
			if (total.contains("Couldn't create window") || total.contains("OpenGL 2.0 or higher") || total.toLowerCase().contains("pixel format") || total.contains("GLEW") || total.contains("unsupported combination of formats")) {
				dialog.get(() -> message(total.contains("Couldn't create window") ? "A graphics initialization error has occured! Try to update your graphics drivers:\n" + finalMessage : "Your graphics card does not support the right OpenGL features.\nTry to update your graphics drivers. If this doesn't work, your computer may not support Mindustry.\n\nFull message: " + finalMessage));
				badGPU = true;
			}
			SDL.SDL_ShowSimpleMessageBox(SDL.SDL_MESSAGEBOX_ERROR, "Oh Nein", e.toString());
		}catch (Throwable ignored) {}
	}
	
	private static void message(String message) {
		SDL.SDL_ShowSimpleMessageBox(16, "oh nein", message);
	}
	
	@Override
	public void updateRPC() {
		if (!disableRPC) super.updateRPC();
	}
}
