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

package Bot;

import Bot.Interface.Shared.ServerInterface;
import Bot.UI.BotControllerUI;
import Main.OxygenMindustry;
import Ozone.Event.DesktopEvent;
import arc.Events;

import java.rmi.registry.Registry;

public class Manifest {
    public static OxygenMindustry oxygen;
    public static Registry registry;
    public static ServerInterface serverInterface;
    public static BotControllerUI botUI;

    static {
        Events.on(DesktopEvent.InitUI.class, s -> {
            botUI = new BotControllerUI();
        });
    }

    public static Status getStatus() {
        try {
            serverInterface.alive();
            return Status.ONLINE;
        } catch (Throwable ignored) {
            return Status.OFFLINE;
        }
    }
}
