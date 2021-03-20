/*******************************************************************************
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
 ******************************************************************************/

package Ozone.Experimental;

import Atom.Utility.Pool;
import Ozone.Internal.Interface;
import Shared.InfoBox;
import mindustry.Vars;

public class SwingBox implements Experimental {
	
	@Override
	public void run() {
		Interface.showInput("Title", s -> Interface.showInput("Text", s1 -> {
			Pool.submit(() -> {
				try {
					InfoBox.requireDisplay();
					InfoBox.infoBox(s, s1);
				}catch (Throwable t) {
					Vars.ui.showException(t);
				}
			});
		}));
	}
}
