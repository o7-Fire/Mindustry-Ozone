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
import arc.util.OS;

import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Ozone.Desktop.BotController.generateProp;

public class BotClient {
    public final StringBuilder sb = new StringBuilder();
    public String name, rmiName;
    private final int port;
    public Process process;
    public InputStream is, er;
    ExecutorService service = Executors.newFixedThreadPool(2);
    BotInterface rmi;
    private Status status;

    public BotClient(String name) {
        this.name = name;
        this.port = Random.getInt(1000, 40000);
        this.rmiName = (BotController.base + name).replace("[", "").replace("]", "").replaceAll("[0-9]", "");
        if (rmiName.equals(BotController.base))
            throw new IllegalStateException("RMI name client equal RMI name server");
        status = Status.OFFLINE;
    }

    public void exit() throws RemoteException {
        if (connected()) {
            getRmi().kill();
            process.destroy();
        } else if (launched()) {
            process.destroy();
        }
        process = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BotClient) {
            return ((BotClient) obj).name.equals(name);
        }
        return super.equals(obj);
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
        try {
            return rmi.getID();
        } catch (Throwable ignored) {
            return 0;
        }
    }


    public synchronized Status getStatus() {
        return status;
    }

    public synchronized void setStatus(Status s) {
        status = s;
    }

    public BotInterface connect() throws RemoteException, NotBoundException {
        if (!launched()) throw new IllegalStateException("Not yet launched");
        if (connected()) throw new IllegalStateException("Already have RMI attached");
        try {
            setStatus(Status.CONNECTING);
            Registry registry = LocateRegistry.getRegistry(getPort());
            BotInterface b = (BotInterface) registry.lookup(getRmiName());
            attachRMI(b);
            setStatus(Status.CONNECTED);
            return b;
        } catch (Throwable t) {
            setStatus(Status.ERROR);
            throw t;
        }

    }

    public Process launch() throws IOException {
        if (launched()) throw new IllegalStateException("Already launched");
        try {
            setStatus(Status.LAUNCHING);
            StringBuilder cli = new StringBuilder();
            cli.append(System.getProperty("java.home")).append(File.separator).append("bin").append(File.separator).append("java ");
            // for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) cli.append(jvmArg).append(" ");

            for (Map.Entry<String, String> p : generateProp(this).entrySet())
                cli.append("-D").append(p.getKey()).append("=").append(p.getValue()).append(" ");
            char separator = OS.isWindows ? ';' : ':';
            cli.append("-cp ");
            cli.append(Ozone.class.getProtectionDomain().getCodeSource().getLocation().getFile());
            for (String s : System.getProperty("java.class.path").split(String.valueOf(separator)))
                cli.append(separator).append(s);
            cli.append(" ");
            cli.append(BotEntryPoint.class.getTypeName()).append(" ");
            Process p = Runtime.getRuntime().exec(cli.toString());
            attachProcess(p);
            setStatus(Status.LAUNCHED);
            return p;
        } catch (Throwable t) {
            setStatus(Status.ERROR);
            throw t;
        }

    }

    public void attachRMI(BotInterface b) {
        if (rmi != null) throw new IllegalStateException("Already have RMI attached");
        rmi = b;
        //check RMI connection
        service.submit(() -> {
            try {
                if (rmi.alive())
                    setStatus(Status.CONNECTING);
                else {
                    setStatus(Status.ERROR);
                    return;
                }
            } catch (Throwable t) {
                t.printStackTrace();
                Log.err(t);
                setStatus(Status.ERROR);
                return;
            }
            while (rmi != null) {
                try {
                    Thread.sleep(500);
                    if (!rmi.alive()) break;
                    if (!status.equals(Status.CONNECTED)) setStatus(Status.ONLINE);
                } catch (RemoteException | InterruptedException remoteException) {
                    remoteException.printStackTrace();
                    Log.err(remoteException);
                }
            }
            setStatus(Status.OFFLINE);
            rmi = null;
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
                        sb.append("\n\n[yellow]Bot Exited: ").append(process.exitValue());
                    }
                    process = null;
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
        while (process != null) {
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            System.out.println(line);
            synchronized (sb) {
                sb.append(line).append("\n");
            }
        }
    }
}
