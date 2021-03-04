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

package Ozone.Watcher;

import Ozone.Internal.InformationCenter;
import Ozone.Internal.Interface;
import arc.func.Cons;
import arc.func.Cons2;
import arc.util.Log;
import mindustry.Vars;
import mindustry.net.*;

import java.io.IOException;

public class NetWatcher extends Net {
	Net net;
	
	public NetWatcher(NetProvider provider) {
		super(provider);
		Log.infoTag("Net-Watcher", provider.getClass().getName());
		Interface.showInfo("Net Watcher: " + provider.getClass().getName());
	}
	
	public NetWatcher(Net net) {
		this(Vars.platform.getNet());
		this.net = net;
	}
	
	@Override
	public void send(Object object, SendMode mode) {
		if (object instanceof Packets.InvokePacket)
			Log.infoTag("Packet-" + mode.name() + "-Send", "[" + ((Packets.InvokePacket) object).type + "] " + InformationCenter.getPacketNameClientSend(((Packets.InvokePacket) object).type));
		else if (object instanceof Packet) Log.infoTag("Packet-" + mode.name() + "-Send", object.getClass().getName());
		
		net.send(object, mode);
	}
	
	@Override
	public void handleClientReceived(Object object) {
		if (object instanceof Packets.InvokePacket)
			Log.infoTag("Packet-Receive", "[" + ((Packets.InvokePacket) object).type + "] " + InformationCenter.getPacketNameClientReceive(((Packets.InvokePacket) object).type));
		else if (object instanceof Packet) Log.infoTag("Packet-Receive", object.getClass().getName());
		net.handleClientReceived(object);
	}
	
	@Override
	public void handleException(Throwable e) {
		net.handleException(e);
	}
	
	@Override
	public void showError(Throwable e) {
		net.showError(e);
	}
	
	@Override
	public void setClientLoaded(boolean loaded) {
		net.setClientLoaded(loaded);
	}
	
	@Override
	public void setClientConnected() {
		net.setClientConnected();
	}
	
	@Override
	public void connect(String ip, int port, Runnable success) {
		net.connect(ip, port, success);
	}
	
	@Override
	public void host(int port) throws IOException {
		net.host(port);
	}
	
	@Override
	public void closeServer() {
		net.closeServer();
	}
	
	@Override
	public void reset() {
		net.reset();
	}
	
	@Override
	public void disconnect() {
		net.disconnect();
	}
	
	@Override
	public byte[] compressSnapshot(byte[] input) {
		return net.compressSnapshot(input);
	}
	
	@Override
	public byte[] decompressSnapshot(byte[] input, int size) {
		return net.decompressSnapshot(input, size);
	}
	
	@Override
	public void discoverServers(Cons<Host> cons, Runnable done) {
		net.discoverServers(cons, done);
	}
	
	@Override
	public Iterable<NetConnection> getConnections() {
		return net.getConnections();
	}
	
	@Override
	public void sendExcept(NetConnection except, Object object, SendMode mode) {
		net.sendExcept(except, object, mode);
	}
	
	@Override
	public Streamable.StreamBuilder getCurrentStream() {
		return net.getCurrentStream();
	}
	
	@Override
	public <T> void handleClient(Class<T> type, Cons<T> listener) {
		net.handleClient(type, listener);
	}
	
	@Override
	public <T> void handleServer(Class<T> type, Cons2<NetConnection, T> listener) {
		net.handleServer(type, listener);
	}
	
	@Override
	public void handleServerReceived(NetConnection connection, Object object) {
		net.handleServerReceived(connection, object);
	}
	
	@Override
	public void pingHost(String address, int port, Cons<Host> valid, Cons<Exception> failed) {
		net.pingHost(address, port, valid, failed);
	}
	
	@Override
	public boolean active() {
		return net.active();
	}
	
	@Override
	public boolean server() {
		return net.server();
	}
	
	@Override
	public boolean client() {
		return net.client();
	}
	
	@Override
	public void dispose() {
		net.dispose();
	}
}
