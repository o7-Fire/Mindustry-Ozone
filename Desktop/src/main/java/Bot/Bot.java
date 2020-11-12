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

import arc.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Bot {
    public final StringBuilder sb = new StringBuilder();
    public String name;
    public int id;
    public Process process;
    public InputStream is, er;
    public long ping;
    ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor();
    ExecutorService service = Executors.newFixedThreadPool(2);
    BotInterface rmi;
    private Status status;

    public Bot(String name, int id) {
        this.name = name;
        this.id = id;
        status = Status.OFFLINE;
    }

    public BotInterface getRmi() {
        return rmi;
    }

    public Status getStatus() {
        return status;
    }

    public synchronized void setStatus(Status s) {
        status = s;
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
