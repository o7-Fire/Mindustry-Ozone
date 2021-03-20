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

package Ozone.Net;

import Atom.Utility.MemoryLog;
import Ozone.Gen.Callable;
import arc.net.ArcNetException;
import arc.util.TaskQueue;
import mindustry.net.Net;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

public class ExpandableNet extends Net {
	protected ExpandableNetProvider netProvider;
	protected TaskQueue taskQueue = new TaskQueue();
	public Callable call;
	public Atom.Utility.Log log = new MemoryLog();
	
	public ExpandableNet(ExpandableNetProvider provider) {
		super(provider);
		this.netProvider = provider;//lol
		provider.setNet(this);
		call = new Callable(this);
	}
	
	public ExpandableNet() {
		this(new ExpandableNetProvider());
	}
	
	@Override
	public void handleException(Throwable e) {
		if (e instanceof ArcNetException) {
			post(() -> showError(new IOException("mismatch")));
		}else if (e instanceof ClosedChannelException) {
			post(() -> showError(new IOException("alreadyconnected")));
		}else {
			post(() -> showError(e));
		}
	}
	
	boolean clientLoaded = false;
	
	public boolean clientLoaded() {
		return clientLoaded;
	}
	
	@Override
	public void setClientLoaded(boolean loaded) {
		super.setClientLoaded(loaded);
		clientLoaded = loaded;
	}
	
	@Override
	public void reset() {
		super.reset();
		clientLoaded = false;
	}
	
	public void post(Runnable r) {
		taskQueue.post(r);
	}
	
	public void update() {
		taskQueue.run();
	}
	
	@Override
	public void send(Object object, SendMode mode) {
		netProvider.sendClient(object, mode);
	}
	
	@Override
	public void showError(Throwable e) {
		Throwable t = e;
		while (t.getCause() != null) {
			t = t.getCause();
		}
		log.err(t);
	}
}
