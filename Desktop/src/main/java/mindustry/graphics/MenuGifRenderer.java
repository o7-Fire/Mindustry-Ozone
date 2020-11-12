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
import Premain.Version;
import arc.Core;
import arc.graphics.g2d.Animation;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.Disposable;
import arc.util.Log;
import arc.util.Time;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MenuGifRenderer implements Disposable {
    Animation<TextureRegion> animation;
    float elapsed;
    int iteration = 0;
    Seq<String> url = Seq.with(//insert your shitty gif here
            "https://cdn.discordapp.com/attachments/713346278003572777/776046031120891934/hmm.gif",
            "https://cdn.discordapp.com/attachments/724060628763017296/776078396936683540/tenor.gif",
            "https://media.discordapp.net/attachments/671340986223296574/774550872544903178/image0-1-1.gif"
    );
    int length;

    public MenuGifRenderer() throws IOException {
        random();
        Object[] e = animation.getKeyFrames();
        length = e.length;
        Log.infoTag("MenuRenderer", "Loaded " + length + " frames");
    }

    private URL cache(URL u) throws IOException {
        File target = new File(Version.cache, u.getFile().replaceAll("/", "."));
        if (target.exists()) return target.toURI().toURL();
        HTPS.downloadSync(u.toExternalForm(), target);
        return target.toURI().toURL();
    }

    private void random() throws IOException {
        URL u = cache(new URL(url.random()));
        animation = GifDecoder.loadGIFAnimation(Animation.PlayMode.loop, u.openStream());
    }

    public void render() {
        elapsed += Time.delta;
        if (iteration > length) iteration = 0;
        int h = Core.graphics.getHeight();
        int w = Core.graphics.getWidth();
        Draw.rect(animation.getKeyFrame(iteration), w / 2, h / 2, w, h);
        iteration++;
        //Draw.rect(animation.getKeyFrame(elapsed), w/2, h/2, w, h);
    }

    @Override
    public void dispose() {

    }
}
