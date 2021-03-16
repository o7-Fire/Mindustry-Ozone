import Atom.Reflect.Reflect;
import info.debatty.java.stringsimilarity.interfaces.StringDistance;
import info.debatty.java.stringsimilarity.interfaces.StringSimilarity;

import java.util.*;

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

public class StringHash {
	static HashMap<String, HashSet<String>> dataSet = new HashMap<>();
	static HashSet<Class<? extends StringDistance>> distance = new HashSet<>();
	static HashSet<Class<? extends StringSimilarity>> similarity = new HashSet<>();
	static double distanceBest = -100;
	static Class bestDistance = null;
	
	static {
		put("Nexity", "nexity", "ytixen", "[red]Nexity", "NexIty");
		put("Volas", "VolasYouKnow", "Vol4sY0uKn0w", "[red]Volas", "VolAs", "Bolas");
		put("Itzbenz", "Itz", "Itzben", "Itzbenzsich");
		put("Codex", "CODEX", "codex", "ControlString");
		put("IGGGAMES", "iggGames", "IGGGames");
		
		
	}
	
	public static void put(String s, String... arg) {
		ArrayList<String> ar = new ArrayList<>(Arrays.asList(arg));
		ar.add(s.replace('i', '1').replace('z', '2').replace('e', '3').replace('a', '4').replace('s', '5').replace('t', '7').replace('g', '9').replace('o', '0'));
		ar.add(s.toLowerCase());
		ar.add("Control-String");
		ar.add("ControlString");
		dataSet.put(s, new HashSet<>(ar));
	}
	
	public static void main(String[] args) {
		
		for (Class<? extends StringSimilarity> clazz : Reflect.getExtendedClass("info.debatty.java", StringSimilarity.class)) {
			try {
				StringSimilarity provider = clazz.getDeclaredConstructor().newInstance();
				System.out.println("StringSimilarity-" + provider.getClass().getTypeName());
				double min = provider.similarity("Undistance", "Distance"), max = provider.similarity("Undistance", "Distance"), controlMin = provider.similarity("Control", "Distance"), controlMax = provider.similarity("Control", "Distance");
				for (Map.Entry<String, HashSet<String>> s : dataSet.entrySet()) {
					for (String ss : s.getValue()) {
						if (!ss.startsWith("Control")) {
							min = Math.min(min, provider.similarity(s.getKey(), ss));
							max = Math.max(max, provider.similarity(s.getKey(), ss));
						}else {
							controlMin = Math.min(controlMin, provider.similarity(s.getKey(), ss));
							controlMax = Math.max(controlMax, provider.similarity(s.getKey(), ss));
						}
						System.out.println();
						System.out.println("  " + s.getKey() + " : " + ss);
						System.out.println("    " + provider.getClass().getSimpleName() + "-StringSimilarity" + ": " + provider.similarity(s.getKey(), ss));
					}
				}
				System.out.println();
				System.out.println("Control: " + controlMin + "-" + controlMax);
				System.out.println("minMax: " + min + "-" + max);
				if (controlMin > min + Math.max(controlMax, max)) {
					similarity.add(clazz);
					System.out.println("Passed");
				}
				System.out.println();
			}catch (Throwable ignored) {}
		}
		
		for (Class<? extends StringDistance> clazz : Reflect.getExtendedClass("info.debatty.java", StringDistance.class)) {
			distance(clazz);
		}
		System.out.println("Passed Similarity: " + similarity.toString());
		System.out.println();
		System.out.println("Passed Distance: " + distance.toString());
		for (Class<? extends StringDistance> clazz : distance) {
			distance(clazz);
		}
		;
		System.out.println("Best Distance: " + bestDistance + " = " + distanceBest);
	}
	
	public static void distance(Class<? extends StringDistance> clazz) {
		try {
			
			StringDistance provider = clazz.getDeclaredConstructor().newInstance();
			System.out.println("StringDistance-" + provider.getClass().getTypeName());
			double min = provider.distance("Undistance", "Distance"), max = provider.distance("Undistance", "Distance"), controlMin = provider.distance("Control", "Distance"), controlMax = provider.distance("Control", "Distance");
			for (Map.Entry<String, HashSet<String>> s : dataSet.entrySet()) {
				for (String ss : s.getValue()) {
					if (!ss.startsWith("Control")) {
						min = Math.min(min, provider.distance(s.getKey(), ss));
						max = Math.max(max, provider.distance(s.getKey(), ss));
					}else {
						controlMin = Math.min(controlMin, provider.distance(s.getKey(), ss));
						controlMax = Math.max(controlMax, provider.distance(s.getKey(), ss));
					}
					System.out.println();
					System.out.println("  " + s.getKey() + " : " + ss);
					System.out.println("    " + provider.getClass().getSimpleName() + "-StringDistance" + ": " + provider.distance(s.getKey(), ss));
				}
			}
			System.out.println();
			System.out.println("Control: " + controlMin + "-" + controlMax);
			System.out.println("minMax: " + min + "-" + max);
			if ((controlMax > max + Math.min(controlMin, min))) {
				distance.add(clazz);
				System.out.println("Passed");
			}
			double before = distanceBest;
			distanceBest = Math.max(distanceBest, controlMin - max);
			if (distanceBest > before) bestDistance = clazz;
			System.out.println();
			
		}catch (Throwable ignored) {}
	}
}
