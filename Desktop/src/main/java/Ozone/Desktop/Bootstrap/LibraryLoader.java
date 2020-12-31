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

import Ozone.Pre.Download;
import io.sentry.Sentry;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LibraryLoader extends URLClassLoader {
	public static File cache = new File("lib/");
	private ExecutorService es = Executors.newCachedThreadPool();
	
	static {
		cache.mkdirs();
		registerAsParallelCapable();
	}
	
	
	public LibraryLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}
	
	public LibraryLoader(URL[] urls) {
		super(urls);
	}
	
	public void defineClass(String name, InputStream is) throws IOException {
		byte[] h = is.readAllBytes();
		defineClass(name, h, 0, h.length);
	}
	
	@Override
	public void addURL(URL url) {
		if (url.getProtocol().startsWith("http") && url.getFile().endsWith(".jar")) {
			try {
				super.addURL(es.submit(() -> {
					if (url.getProtocol().startsWith("http") && url.getFile().endsWith(".jar")) try {
						return (cache(url));
					}catch (Throwable e) {
						Sentry.captureException(e);
					}
					return (url);
				}).get());
			}catch (InterruptedException | ExecutionException ignored) { }
		}else {
			super.addURL(url);
		}
	}
	
	public void addURL(File f) throws MalformedURLException {
		if (f.exists()) addURL(f.toURI().toURL());
	}
	
	public void addURL(List<URL> urlList) {
		ArrayList<Future<URL>> ar = new ArrayList<>();
		for (URL u : urlList)
			ar.add(es.submit(() -> {
				if (u.getProtocol().startsWith("http") && u.getFile().endsWith(".jar")) try {
					return (cache(u));
				}catch (Throwable e) {
					Sentry.captureException(e);
				}
				return (u);
			}));
		for (Future<URL> f : ar) {
			try {
				addURL(f.get());
			}catch (InterruptedException | ExecutionException ignored) {
			
			}
		}
	}
	
	private URL cache(URL url) {
		File temp = new File(cache, url.getFile());
		temp.getParentFile().mkdirs();
		if (!temp.exists()) {
			try {
				Main.Download.main(url, temp);
			}catch (Throwable t) {
				try {
					Download d = new Download(url, temp);
					d.print(s -> {
						s = "[LibraryLoader-" + temp.getName() + "]" + s;
						SharedBootstrap.setSplash(s);
						System.out.println(s);
					});
					d.run();
				}catch (Throwable et) {
					Sentry.captureException(et);
				}
				Sentry.captureException(t);
			}
		}
		if (temp.exists()) {
			try {
				url = temp.toURI().toURL();
			}catch (MalformedURLException e) {
				Sentry.captureException(e);
			}
		}
		return url;
	}
	
	@Nullable
	@Override
	public URL getResource(String name) {
		URL u = super.getResource(name);
		if (u == null) u = ClassLoader.getSystemResource(name);
		return u;
	}
	
	@Override
	public InputStream getResourceAsStream(String name) {
		try {
			URL u = getResource(name);
			if (u == null) throw new NullPointerException("bruh");
			return u.openStream();
		}catch (Throwable ignored) {}
		return super.getResourceAsStream(name);
	}
	
	
	@Override
	public synchronized Class<?> loadClass(String name) throws ClassNotFoundException {
		
		//Note: don't mess with java
		if (!name.startsWith(this.getClass().getPackageName()))
			try { return super.loadClass(name); }catch (Throwable ignored) {}
		//Log.infoTag("Ozone-LibraryLoader", name);
		return ClassLoader.getSystemClassLoader().loadClass(name);
	}
}
