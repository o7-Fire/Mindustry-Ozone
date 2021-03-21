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

package Ozone.Commands.Class;

import com.beust.jcommander.Parameter;

public class PowerNode extends CommandsClass {
	@Parameter(names = "-all", description = "all nodes")
	public boolean all = false;
	@Parameter(names = "-disconnect", description = "disconnect nodes")
	public boolean disconnect = false;
	@Parameter(names = "-connect", description = "connect nodes")
	public boolean connect = false;
	
	@Override
	void run() throws Exception {
	
	}
}
