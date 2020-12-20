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

import arc.ApplicationListener;
import arc.backend.sdl.SdlApplication;
import arc.backend.sdl.SdlConfig;

public class SdlAppWithExtraStep extends SdlApplication {
	public SdlAppWithExtraStep(ApplicationListener listener, SdlConfig config) {
		super(listener, config);
	}
	
	@Override
	public void exit() {
		super.exit();
		Thread t = new Thread(() -> {
			try { Thread.sleep(3000); }catch (InterruptedException e) { }
			System.exit(0);
		});
		t.setDaemon(true);//beware of stall
		t.start();
		
	}
}
