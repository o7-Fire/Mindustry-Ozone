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
import arc.Core;
import mindustry.Vars;

public class OutOfMemoryException implements Experimental {
	private static void gay() {
		long[][] ary = new long[Integer.MAX_VALUE][Integer.MAX_VALUE];
		long[][] kys = new long[Integer.MAX_VALUE][Integer.MAX_VALUE];
		throw new OutOfMemoryError("kys");
	}
	
	@Override
	public void run() {
		//dont run on this method/thread it will get caught
		Vars.ui.showConfirm("OutOfMemoryException", "You wanna die ?", () -> {
			try {
				Core.app.post(OutOfMemoryException::gay);
			}catch (NullPointerException n) {
				Pool.daemon(OutOfMemoryException::gay).start();
			}
		});
		
	}
}
