//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mindustry.net;

import Ozone.Event.EventExtended;
import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.func.Cons2;
import arc.net.ArcNetException;
import arc.struct.IntMap;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Strings;
import arc.util.Time;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.core.Platform;
import mindustry.gen.Call;
import mindustry.net.Packets.KickReason;
import mindustry.net.Packets.StreamBegin;
import mindustry.net.Packets.StreamChunk;
import mindustry.net.Streamable.StreamBuilder;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.util.Iterator;
import java.util.Objects;

public class Net {
    private final Seq<Object> packetQueue = new Seq();
    private final ObjectMap<Class<?>, Cons> clientListeners = new ObjectMap();
    private final ObjectMap<Class<?>, Cons2<NetConnection, Object>> serverListeners = new ObjectMap();
    private final IntMap<StreamBuilder> streams = new IntMap();
    private final Net.NetProvider provider;
    private final LZ4FastDecompressor decompressor = LZ4Factory.fastestInstance().fastDecompressor();
    private final LZ4Compressor compressor = LZ4Factory.fastestInstance().fastCompressor();
    private boolean server;
    private boolean active;
    private boolean clientLoaded;
    @Nullable
    private StreamBuilder currentStream;

    public Net(Net.NetProvider provider) {
        this.provider = provider;
    }

    public void handleException(Throwable e) {
        if (e instanceof ArcNetException) {
            Core.app.post(() -> {
                this.showError(new IOException("mismatch"));
            });
        } else if (e instanceof ClosedChannelException) {
            Core.app.post(() -> {
                this.showError(new IOException("alreadyconnected"));
            });
        } else {
            Core.app.post(() -> {
                this.showError(e);
            });
        }

    }

    public void showError(Throwable e) {
        if (!Vars.headless) {
            Throwable t;
            for (t = e; t.getCause() != null; t = t.getCause()) {
            }

            String baseError = Strings.getFinalMessage(e);
            String error = baseError == null ? "" : baseError.toLowerCase();
            String type = t.getClass().toString().toLowerCase();
            boolean isError = false;
            if (!(e instanceof BufferUnderflowException) && !(e instanceof BufferOverflowException)) {
                if (error.equals("mismatch")) {
                    error = Core.bundle.get("error.mismatch");
                } else if (!error.contains("port out of range") && !error.contains("invalid argument") && (!error.contains("invalid") || !error.contains("address")) && !Strings.neatError(e).contains("address associated")) {
                    if (!error.contains("connection refused") && !error.contains("route to host") && !type.contains("unknownhost")) {
                        if (type.contains("timeout")) {
                            error = Core.bundle.get("error.timedout");
                        } else if (!error.equals("alreadyconnected") && !error.contains("connection is closed")) {
                            if (!error.isEmpty()) {
                                error = Core.bundle.get("error.any");
                                isError = true;
                            }
                        } else {
                            error = Core.bundle.get("error.alreadyconnected");
                        }
                    } else {
                        error = Core.bundle.get("error.unreachable");
                    }
                } else {
                    error = Core.bundle.get("error.invalidaddress");
                }
            } else {
                error = Core.bundle.get("error.io");
            }

            if (isError) {
                Vars.ui.showException("@error.any", e);
            } else {
                Vars.ui.showText("", Core.bundle.format("connectfail", new Object[]{error}));
            }

            Vars.ui.loadfrag.hide();
            if (this.client()) {
                Vars.netClient.disconnectQuietly();
            }
        }

        Log.err(e);
    }

    public void setClientLoaded(boolean loaded) {
        this.clientLoaded = loaded;
        if (loaded) {
            for (int i = 0; i < this.packetQueue.size; ++i) {
                this.handleClientReceived(this.packetQueue.get(i));
            }
        }

        this.packetQueue.clear();
    }

    public void setClientConnected() {
        this.active = true;
        this.server = false;
    }

    public void connect(String ip, int port, Runnable success) {
        try {
            if (this.active) {
                throw new IOException("alreadyconnected");
            }

            this.provider.connectClient(ip, port, success);
            this.active = true;
            this.server = false;
            Events.fire(new EventExtended.Connecting(ip, port));
        } catch (IOException var5) {
            this.showError(var5);
        }

    }

    public void host(int port) throws IOException {
        this.provider.hostServer(port);
        this.active = true;
        this.server = true;
        Platform var10001 = Vars.platform;
        Objects.requireNonNull(var10001);
        Time.runTask(60.0F, var10001::updateRPC);
    }

    public void closeServer() {
        Iterator var1 = this.getConnections().iterator();

        while (var1.hasNext()) {
            NetConnection con = (NetConnection) var1.next();
            Call.kick(con, KickReason.serverClose);
        }

        this.provider.closeServer();
        this.server = false;
        this.active = false;
    }

