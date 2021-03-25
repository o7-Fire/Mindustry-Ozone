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

/* o7 Inc 2021 Copyright
  Licensed under the o7 Inc License, Version 1.0.1, ("the License");
  You may use this file but only with the License. You may obtain a
  copy of the License at
  
  https://github.com/o7-Fire/Mindustry-Ozone/Licenses
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the license for the specific language governing permissions and
  limitations under the License.
*/

package Ozone.Internal;

import Atom.Reflect.Reflect;
import Atom.Utility.Encoder;
import Ozone.Gen.Callable;
import Shared.SharedBoot;
import arc.net.Client;
import arc.struct.Seq;
import arc.util.OS;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.RemoteReadClient;
import mindustry.gen.RemoteReadServer;
import mindustry.net.ArcNetProvider;
import mindustry.net.Net;
import mindustry.net.Packets;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.serializers.JsonSerializer;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;

public class InformationCenter {
	public static String currentServerIP = "";
	public static int currentServerPort = 0;
	public static Callable callable;
	private static ArrayList<String> clientSendPackets = new ArrayList<>();
	private static HashSet<Integer> commonPacketClientReceive = new HashSet<>(), commonPacketClientSend = new HashSet<>();
	
	static {
		commonPacketClientReceive.add(59);//state snapshot
		commonPacketClientReceive.add(31);//ping response
		commonPacketClientReceive.add(18);//entity snapshot
		commonPacketClientSend.add(30);//ping response
		commonPacketClientSend.add(72);//client snapshot
		Seq<Method> a = new Seq<>(Call.class.getDeclaredMethods());
		a.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
		for (Method m : a) {
			if (!clientSendPackets.contains(m.getName())) clientSendPackets.add(m.getName());
		}
		
	}
	
	protected static HashSet<String> moduleRegistered = new HashSet<>(), moduleLoaded = new HashSet<>(), modulePost = new HashSet<>();
	
	public static Callable getCallableMain() {
		if (callable == null) callable = new Callable(Vars.net);
		
		return callable;
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
	
	public static String getPacketNameServerSend(int id) {
		return getPacketNameClientReceive(id);//drug
	}
	
	public static String getPacketNameServerReceive(int id) {
		return getPacketNameClientSend(id);//drug
	}
	
	public static String getPacketNameServerSend(Packets.InvokePacket packet) {
		return getPacketNameClientReceive(packet.type);//drug
	}
	
	public static String getPacketNameServerReceive(Packets.InvokePacket packet) {
		return getPacketNameClientSend(packet.type);//drug
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
	
	public static boolean isCommonPacketClientReceive(Packets.InvokePacket packet) {
		return isCommonPacketClientReceive(packet.type);
	}
	
	public static String getPacketNameClientReceive(Packets.InvokePacket packet) {
		return getPacketNameClientReceive(packet.type);
	}
	
	public static boolean isCommonPacketClientSend(Packets.InvokePacket packet) {
		return isCommonPacketClientSend(packet.type);
	}
	
	public static boolean isCommonPacketServerReceive(int type) {
		return isCommonPacketClientSend(type);
	}
	
	public static boolean isCommonPacketServerSend(int type) {
		return isCommonPacketClientReceive(type);
	}
	
	public static boolean isCommonPacketServerReceive(Packets.InvokePacket packet) {
		return isCommonPacketClientSend(packet.type);
	}
	
	public static boolean isCommonPacketServerSend(Packets.InvokePacket packet) {
		return isCommonPacketClientReceive(packet.type);
	}
	
	public static String getPacketNameClientSend(Packets.InvokePacket packet) {
		return getPacketNameClientSend(packet.type);
	}
	
	public static Reflections reflections = null;
	protected static String jsonReflect;
	private static boolean youtried, youtried2;
	
	public static String reflectJson() throws IOException {
		if (jsonReflect != null) return jsonReflect;
		if (youtried2) throw new NullPointerException("No json: " + reflectJsonFile());
		youtried2 = true;
		if (OS.isAndroid) {
			jsonReflect = Encoder.readString(InformationCenter.class.getClassLoader().getResource(InformationCenter.reflectJsonFile()).openStream());
		}
		if (jsonReflect == null) if (Atom.Manifest.internalRepo.resourceExists(reflectJsonFile())) {
			InputStream is = null;
			is = Atom.Manifest.internalRepo.getResourceAsStream(reflectJsonFile());
			jsonReflect = Encoder.readString(is);
		}
		if (jsonReflect == null) throw new NullPointerException("No json: " + reflectJsonFile());
		return jsonReflect;
	}
	
	public static String reflectJsonFile() {
		return "reflections/" + SharedBoot.type + "-reflections.json";
	}
	
	public static Reflections getReflection() {
		if (!youtried) {
			youtried = true;
			String reflect = reflectJsonFile();
			try {
				if (Atom.Manifest.internalRepo.resourceExists(reflect)) {
					InputStream is;
					is = Atom.Manifest.internalRepo.getResourceAsStream(reflect);
					ConfigurationBuilder config = ConfigurationBuilder.build().setSerializer(new JsonSerializer());
					config.setClassLoaders(new ClassLoader[]{InformationCenter.class.getClassLoader()});
					config.setScanners(new SubTypesScanner());
					Reflections reflections = new Reflections(config);
					reflections.collect(is);
					
				}else throw new FileNotFoundException();
			}catch (Throwable ignored) {}
		}
		return reflections;
	}
}
