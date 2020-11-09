package Ozone.Event;

public class EventExtended {
    public enum Connect {
        Disconnected,
        Connected
    }

    public static class Connecting {
        public String ip;
        public int port;

        public Connecting(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }
    }
}
