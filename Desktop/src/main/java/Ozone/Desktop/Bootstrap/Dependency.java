/*
 * Copyright 2020 Itzbenz
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

package Ozone.Desktop.Bootstrap;

import Ozone.Desktop.Propertied;
import io.sentry.Sentry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Dependency implements Serializable {
	public static ArrayList<Dependency> dependencies = new ArrayList<>();
	public static ArrayList<String> url = new ArrayList<>();
	private static File cache = new File("lib/dependency.link");
	private static HashMap<String, String> downloadCache = new HashMap<>();
	private static ExecutorService executorService = Executors.newCachedThreadPool();
	
	static {
		cache.getParentFile().mkdirs();
		try {
			parseDependency();
			load();
		}catch (Throwable t) {
			t.printStackTrace();
			Sentry.captureException(t);
			throw new RuntimeException("Failed to parse dependencies", t);
		}
	}
	
	public final String groupId, artifactId, version;
	public final Type type;
	public String link;
	
	public Dependency(String groupId, String artifactId, String version, String type) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.type = Type.valueOf(type);
		executorService.submit(() -> { try { getDownload(); }catch (IOException ignored) { } });
	}
	
	public static void load() throws IOException {
		if (cache.exists()) downloadCache = Propertied.parse(new String(Files.readAllBytes(cache.toPath())));
	}
	
	public static void parseDependency() throws IOException {
		dependencies.clear();
		url.clear();
		String last = "";
		HashMap<String, String> hash = new HashMap<>();
		for (String s : new String(Propertied.getResource("dependencies").readAllBytes()).split("\n")) {
			if (s.isEmpty()) continue;
			if (last.equals("type")) {
				dependencies.add(new Dependency(hash.get("groupId"), hash.get("artifactId"), hash.get("version"), hash.get("type")));
				hash = new HashMap<>();
			}
			
			String[] sys = s.split("=");
			last = sys[0];
			if (last.equals("url")) {
				url.add(sys[1]);
			}else hash.put(sys[0], sys[1]);
			
		}
	}
	
	public static void save() throws IOException {
		Files.write(cache.toPath(), Propertied.reverseParse(downloadCache).getBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
	}
	
	@Override
	public String toString() {
		return groupId + "." + artifactId + "." + version;
	}
	
	public synchronized String getDownload() throws IOException {
		if (link != null) return link;
		if (downloadCache.containsKey(toString())) {
			link = downloadCache.get(toString());
			return link;
		}
		StringBuilder sb = new StringBuilder();
		for (String u : url) {
			try {
				String r = getDownload(u);
				sb.append(r).append("\n");
				new URL(r).openStream();
				assignDownload(r);
				return r;
			}catch (FileNotFoundException ignored) { }
		}
		throw new FileNotFoundException(sb.toString());
	}
	
	private synchronized void assignDownload(String r) {
		link = r;
		downloadCache.put(toString(), link);
	}
	
	
	public String getDownload(String url) {
		return String.format("%s/%s/%s/%s/%s-%s.jar", url, groupId.replace('.', '/'), artifactId, version, artifactId, version);
		
	}
	
	public enum Type {provided, runtime, compile}
}
