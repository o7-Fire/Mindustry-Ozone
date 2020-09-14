package Ozone.Commands;

import Ozone.Commands.Task.Move;
import Ozone.Commands.Task.Task;
import arc.Events;
import arc.struct.Queue;
import mindustry.game.EventType;

import java.util.function.Consumer;

public class PlayerInterface {
    private static volatile boolean init = false;
    private static final Queue<Task> taskQueue = new Queue<>();

    public static void init(){
        if(init)return;
        init = true;
        Events.run(EventType.Trigger.update, PlayerInterface::update);
    }

    public static void moveTo(int x, int y, Consumer<Void> onDone){
       Move task = new Move(x,y);
       if(onDone != null)task.onTaskCompleted(onDone);
       taskQueue.addLast(task);
    }

    private static void update(){
        if(taskQueue.isEmpty())return;
        if(!taskQueue.first().isCompleted()) taskQueue.first().update();
        else taskQueue.removeFirst().taskCompleted();
    }


}
