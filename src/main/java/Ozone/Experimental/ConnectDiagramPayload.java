/*******************************************************************************
 * Copyright 2021 Itzbenz
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
 ******************************************************************************/

/* o7 Inc 2021 Copyright

  Licensed under the o7 Inc License, Version 1.0.1, ("the License");
  You may use this file but only with the License. You may obtain a
  copy of the License at
  
  https://github.com/o7-Fire/Mindustry-Ozone/Licenses
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the license for the specific language governing permissions and
  limitations under the License.
*/

package Ozone.Experimental;

import Atom.Utility.Pool;
import arc.struct.Queue;
import mindustry.Vars;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

public class ConnectDiagramPayload extends AttackDiagram {
	public static ThreadPoolExecutor service = (ThreadPoolExecutor) Executors.newCachedThreadPool(r -> {//IO Blocking operation, more thread than cpu core should not matter
		Thread t = Executors.defaultThreadFactory().newThread(r);
		t.setName(t.getName() + "-Connect-Diagram");
		t.setDaemon(true);
		t.setPriority(Thread.MIN_PRIORITY);//prevent Denial of Service to the host machine (imagine running 10000 thread)
		return t;
	});
	protected String ip = "localhost";
	protected int port = Vars.port, thread = Runtime.getRuntime().availableProcessors();
	protected Consumer<?> payload = null;
	protected boolean enableJoin = false;
	protected Queue<Future> queue = new Queue<>();
	protected Thread garbageCollector = Pool.daemon(() -> {
		while (!isCompleted()) {
			ArrayList<Future> refArr = new ArrayList<>();
			if (getQueue() != null) {
				for (Future f : queue) {
					if (f.isDone()) refArr.add(f);
				}
				for (Future f : refArr)
					getQueue().remove(f);
			}
			
			
			try { Thread.sleep(50); }catch (InterruptedException e) { }
			
		}
	});
	
	{
		garbageCollector.start();
	}
	
	public ConnectDiagramPayload setIp(String ip) {
		this.ip = ip;
		return this;
	}
	
	public ConnectDiagramPayload setPort(int port) {
		this.port = port;
		return this;
	}
	
	public ConnectDiagramPayload setThread(int thread) {
		this.thread = thread;
		return this;
	}
	
	public ConnectDiagramPayload setPayload(Consumer<?> payload) {
		this.payload = payload;
		return this;
	}
	
	public ConnectDiagramPayload setEnableJoin(boolean enableJoin) {
		this.enableJoin = enableJoin;
		return this;
	}
	
	
	@Override
	public void onCompleted() {
		for (Future f : queue)
			f.cancel(true);
		queue.clear();
	}
	
	protected synchronized Queue<Future> getQueue() {
		return queue;
	}
	
	@Override
	void run() {
		
		while (getQueue().size < thread) {
			getQueue().addLast(service.submit(() -> {
			
			}));
		}
		
	}
}
