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

import Ozone.Gen.Callable;
import mindustry.net.Net;

//no
public class OzoneNet extends mindustry.net.Net {
	public DefaultNetListener netListener;
	protected OzoneNetProvider provider;
	
	public OzoneNet(OzoneNetProvider provider, DefaultNetListener netListener) {
		super(provider);
		this.provider = provider;
		provider.getClient().addListener(netListener.setNet(this));
		this.netListener = netListener;
	}
	
	@Override
	public void showError(Throwable e) {
	
	}
	
	public void confirmConnect() {
		setClientLoaded(true);
		netListener.post(() -> call().connectConfirm());
	}
	
	public OzoneNetProvider getProvider() {
		return provider;
	}
	
	public Callable call() {
		return provider.call;
	}
	
	@Override
	public void handleException(Throwable e) {
		provider.handleException(e);
	}
	
	@Override
	public void send(Object object, SendMode mode) {
		provider.sendClient(object, mode);
	}
	
	public static class DefaultNetListener extends Ozone.Net.ClichedNetListener {
		protected Net net;
		
		public DefaultNetListener setNet(Net net) {
			this.net = net;
			return this;
		}
		
		@Override
		public void handleClientReceived(Object o) {
			net.handleClientReceived(o);
		}
		
		@Override
		protected void post(Runnable r) {
			super.post(r);
		}
	}
}
