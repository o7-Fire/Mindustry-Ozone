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
import Ozone.Experimental.ConnectDiagram;
import arc.func.Cons;
import arc.func.Prov;
import arc.net.*;
import arc.struct.IntMap;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.pooling.Pools;
import mindustry.net.ArcNetProvider;
import mindustry.net.Net;
import mindustry.net.Packets;
import mindustry.net.Streamable;

import java.io.IOException;
import java.net.DatagramPacket;

public class ExpandableNetProvider extends OzoneFrameworkNetProvider {
	protected final IntMap<Streamable.StreamBuilder> streams = new IntMap<>();
	protected final Seq<Object> packetQueue = new Seq<>();
	protected final ObjectMap<Class<?>, Cons> clientListeners = new ObjectMap<>();
	
	protected Client client;
	protected ConnectDiagram cd;
	protected Runnable suc = null;
	protected boolean clientLoaded, connecting;
	
	protected ExpandableNet net;
	
	ExpandableNetProvider() {
		Prov<DatagramPacket> packetSupplier = () -> new DatagramPacket(new byte[512], 512);
		client = new Client(8192, 8192, new ArcNetProvider.PacketSerializer());
		client.setDiscoveryPacket(packetSupplier);
		client.addListener(new NetListener() {
			@Override
			public void connected(Connection connection) {
				Packets.Connect c = new Packets.Connect();
				c.addressTCP = connection.getRemoteAddressTCP().getAddress().getHostAddress();
				if (connection.getRemoteAddressTCP() != null)
					c.addressTCP = connection.getRemoteAddressTCP().toString();
				post(() -> handleClientReceived(c));
			}
			
			@Override
			public void disconnected(Connection connection, DcReason reason) {
				Packets.Disconnect c = new Packets.Disconnect();
				c.reason = reason.toString();
				post(() -> handleClientReceived(c));
			}
			
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof FrameworkMessage) return;
				post(() -> handleClientReceived(object));
			}
		});
	}
	
	protected void setNet(ExpandableNet net) {
		this.net = net;
	}
	
	protected void post(Runnable r) {
		net.post(r);
	}
	
	
	public void handleClientReceived(Object object) {
		net.handleClientReceived(object);
	}
	
	public void connectClient(String ip, int port, Runnable success, Cons<Exception> fail) {
		Pool.daemon(() -> {
			try {
				connectClient(ip, port, success);
			}catch (Exception e) {
				fail.get(e);
			}
		}).start();
	}
	
	@Override
	public void connectClient(String ip, int port, Runnable success) {
		suc = success;
		Pool.daemon(() -> {
			client.stop();
			Pool.daemon(() -> {
				try {
					client.run();
				}catch (Exception ignored) { }
			}).start();
			
			try {
				client.connect(5000, ip, port, port);
			}catch (IOException e) {
				net.log.err(e);
			}
		}).start();
		
		
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
		}catch (Exception t) {
			net.handleException(t);
			disconnectClient();
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
		try { client.dispose(); }catch (IOException ignored) { }
	}
	
	
}
