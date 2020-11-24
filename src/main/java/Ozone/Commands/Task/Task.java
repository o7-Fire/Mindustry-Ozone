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

package Ozone.Commands.Task;

import Ozone.Commands.Commands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class Task {
    protected ArrayList<Consumer<Object>> onTaskCompleted = new ArrayList<>();
    protected int tick = 1;
    protected int currentTick = 0;

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public void onTaskCompleted(Consumer<Object> v) {
        onTaskCompleted.add(v);
    }

    public void tellUser(String s) {
        Commands.tellUser(s);
    }

    public void taskCompleted() {
        for (Consumer<Object> s : onTaskCompleted)
            s.accept(new Object());//wtf ?
    }

    public void interrupt() {
        taskCompleted();
    }

    protected void setTick(int tick1) {
        tick = tick1;
    }

    protected boolean tick() {
        if (currentTick < tick) {
            currentTick++;
            return true;
        }else {
            currentTick = 0;
            return false;
        }
    }

    public boolean isCompleted() {
        return true;
    }

    public void update() {

    }
}
