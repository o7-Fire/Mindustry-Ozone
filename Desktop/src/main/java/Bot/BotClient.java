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

package Bot;

import Atom.Utility.Random;
import Main.Ozone;
import Ozone.Desktop.BotController;
import Premain.BotEntryPoint;
import arc.util.Log;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static Ozone.Desktop.BotController.generateProp;

public class BotClient {
    public final StringBuilder sb = new StringBuilder();
    public String name, rmiName;
    private int id = 0, port;
    public Process process;
    public InputStream is, er;
    public long ping;
    ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor();
    ExecutorService service = Executors.newFixedThreadPool(2);
    BotInterface rmi;
    private Status status;

    public BotClient(String name) {
        this.name = name;
        this.port = Random.getInt(1000, 40000);
        this.rmiName = BotController.base + name;
        if (rmiName.equals(BotController.base))
            throw new IllegalStateException("RMI name client equal RMI name server");
        status = Status.OFFLINE;
    }

    public boolean connected() {
        return rmi != null;
    }

    public boolean launched() {
        return process != null;
    }

    public int getPort() {
        return port;
    }

    public String getRmiName() {
        return rmiName;
    }

    public BotInterface getRmi() {
        return rmi;
    }

    public int getId() {
        return id;
    }

    public void refreshID() {
        if (rmi != null)
            try {
                int i = rmi.getID();
                if (i == 0) return;
                id = rmi.getID();
            } catch (Throwable ignored) {
            }
    }

    public Status getStatus() {
        return status;
    }

    public synchronized void setStatus(Status s) {
        status = s;
    }

    public BotInterface connect() throws RemoteException, NotBoundException {
        if (!launched()) throw new IllegalStateException("Not yet launched");
        if (connected()) throw new IllegalStateException("Already have RMI attached");
        Registry registry = LocateRegistry.getRegistry(getPort());
        BotInterface b = (BotInterface) registry.lookup(getRmiName());
        attachRMI(b);
        return b;
    }

    public Process launch() throws IOException {
        if (launched()) throw new IllegalStateException("Already launched");
        StringBuilder cli = new StringBuilder();
        cli.append(System.getProperty("java.home")).append(File.separator).append("bin").append(File.separator).append("java ");
        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            cli.append(jvmArg).append(" ");
        }
        for (Map.Entry<String, String> p : generateProp(this).entrySet())
            cli.append("-D").append(p.getKey()).append("=").append(p.getValue()).append(" ");
        cli.append("-cp ");
        cli.append(Ozone.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        cli.append(" ");
        cli.append(BotEntryPoint.class.getTypeName()).append(" ");
        Process p = Runtime.getRuntime().exec(cli.toString());
        attachProcess(p);
        return p;
    }

    public void attachRMI(BotInterface b) {
        if (rmi != null) throw new IllegalStateException("Already have RMI attached");
        rmi = b;
        //check RMI connection
        service.submit(() -> {
            try {
                if (rmi.alive())
                    setStatus(Status.ONLINE);
                else {
                    setStatus(Status.ERROR);
                    return;
                }
            } catch (Throwable ignored) {
                setStatus(Status.ERROR);
                return;
            }
            refreshID();
            while (rmi != null) {
                try {
                    Thread.sleep(500);
                    long s = System.currentTimeMillis();
                    if (!rmi.alive()) break;
                    ping = System.currentTimeMillis() - s;
                } catch (RemoteException | InterruptedException remoteException) {
                    remoteException.printStackTrace();
                    Log.err(remoteException);
                }
            }
            setStatus(Status.OFFLINE);
        });
    }

    public void attachProcess(Process p) {
        if (process != null) throw new IllegalStateException("Already have process attached");
        process = p;
        is = p.getInputStream();
        er = p.getErrorStream();
        //check for process exit
        service.submit(() -> {
            while (process != null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                if (!process.isAlive()) {
                    synchronized (sb) {
                        sb.append("Bot Exited: ").append(process.exitValue());
                    }
                    setStatus(Status.OFFLINE);
                    break;
                }
            }
        });
        service.submit(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            readProcessStream(reader);
        });
        service.submit(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            readProcessStream(reader);
        });
    }

    private void readProcessStream(BufferedReader reader) {
        String line = "";
        while (true) {
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            synchronized (sb) {
                sb.append(line).append("\n");
            }
        }
    }
}
