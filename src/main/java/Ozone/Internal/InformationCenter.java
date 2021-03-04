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

package Ozone.Internal;

import Atom.Reflect.Reflect;
import arc.net.Client;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.RemoteReadClient;
import mindustry.gen.RemoteReadServer;
import mindustry.net.ArcNetProvider;
import mindustry.net.Net;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;

public class InformationCenter {
	protected static ArrayList<String> moduleRegistered = new ArrayList<>(), moduleLoaded = new ArrayList<>(), modulePost = new ArrayList<>();
	private static ArrayList<String> clientSendPackets = new ArrayList<>();
	private static HashSet<Integer> commonPacketClientReceive = new HashSet<>(), commonPacketClientSend = new HashSet<>();
	public static String currentServerIP = "";
	public static int currentServerPort = 0;
	
	static {
		commonPacketClientReceive.add(59);//state snapshot
		commonPacketClientReceive.add(31);//ping response
		commonPacketClientReceive.add(18);//entity snapshot
		commonPacketClientSend.add(8);//client snapshot
		commonPacketClientSend.add(30);//ping response
		Seq<Method> a = new Seq<>(Call.class.getDeclaredMethods());
		a.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
		for (Method m : a) {
			if (!clientSendPackets.contains(m.getName())) clientSendPackets.add(m.getName());
		}
		
	}
	
	public static File getCurrentJar() {
		try {
			File f = Reflect.getCurrentJar(InformationCenter.class);
			if (f == null) throw new NullPointerException();
			return f;
		}catch (Throwable ignored) {
			return new File(InformationCenter.class.getClassLoader().getResource("mod.hjson").getFile());
			
		}
	}
	
	public static ArrayList<String> getLoadedModule() {return new ArrayList<>(moduleLoaded);}
	
	public static ArrayList<String> getRegisteredModule() {
		return new ArrayList<>(moduleRegistered);
	}
	
	public static String getPacketNameClientSend(int id) {
		try {
			RemoteReadServer.readPacket(null, id, Vars.player);
		}catch (Throwable t) {
			return t.getMessage().replace("Failed to read remote method", "");
		}
		return "Unknown Packet";
	}
	
	public static int getCurrentServerPort() {
		try {
			return getCurrentClientNet().getRemoteAddressTCP().getPort();
		}catch (Throwable t) {
			return currentServerPort;
		}
	}
	
	public static String getCurrentServerIP() {
		try {
			return getCurrentClientNet().getRemoteAddressTCP().getAddress().getHostAddress();
		}catch (Throwable t) {
			return currentServerIP;
		}
		
	}
	
	public static @Nullable Client getCurrentClientNet() {
		try {
			Net.NetProvider n = Reflect.getField(Vars.net.getClass(), "provider", Vars.net);
			if (!(n instanceof ArcNetProvider)) return null;
			ArcNetProvider arc = (ArcNetProvider) n;
			return Reflect.getField(arc.getClass(), "client", arc);
		}catch (Throwable ignored) { }
		return null;
	}
	
	public static String getPacketNameClientReceive(int id) {
		try {
			RemoteReadClient.readPacket(null, id);
		}catch (Throwable t) {
			return t.getMessage().replace("Failed to read remote method", "");
		}
		return "Unknown Packet";
	}
	
	public static String getPacketName(int id) {
		try {
			RemoteReadClient.readPacket(null, id);
		}catch (Throwable t) {
			return t.getMessage().replace("Failed to read remote method", "");
		}
		try {
			return clientSendPackets.get(id);
		}catch (Throwable t) {
			return "Unknown Packet";
		}
	}
	
	public static boolean isCommonPacketClientSend(int b) {
		return commonPacketClientSend.contains(b);
	}
	
	public static boolean isCommonPacketClientReceive(int b) {
		return commonPacketClientReceive.contains(b);
	}
	
	public static boolean isCommonPacket(int id) {
		return commonPacketClientSend.contains(id);
	}
	
	public static ArrayList<String> getPacketsName() {
		return new ArrayList<>(clientSendPackets);//what r u doing debil
	}
}
