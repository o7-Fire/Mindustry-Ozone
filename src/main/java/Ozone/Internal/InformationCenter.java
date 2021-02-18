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
import arc.struct.Seq;
import mindustry.gen.Call;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class InformationCenter {
	protected static ArrayList<String> moduleRegistered = new ArrayList<>(), moduleLoaded = new ArrayList<>(), modulePost = new ArrayList<>();
	private static ArrayList<String> packetName = new ArrayList<>();
	private static ArrayList<Integer> commonPacket = new ArrayList<>(Arrays.asList(17, 58, 7));//spammy af
	
	static {
		
		Seq<Method> a = new Seq<>(Call.class.getDeclaredMethods());
		a.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
		for (Method m : a) {
			if (!packetName.contains(m.getName())) packetName.add(m.getName());
		}
		
	}
	
	public static File getCurrentJar() {
		try {
			File f = Reflect.getCurrentJar();
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
	
	public static String getPacketName(int id) {
		try {
			return packetName.get(id);
		}catch (Throwable t){
			return "Unknown Packet";
		}
	}
	
	public static boolean isCommonPacket(int id) {
		return commonPacket.contains(id);
	}
	
	public static ArrayList<String> getPacketsName() {
		return new ArrayList<>(packetName);
	}
}
