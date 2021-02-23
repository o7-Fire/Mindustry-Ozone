/*
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
 */

package Ozone.Net;

import arc.net.ArcNetException;
import arc.util.Log;
import arc.util.TaskQueue;
import mindustry.net.Net;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

public class ExpandableNet extends Net {
	protected ExpandableNetProvider netProvider;
	protected TaskQueue taskQueue = new TaskQueue();
	
	public ExpandableNet(ExpandableNetProvider provider) {
		super(provider);
		this.netProvider = provider;//lol
		provider.setNet(this);
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
	
	protected void post(Runnable r) {
		taskQueue.post(r);
	}
	
	public void update() {
		taskQueue.run();
	}
	
	@Override
	public void showError(Throwable e) {
		Throwable t = e;
		while (t.getCause() != null) {
			t = t.getCause();
		}
		Log.err(t);
	}
}
