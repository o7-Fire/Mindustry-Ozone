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

import Atom.File.SerializeData;
import Atom.Utility.Random;
import Atom.Utility.Utility;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SimpleLearning {
	
	static int useJacketAtCentigrade = 10;
	static File f = new File("SimpleLearning.mdl");
	static Model n = new Model(1, 1);
	
	public static void main(String[] args) throws Throwable {
		
		try {
			n = SerializeData.dataIn(f);
		}catch (Throwable gay) {
			trainModel(n);
		}
		Runtime.getRuntime().addShutdownHook(new Thread(SimpleLearning::saveModel));
		
		Utility.convertThreadToInputListener(">", s -> {
			if (s == null) {
				try {
					Thread.currentThread().interrupt();
				}catch (Throwable ignored) {}
				return;
			}
			if (s.equalsIgnoreCase("train")) {
				trainModel(n);
				return;
			}
			try {
				int i = Integer.parseInt(s);
				boolean b = n.get(i);
				while (b != useJacketAtCentigrade > i) {
					try {
						trainModel(n);
						b = n.get(i);
					}catch (Throwable e) {
						e.printStackTrace();
					}
				}
				System.out.println("[Model] use jacket:" + (b ? "yes" : "no"));
			}catch (Throwable t) {
				System.out.println(t.toString());
				t.printStackTrace();
			}
		});
		
		
	}
	
	public static void saveModel(Model m, File f) {
		try {
			SerializeData.dataOut(n, f);
			System.out.println("Saved to: " + f.getAbsolutePath());
		}catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public static void saveModel() {
		saveModel(n, f);
	}
	
	public static void trainModel(Model n) {
		System.out.println("Model not exists, training one");
		long i = 0;
		while (!test(n)) {
			i++;
			System.out.println("Iteration:" + i);
			n.update();
		}
		while (!test(n)) {
			i++;
			System.out.println("Iteration:" + i);
			n.update();
		}
		System.out.println("Training successful");
		System.out.println("Model: \n" + n.toString());
		saveModel(n, f);
	}
	
	public static boolean test(Model n) {
		
		for (int i = 0; i < 20; i++) {
			int r = Random.getInt(-2000, 2000);
			
			boolean b = n.get(r);
			if (b != useJacketAtCentigrade > r) return false;
		}
		return true;
	}
	
	public static class Model implements Serializable {
		ArrayList<ArrayList<Node>> nodes = new ArrayList<>();
		Node end = new Node(Random.getLong());
		
		public Model(int x, int y) {
			for (int i = 0; i < x; i++) {
				ArrayList<Node> node = new ArrayList<>();
				for (int j = 0; j < y; j++) {
					node.add(new Node(Random.getLong()));
				}
				nodes.add(node);
			}
		}
		
		public void update() {
			for (ArrayList<Node> n : nodes) {
				for (Node node : n)
					node.updateScrew(Random.getLong());
			}
			end.updateScrew(Random.getLong());
		}
		
		public boolean get(int data) {
			float datashit = data;
			for (List<Node> n : nodes) {
				for (Node node : n)
					datashit = node.get(datashit);
			}
			System.out.println("[Model] Input: " + data + ", Output: " + datashit);
			return datashit < 0.5f;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			int x = 0, y = 0;
			for (ArrayList<Node> n : nodes) {
				x++;
				for (Node node : n) {
					y++;
					sb.append("x:").append(x).append(", y:").append(y).append("  ").append(node.toString()).append("\n");
				}
			}
			return sb.toString();
		}
	}
	
	public static class Node implements Serializable {
		long s;
		transient Random random = new Random();
		
		public Node(long screw) {
			updateScrew(screw);
			
		}
		
		public void check() {
			if (random == null) {
				random = new Random();
				applyScrew();
			}
		}
		
		public void applyScrew() {
			updateScrew(s);
		}
		
		public void updateScrew(long screw) {
			if (random == null) random = new Random();
			s = screw;
			random.setSeed(screw);
		}
		
		public float get(float data) {
			check();
			if (random.nextBoolean()) return Math.min(random.nextFloat(), data * random.nextFloat());
			else return Math.max(random.nextFloat(), data * random.nextFloat());
		}
		
		@Override
		public String toString() {
			return super.toString() + ": " + s;
		}
	}
}
