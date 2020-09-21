package Ozone.Commands;

import Ozone.Commands.Task.Move;
import Ozone.Commands.Task.Task;
import arc.Events;
import arc.math.geom.Vec2;
import arc.struct.Queue;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.world.Tile;

import java.util.function.Consumer;

public class BotInterface {
    private static final Queue<Task> taskQueue = new Queue<>();
    private static volatile boolean init = false;

    public static void init() {
        if (init) return;
        init = true;
        Events.run(EventType.Trigger.update, BotInterface::update);
        Events.run(EventType.WorldLoadEvent.class, BotInterface::reset);
    }

    private static void update() {
        if (taskQueue.isEmpty()) return;
        if (!taskQueue.first().isCompleted()) taskQueue.first().update();
        else taskQueue.removeFirst().taskCompleted();
    }

    public static void addTask(Task task, Consumer<Object> onDone) {
        if (onDone != null) task.onTaskCompleted(onDone);
        taskQueue.addLast(task);
    }

    public static void moveTo(int x, int y, Consumer<Object> onDone) {
        Move task = new Move(x, y);
        if (onDone != null) task.onTaskCompleted(onDone);
        taskQueue.addLast(task);
    }


    public static Vec2 getCurrentPos() {
        return new Vec2(Vars.player.x, Vars.player.y);
    }

    public static Vec2 getCurrentPos(Tile t) {
        return new Vec2(t.x * 8, t.y * 8);
    }

    public static Vec2 getCurrentTilePos() {
        return new Vec2(Vars.player.tileX(), Vars.player.tileY());
    }

    public static Vec2 getCurrentTilePos(Vec2 ref) {
        return new Vec2(Math.round(ref.x / 8), Math.round(ref.y / 8));
    }


    private static void reset() {
        taskQueue.clear();
    }


}
