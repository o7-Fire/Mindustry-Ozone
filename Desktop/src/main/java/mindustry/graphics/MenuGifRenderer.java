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

package mindustry.graphics;

import Atom.Net.HTPS;
import Settings.Desktop;
import arc.Core;
import arc.files.Fi;
import arc.graphics.Texture;
import arc.graphics.g2d.Animation;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.Disposable;
import arc.util.Log;
import arc.util.Time;
import io.sentry.Sentry;
import mindustry.Vars;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class MenuGifRenderer implements Disposable {
	Animation<TextureRegion> animation;
	float elapsed;
	int iteration = 0;
	Seq<String> url = new Seq<>();
	Seq<String> allowed = Seq.with("gif", "jpg", "png", "jpeg");
	int length;
	Fi menu = new Fi(new File(Vars.dataDirectory.file(), "menu/"));
	String readme = "Place your own gif/jpg/png/jpeg here, and it will showed randomly on menu";
	
	public MenuGifRenderer() throws IOException, NoMenuResource {
		try {
			url.addAll(new String(new URL("https://raw.githubusercontent.com/o7-Fire/Mindustry-Ozone/master/Desktop/gif.txt").openStream().readAllBytes()).split("\n"));
		}catch (Throwable i) {
			Sentry.captureException(i);
		}
		menu.mkdirs();
		if (!menu.child("readme.txt").exists())
			Files.write(menu.child("readme.txt").file().toPath(), readme.getBytes());
		if (Desktop.disableDefaultGif) url.clear();
		for (Fi f : menu.findAll(f -> allowed.contains(f.extension())))
			url.add(f.file().toURI().toURL().toExternalForm());
		if (url.isEmpty()) throw new NoMenuResource("Gif list is empty");
		random();
		Object[] e = animation.getKeyFrames();
		length = e.length;
		Log.infoTag("MenuRenderer", "Loaded " + length + " frames");
	}
	
	private URL cache(URL u) throws IOException {
		if (u.getProtocol().startsWith("file")) return u;
		File target = new File(menu.file(), u.getFile().replaceAll("/", "."));
		if (target.exists()) return target.toURI().toURL();
		HTPS.downloadSync(u.toExternalForm(), target);
		return target.toURI().toURL();
	}
	
	private void random() throws IOException {
		URL u = cache(new URL(url.random()));
		if (new Fi(u.getFile()).extension().equals("gif"))
			animation = GifDecoder.loadGIFAnimation(Animation.PlayMode.loop, u.openStream());
		else
			animation = new Animation<>(1f, Seq.with(new TextureRegion(new Texture(new Fi(u.getFile())))), Animation.PlayMode.loop);
	}
	
	public void render() {
		elapsed += Time.delta;
		if (iteration > length) iteration = 0;
		int h = Core.graphics.getHeight();
		int w = Core.graphics.getWidth();
		Draw.rect(animation.getKeyFrame(iteration), (float) w / 2, (float) h / 2, w, h);
		iteration++;
		//Draw.rect(animation.getKeyFrame(elapsed), w/2, h/2, w, h);
	}
	
	@Override
	public void dispose() {
	
	}
	
	public static class NoMenuResource extends Throwable {
		public NoMenuResource(String message) {
			super(message);
		}
	}
}
