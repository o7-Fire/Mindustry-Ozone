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

package Bot.Interface.Shared;

import Ozone.Commands.Task.Task;
import arc.struct.Seq;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.function.Consumer;

public interface BotInterface extends Remote {
    default void addTask(Task t) throws RemoteException {

    }
    default void addTask(Task t, Consumer<Object> onEnd) throws RemoteException {

    }
    default ArrayList<Task> getTask()throws RemoteException{
        return null;
    }

    default void clearTask() throws RemoteException {

    }

    default void kill() throws RemoteException {

    }

    default void connect(String ip, int port) throws RemoteException {

    }

    default void sendChat(String s) throws RemoteException {

    }

    default String getType() throws RemoteException {
        return null;
    }

    default boolean alive() throws RemoteException {
        return false;
    }

    default int getID() throws RemoteException {
        return 0;
    }

}
