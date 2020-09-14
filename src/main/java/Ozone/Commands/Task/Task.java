package Ozone.Commands.Task;

import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class Task {
    private ArrayList<Consumer<Void>> onTaskCompleted = new ArrayList<>();

    public String getName(){
       return this.getClass().getSimpleName();
    }

    public void onTaskCompleted(Consumer<Void> v){
        onTaskCompleted.add(v);
    }

    public void taskCompleted(){

    }

    public boolean isCompleted(){
        return onTaskCompleted == null;
    }

    public void update(){

    }
}
