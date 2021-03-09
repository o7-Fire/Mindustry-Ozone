/*
 * Copyright 2021 Itzbenz
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

/* o7 Inc 2021 Copyright

  Licensed under the o7 Inc License, Version 1.0.1, ("the License");
  You may use this file but only with the License. You may obtain a
  copy of the License at
  
  https://github.com/o7-Fire/Mindustry-Ozone/Licenses
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the license for the specific language governing permissions and
  limitations under the License.
*/

package Premain;

import Ozone.Bootstrap.OzoneBootstrap;
import Ozone.Main;
import Ozone.Manifest;
import Shared.LoggerMode;
import arc.Core;
import arc.util.Log;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import mindustry.mod.Mod;
import org.jetbrains.annotations.NotNull;

public class EntryPoint extends Mod {
	static {
		try {
			LoggerMode.loadLogger();
			Log.info("Ozone Standalone");
			OzoneBootstrap.init();
		}catch (Throwable t) {
			t.printStackTrace();
			Log.err(t);
			Sentry.captureException(t);
		}
		@NotNull ITransaction s = null;
		try { s = Sentry.startTransaction("Init", "Early Init"); }catch (Throwable ignored) {}
		try {
			Main.earlyInit();
			if (s != null) s.finish();
		}catch (Throwable t) {
			if (s != null) {
				s.setThrowable(t);
				s.setStatus(SpanStatus.INTERNAL_ERROR);
			}
			Sentry.captureException(t);
			Log.err(t);
			throw new RuntimeException(t);
		}
	}
	
	public EntryPoint() {
		@NotNull ITransaction s = null;
		try { s = Sentry.startTransaction("Init", "Pre Init"); }catch (Throwable ignored) {}
		if (Core.settings != null) {
			Core.settings.put("crashreport", true);
			Core.settings.put("uiscalechanged", false);//shut
		}
		try {
			Manifest.ozone = this;
			Main.preInit();
			if (s != null) s.finish();
		}catch (Throwable t) {
			if (s != null) {
				s.setThrowable(t);
				s.setStatus(SpanStatus.INTERNAL_ERROR);
			}
			Sentry.captureException(t);
			Log.err(t);
			throw new RuntimeException(t);
		}
	}
	
	@Override
	public void init() {
		@NotNull ITransaction s = null;
		try { s = Sentry.startTransaction("Init", "Init"); }catch (Throwable ignored) {}
		try {
			Main.init();
			if (s != null) s.finish();
		}catch (Throwable t) {
			if (s != null) {
				s.setThrowable(t);
				s.setStatus(SpanStatus.INTERNAL_ERROR);
			}
			t.printStackTrace();
			Sentry.captureException(t);
			Log.err(t);
		}
	}
	
	@Override
	public void loadContent() {
		@NotNull ITransaction s = null;
		try { s = Sentry.startTransaction("Init", "Load Content"); }catch (Throwable ignored) {}
		try {
			Main.loadContent();
			if (s != null) s.finish();
		}catch (Throwable t) {
			if (s != null) {
				s.setThrowable(t);
				s.setStatus(SpanStatus.INTERNAL_ERROR);
			}
			t.printStackTrace();
			Sentry.captureException(t);
			Log.err(t);
		}
	}
}
