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
import arc.func.Cons;
import arc.func.Prov;
import arc.net.Client;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.net.ArcNetProvider;
import mindustry.net.Host;
import mindustry.net.NetConnection;
import mindustry.net.NetworkIO;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

import static mindustry.Vars.*;

public class OzoneFrameworkNetProvider implements OzoneNet.NetProvider {
	public static Prov<DatagramPacket> staticPacketSupplier;
	@Nullable
	public static Client staticClient; //don't use it for actual purpose
	@Nullable
	public static ArcNetProvider fallbackProvider;
	public static int maxBuffer = 8192;
	
	static {
		staticPacketSupplier = () -> new DatagramPacket(new byte[512], 512);
		try {
			staticClient = new Client(8192, 8192, new ArcNetProvider.PacketSerializer());
			staticClient.setDiscoveryPacket(staticPacketSupplier);
		}catch (Throwable t) {
			staticClient = null;
		}
		try {
			fallbackProvider = new ArcNetProvider();
		}catch (Throwable t) {
			fallbackProvider = null;
		}
	}
	
	public static HashSet<Host> discoverServers() {
		HashSet<Host> foundAddresses = new HashSet<>();
		long time = Time.millis();
		AtomicBoolean finished = new AtomicBoolean(false);
		if (staticClient == null) {
			return foundAddresses;
		}
		staticClient.discoverHosts(port, multicastGroup, multicastPort, 3000, packet -> {
			try {
				ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
				Host host = NetworkIO.readServerData((int) Time.timeSinceMillis(time), packet.getAddress().getHostAddress(), buffer);
				foundAddresses.add(host);
			}catch (Exception e) {
				//don't crash when there's an error pinging a a server or parsing data
				e.printStackTrace();
			}
			
		}, () -> finished.set(true));
		while (!finished.get()) {
			try { Thread.sleep(10); }catch (InterruptedException ignored) { }
		}
		return foundAddresses;
	}
	
	public static boolean isLocal(InetAddress addr) {
		if (addr.isAnyLocalAddress() || addr.isLoopbackAddress()) return true;
		
		try {
			return NetworkInterface.getByInetAddress(addr) != null;
		}catch (Exception e) {
			return false;
		}
	}
	
	public static Host pingHost(String address, int port) throws IOException {
		DatagramSocket socket = new DatagramSocket();
		long time = Time.millis();
		socket.send(new DatagramPacket(new byte[]{-2, 1}, 2, InetAddress.getByName(address), port));
		socket.setSoTimeout(2000);
		
		DatagramPacket packet = staticPacketSupplier.get();
		socket.receive(packet);
		
		ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
		
		return NetworkIO.readServerData((int) Time.timeSinceMillis(time), packet.getAddress().getHostAddress(), buffer);
	}
	
	@Override
	public void connectClient(String ip, int port, Runnable success) throws IOException {
	
	}
	
	@Override
	public void sendClient(Object object, OzoneNet.SendMode mode) {
	
	}
	
	@Override
	public void disconnectClient() {
	
	}
	
	@Override
	public void discoverServers(Cons<Host> callback, Runnable done) {
		try {
			fallbackProvider.discoverServers(callback, done);
		}catch (Throwable ignored) {
			Pool.daemon(() -> {
				HashSet<Host> h = discoverServers();
				for (Host he : h)
					callback.get(he);
				done.run();
			}).start();
		}
		
	}
	
	//this is can be static but whatever
	@Override
	public void pingHost(String address, int port, Cons<Host> valid, Cons<Exception> failed) {
		Pool.daemon(() -> {
			try {
				valid.get(pingHost(address, port));
			}catch (Throwable e) {
				if (e instanceof Exception) failed.get((Exception) e);
				if (fallbackProvider != null && !(e instanceof IOException))
					fallbackProvider.pingHost(address, port, valid, failed);//wtf ?
			}
		}).start();
	}
	
	@Override
	public void hostServer(int port) throws IOException {
	
	}
	
	@Override
	public Iterable<? extends NetConnection> getConnections() {
		return new Seq<>();
	}
	
	@Override
	public void closeServer() {
	
	}
	
	@Override
	public void dispose() {
	
	}
}
