package Ozone.Commands.Task;

import Ozone.Commands.Commands;

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
            s.accept(new Object());
    }

    protected void setTick(int tick1) {
        tick = tick1;
    }

    protected boolean tick() {
        if (currentTick < tick) {
            currentTick++;
            return true;
        } else {
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
