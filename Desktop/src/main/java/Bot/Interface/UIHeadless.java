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

package Bot.Interface;

import arc.assets.AssetDescriptor;
import arc.func.Boolp;
import arc.func.Cons;
import arc.scene.style.TextureRegionDrawable;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Log;
import arc.util.Time;
import mindustry.core.UI;

//TODO redirect UI to main UI
//keep invoking Fonts.load, weird right
public class UIHeadless extends UI {
    public UIHeadless() {
        this(1);
    }

    public UIHeadless(Object distraction) {
        loadAsync();
        loadSync();
    }

    @Override
    public void loadAsync() {

    }

    @Override
    public void loadSync() {

    }

    @Override
    public Seq<AssetDescriptor> getDependencies() {
        return new Seq<>();
    }

    @Override
    public void update() {

    }

    @Override
    public void init() {
        loadfrag = new StubUI.LoadFrag();
        chatfrag = new StubUI.ChatFrag();
        join = new StubUI.Join();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public TextureRegionDrawable getIcon(String name) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public TextureRegionDrawable getIcon(String name, String def) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void loadAnd(Runnable call) {
        loadAnd("Loading..", call);
    }

    @Override
    public void loadAnd(String text, Runnable call) {
        StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        Log.infoTag(walker.getCallerClass().getTypeName(), text);
        Time.runTask(7f, () -> {
            call.run();
            loadfrag.hide();
        });
    }

    @Override
    public void showTextInput(String titleText, String dtext, int textLength, String def, boolean inumeric, Cons<String> confirmed) {
        throw new IllegalStateException("No Input");
    }

    @Override
    public void showTextInput(String title, String text, String def, Cons<String> confirmed) {
        showTextInput(title, text, 32, def, confirmed);
    }

    @Override
    public void showTextInput(String titleText, String text, int textLength, String def, Cons<String> confirmed) {
        showTextInput(titleText, text, textLength, def, false, confirmed);
    }

    @Override
    public void showInfoFade(String info) {
        Log.info(info);
    }

    @Override
    public void showInfoToast(String info, float duration) {
        Log.info(info);
    }

    @Override
    public void showInfoPopup(String info, float duration, int align, int top, int left, int bottom, int right) {
        Log.info(info);
    }

    @Override
    public void showLabel(String info, float duration, float worldx, float worldy) {
        Log.info(info);
    }

    @Override
    public void showInfo(String info) {
        showInfo(info, () -> {
        });
    }

    @Override
    public void showInfo(String info, Runnable listener) {
        Log.info(info);
    }

    @Override
    public void showStartupInfo(String info) {
        Log.info(info);
    }

    @Override
    public void showErrorMessage(String text) {
        Log.err(text);
    }

    @Override
    public void showException(Throwable t) {
        showException("", t);
    }

    @Override
    public void showException(String text, Throwable exc) {
        Log.err(text);
        Log.err(exc);
    }

    @Override
    public void showText(String titleText, String text) {
        showText(titleText, text, Align.center);
    }

    @Override
    public void showText(String titleText, String text, int align) {
        Log.infoTag(titleText, text);
    }

    @Override
    public void showInfoText(String titleText, String text) {
        Log.infoTag(titleText, text);
    }

    @Override
    public void showSmall(String titleText, String text) {
        Log.infoTag(titleText, text);
    }

    @Override
    public void showConfirm(String title, String text, Runnable confirmed) {
        showConfirm(title, text, null, confirmed);
    }

    @Override
    public void showConfirm(String title, String text, Boolp hide, Runnable confirmed) {
        throw new IllegalStateException("No Input");
    }

    @Override
    public void showCustomConfirm(String title, String text, String yes, String no, Runnable confirmed, Runnable denied) {
        throw new IllegalStateException("No Input");
    }

    @Override
    public void announce(String text) {
        announce(text, 3);
    }

    @Override
    public void announce(String text, float duration) {
        Log.info(text);
    }

    @Override
    public void showOkText(String title, String text, Runnable confirmed) {
        Log.infoTag(title, text);
    }
}
