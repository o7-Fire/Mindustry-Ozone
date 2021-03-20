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

import Atom.Utility.Pool;
import arc.net.Client;
import arc.util.pooling.Pools;
import mindustry.net.ArcNetProvider;
import mindustry.net.Net;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedSelectorException;

//basically with built-in client
public class OzoneClientFrameworkProvider extends OzoneFrameworkNetProvider {
	protected Client client;
	
	public OzoneClientFrameworkProvider() {
		client = new Client(maxBuffer, maxBuffer, new ArcNetProvider.PacketSerializer());
	}
	
	public Client getClient() {
		return client;
	}
	
	@Override
	public void connectClient(String ip, int port, Runnable success) throws IOException {
		Pool.daemon(() -> {
			try {
				//just in case
				client.stop();
				
				Pool.daemon(() -> {
					try {
						client.run();
					}catch (Exception e) {
						if (!(e instanceof ClosedSelectorException)) handleException(e);
					}
				}).start();
				
				client.connect(5000, ip, port, port);
				success.run();
			}catch (Exception e) {
				handleException(e);
			}
		}).start();
	}
	
	protected void handleException(Exception e) {
	
	}
	
	@Override
	public void sendClient(Object object, Net.SendMode mode) {
		try {
			if (mode == Net.SendMode.tcp) {
				client.sendTCP(object);
			}else {
				client.sendUDP(object);
			}
			//sending things can cause an under/overflow, catch it and disconnect instead of crashing
		}catch (BufferOverflowException | BufferUnderflowException e) {
			handleException(e);
		}
		
		Pools.free(object);
	}
	
	@Override
	public void disconnectClient() {
		client.close();
	}
	
	
	@Override
	public void dispose() {
		disconnectClient();
		closeServer();
		try {
			client.dispose();
		}catch (IOException ignored) {
		}
	}
}
