package Ozone.Commands.Task;

import Ozone.Commands.Commands;
import mindustry.Vars;

//not finished
public class CommandsSpam extends Task {
    private int howManyTimes = 1;
    private int currentCycle = 0;
    private String commands = "";

    public CommandsSpam(String howMany, String delay, String commands) {
        try {
            if (!howMany.isEmpty())
                howManyTimes = Integer.parseInt(howMany);
            int delays;
            if (delay.isEmpty())
                delays = 0;
            else
                delays = Integer.parseInt(delay);
            if (!Commands.call(commands)) {
                currentCycle = 1;
                howManyTimes = 1;
                Vars.ui.showErrorMessage(commands + " is not a valid commands");
            } else this.commands = commands;
            setTick(delays);
        } catch (NumberFormatException c) {
            Vars.ui.showException(c);
            currentCycle = 1;
        }
    }

    @Override
    public boolean isCompleted() {
        return currentCycle > howManyTimes;
    }

    @Override
    public void update() {
        if (tick()) return;
        if (currentCycle > howManyTimes) return;
        currentCycle++;
        Commands.call(commands);
    }
}
