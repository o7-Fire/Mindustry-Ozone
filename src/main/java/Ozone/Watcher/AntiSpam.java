package Ozone.Watcher;

public class AntiSpam {
    public static int MaxDataPerSecond = 5;
    public final Object id;
    public final String alias;
    public Reason reason = Reason.Nothing;
    private Object lastData = null;
    private int lastDuplicate = 0;
    private long lastDataCaptured = 0L;
    private String reasonString = "";

    public AntiSpam(Object id, String alias) {
        this.id = id;
        this.alias = alias;
    }

    public static long rateLimit() {
        return MaxDataPerSecond / 1000;
    }

    public boolean accepted() {
        return accepted(lastData);
    }

    public boolean accepted(Object last) {
        reason = Reason.NotEnoughData;
        if (last == null) return true;
        long captureMillis = (System.currentTimeMillis() - lastDataCaptured);
        if (captureMillis < rateLimit()) {
            reasonMaxDataPerSecond(captureMillis);
            return false;
        }
        if (last.equals(lastData)) {
            reasonDuplicate();
            return false;
        }
        reason = Reason.Nothing;
        return true;
    }

    public void setData(Object data) {
        if (data.equals(lastData))
            lastDuplicate++;
        else
            lastDuplicate = 0;
        lastData = data;
        lastDataCaptured = System.currentTimeMillis();
    }

    public String getReason() {
        return (reasonString + (reasonString = ""));//order of operation
    }

    private void reasonDuplicate() {
        reason = Reason.Duplicate;
        reasonString += "[" + lastDuplicate + "x]" + "Duplicate last message for " + alias;
    }

    private void reasonMaxDataPerSecond(long capture) {
        reason = Reason.MaxDataPerSecond;
        reasonString += "MaxDataPerSecond reached for \"" + alias + "\" in " + capture + " ms\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AntiSpam) {
            AntiSpam a = (AntiSpam) obj;
            return a.id.equals(id);
        }
        return super.equals(obj);
    }

    public enum Reason {
        Duplicate("Duplicate Message/Spam"),
        MaxDataPerSecond("Ratelimit Reached"),
        NotEnoughData("Unknown/Null"),
        Nothing("Nothing");
        String description;

        Reason(String description) {
            this.description = description;
        }
    }

}
