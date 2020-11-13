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

import Main.OxygenMindustry;
import arc.Events;
import arc.func.Floatp;
import arc.scene.Group;
import arc.scene.style.Drawable;
import arc.util.Log;
import arc.util.Strings;
import io.sentry.Sentry;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.ui.dialogs.GameOverDialog;
import mindustry.ui.dialogs.JoinDialog;
import mindustry.ui.fragments.ChatFragment;
import mindustry.ui.fragments.Fragment;
import mindustry.ui.fragments.HudFragment;
import mindustry.ui.fragments.LoadingFragment;

import java.rmi.RemoteException;

import static mindustry.Vars.player;

public class StubUI {
    public static RuntimeException stub = new RuntimeException("Stub!");

    public static class ChatFrag extends ChatFragment {
        public ChatFrag() {

        }

        @Override
        public Fragment container() {
            throw stub;
        }

        @Override
        public void clearMessages() {

        }

        @Override
        public void draw() {

        }

        @Override
        public void toggle() {

        }

        @Override
        public void hide() {
            super.hide();
        }

        @Override
        public void updateChat() {

        }

        @Override
        public void clearChatInput() {

        }

        @Override
        public boolean shown() {
            return true;
        }

        @Override
        public void addMessage(String message, String sender) {
            Log.infoTag(Strings.stripColors(sender), Strings.stripColors(sender));
        }
    }

    public static class HudFrag extends HudFragment {
        String s = "";

        public HudFrag() {

        }

        @Override
        public void build(Group parent) {

        }

        @Override
        public synchronized void setHudText(String text) {
            s = text;
        }

        @Override
        public void toggleHudText(boolean shown) {
            if (shown)
                Log.infoTag("HUD", s);
        }

        @Override
        public void showToast(String text) {
            Log.infoTag("HUD", text);
        }

        @Override
        public void showToast(Drawable icon, String text) {
            Log.infoTag("HUD", text);
        }

        @Override
        public void showUnlock(UnlockableContent content) {
            Log.infoTag("Unlocked", content.name);
        }

        @Override
        public void showLaunchDirect() {
            Log.infoTag("HUD", "Launch Direct");
        }

        @Override
        public void showLaunch() {
            Log.infoTag("HUD", "Launch");
        }

        @Override
        public void showLand() {
            Log.infoTag("HUD", "Land");
        }
    }

    public static class Restart extends GameOverDialog {
        @Override
        public void show(Team winner) {
            Log.info("Team: " + winner.name + " win");
            if (winner == player.team()) {
                Events.fire(new EventType.WinEvent());
            } else {
                Events.fire(new EventType.LoseEvent());
            }
        }
    }

    public static class Join extends JoinDialog {

        @Override
        public void connect(String ip, int port) {
            try {
                OxygenMindustry.botInterface.connect(ip, port);
            } catch (RemoteException e) {
                Sentry.captureException(e);
                e.printStackTrace();
            }
        }

        @Override
        public void hide() {

        }
    }

    public static class LoadFrag extends LoadingFragment {
        private boolean show;
        private String string = "";
        private float progress;
        private String last = "";

        public LoadFrag() {
            Events.run(EventType.Trigger.update, () -> {
                if (!show) return;
                deleteLast();
                printProgress();
            });
        }

        @Override
        public void build(Group parent) {

        }

        @Override
        public synchronized void setProgress(Floatp progress) {
            this.progress = progress.get();
        }

        @Override
        public void setButton(Runnable listener) {

        }

        @Override
        public synchronized void setText(String text) {
            string = text;
        }

        @Override
        public void show() {
            show("Loading");
        }

        @Override
        public synchronized void show(String text) {
            if (show) throw new IllegalStateException("Im busy");
            show = true;
            setText(text);
        }

        @Override
        public synchronized void hide() {
            show = false;
            setText("");
            progress = 0f;
        }

        private void deleteLast() {
            for (int i = 0; i < last.length(); i++) {
                System.out.print('\b');
            }
            last = "";
        }

        private void printProgress() {
            StringBuilder sb = new StringBuilder();
            sb.append("[").append((int) (progress * 100f)).append("%]: ").append(string);
            System.out.print(sb.toString());
            last = sb.toString();
        }
    }
}