    public void reset() {
        this.closeServer();
        Vars.netClient.disconnectNoReset();
    }

    public void disconnect() {
        if (this.active && !this.server) {
            Log.info("Disconnecting.");
        }

        this.provider.disconnectClient();
        this.server = false;
        this.active = false;
    }

    public byte[] compressSnapshot(byte[] input) {
        return this.compressor.compress(input);
    }

    public byte[] decompressSnapshot(byte[] input, int size) {
        return this.decompressor.decompress(input, size);
    }

    public void discoverServers(Cons<Host> cons, Runnable done) {
        this.provider.discoverServers(cons, done);
    }

    public Iterable<NetConnection> getConnections() {
        return (Iterable<NetConnection>) this.provider.getConnections();
    }

    public void send(Object object, Net.SendMode mode) {
        if (this.server) {
            Iterator var3 = this.provider.getConnections().iterator();

            while (var3.hasNext()) {
                NetConnection con = (NetConnection) var3.next();
                con.send(object, mode);
            }
        } else {
            this.provider.sendClient(object, mode);
        }

    }

    public void sendExcept(NetConnection except, Object object, Net.SendMode mode) {
        Iterator var4 = this.getConnections().iterator();

        while (var4.hasNext()) {
            NetConnection con = (NetConnection) var4.next();
            if (con != except) {
                con.send(object, mode);
            }
        }

    }

    @Nullable
    public StreamBuilder getCurrentStream() {
        return this.currentStream;
    }

    public <T> void handleClient(Class<T> type, Cons<T> listener) {
        this.clientListeners.put(type, listener);
    }

    public <T> void handleServer(Class<T> type, Cons2<NetConnection, T> listener) {
        this.serverListeners.put(type, (Cons2<NetConnection, Object>) listener);
    }

    public void handleClientReceived(Object object) {
        StreamBegin b;
        if (object instanceof StreamBegin && (b = (StreamBegin) object) == (StreamBegin) object) {
            this.streams.put(b.id, this.currentStream = new StreamBuilder(b));
        } else {
            StreamChunk c;
            if (object instanceof StreamChunk && (c = (StreamChunk) object) == (StreamChunk) object) {
                StreamBuilder builder = (StreamBuilder) this.streams.get(c.id);
                if (builder == null) {
                    throw new RuntimeException("Received stream chunk without a StreamBegin beforehand!");
                }

                builder.add(c.data);
                if (builder.isDone()) {
                    this.streams.remove(builder.id);
                    this.handleClientReceived(builder.build());
                    this.currentStream = null;
                }
            } else if (this.clientListeners.get(object.getClass()) != null) {
                if (this.clientLoaded || object instanceof Packet && ((Packet) object).isImportant()) {
                    if (this.clientListeners.get(object.getClass()) != null) {
                        ((Cons) this.clientListeners.get(object.getClass())).get(object);
                    }

                    Pools.free(object);
                } else if (object instanceof Packet && ((Packet) object).isUnimportant()) {
                    Pools.free(object);
                } else {
                    this.packetQueue.add(object);
                }
            } else {
                Log.err("Unhandled packet type: '@'!", new Object[]{object});
            }
        }

    }

    public void handleServerReceived(NetConnection connection, Object object) {
        if (this.serverListeners.get(object.getClass()) != null) {
            if (this.serverListeners.get(object.getClass()) != null) {
                ((Cons2) this.serverListeners.get(object.getClass())).get(connection, object);
            }

            Pools.free(object);
        } else {
            Log.err("Unhandled packet type: '@'!", new Object[]{object.getClass()});
        }

    }

    public void pingHost(String address, int port, Cons<Host> valid, Cons<Exception> failed) {
        this.provider.pingHost(address, port, valid, failed);
    }

    public boolean active() {
        return this.active;
    }

    public boolean server() {
        return this.server && this.active;
    }

    public boolean client() {
        return !this.server && this.active;
    }

    public void dispose() {
        this.provider.dispose();
        this.server = false;
        this.active = false;
    }

    public static enum SendMode {
        tcp,
        udp;

        private SendMode() {
        }
    }

    public interface NetProvider {
        void connectClient(String var1, int var2, Runnable var3) throws IOException;

        void sendClient(Object var1, Net.SendMode var2);

        void disconnectClient();

        void discoverServers(Cons<Host> var1, Runnable var2);

        void pingHost(String var1, int var2, Cons<Host> var3, Cons<Exception> var4);

        void hostServer(int var1) throws IOException;

        Iterable<? extends NetConnection> getConnections();

        void closeServer();

        default void dispose() {
            this.disconnectClient();
            this.closeServer();
        }
    }
}
