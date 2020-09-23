package Ozone.Commands;

import Ozone.Commands.Task.Task;
import Ozone.Patch.DesktopInput;
import arc.Events;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.Queue;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.world.Tile;
import org.openjdk.tools.sjavac.Log;

import java.util.ArrayList;
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
        Log.debug("Task: " + task.getName() + " has been added to queue : " + taskQueue.size);
    }

    public static void setMov(Position targetTile) {
        Vec2 vec = new Vec2();
        vec.trns(Vars.player.unit().angleTo(targetTile), Vars.player.unit().type().speed);
        arc.util.Log.debug("Ozone-AI @", "DriveX: " + vec.x);
        arc.util.Log.debug("Ozone-AI @", "DriveY: " + vec.y);
        Vars.player.unit().moveAt(vec);
    }

    public static void setMov(Vec2 mov) {
        if (Vars.control.input instanceof DesktopInput) ((DesktopInput) Vars.control.input).setMove(mov);
        else arc.util.Log.infoTag("Ozone", "Can't control movement, DesktopInput not patched");
    }

    public static ArrayList<Tile> getNearby(Tile tile, int rotation, int range) {
        ArrayList<Tile> tiles = new ArrayList<>();
        if (rotation == 0) {
            for (int i = 0; i < range; i++) {
                tiles.add(Vars.world.tile(tile.x + 1 + i, tile.y));
            }
        } else if (rotation == 1) {
            for (int i = 0; i < range; i++) {
                tiles.add(Vars.world.tile(tile.x, tile.y + 1 + i));
            }
        } else if (rotation == 2) {
            for (int i = 0; i < range; i++) {
                tiles.add(Vars.world.tile(tile.x - 1 - i, tile.y));
            }
        } else if (rotation == 3) {
            for (int i = 0; i < range; i++) {
                tiles.add(Vars.world.tile(tile.x, tile.y - 1 - i));
            }
        }
        return tiles;
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
