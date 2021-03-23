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
	private static StaticNet staticNet = new StaticNet();
	private Net.NetProvider provider;
	
	public Callable(Net net) {
		this.net = net;
	}
	
	public Callable(Net.NetProvider provider) {
		this.provider = provider;
	}
	
	public void pre() {
		original = Vars.net;
		staticNet.setNet(net).setProvider(provider);
		Vars.net = staticNet;
	}
	
	public void post() {
		Vars.net = original;
		original = null;
	}
	
	public void base() {
		pre();
	}
	
	private static class StaticNet extends LoggableNet {
		
		NetProvider provider;
		
		public StaticNet() {
			super(null);
		}
		
		public StaticNet setProvider(NetProvider provider) {
			this.provider = provider;
			return this;
		}
		
		public StaticNet setNet(Net net) {
			this.net = net;
			return this;
		}
		
		@Override
		public void send(Object object, SendMode mode) {
			if (provider != null) {
				provider.sendClient(object, mode);
				provider = null;
			}
			if (net != null) {
				net.send(object, mode);
				net = null;
			}
		}
	}
}