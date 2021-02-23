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
package Ozone.Net;

import arc.util.io.ReusableByteOutStream;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.net.Net;

import java.io.DataOutputStream;

public class Callable {
	
	protected final ReusableByteOutStream OUT = new ReusableByteOutStream(8192);
	
	protected final Writes WRITE = new Writes(new DataOutputStream(OUT));
	
	private Net net, original;
	
	public Callable(Net net) {
		this.net = net;
	}
	
	private void sendClient(Object packet, Net.SendMode sendMode) {
		net.send(packet, sendMode);
	}
	
	public void pre() {
		original = Vars.net;
		Vars.net = net;
	}
	
	public void post() {
		Vars.net = original;
	}
	
	public void base() {
		pre();
	}
}