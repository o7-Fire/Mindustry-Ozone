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

package Ozone.Patch.Mindustry;

import Atom.Reflect.Reflect;
import Ozone.Internal.InformationCenter;
import Ozone.Net.LoggableNet;
import arc.struct.IntMap;
import mindustry.Vars;
import mindustry.net.Net;
import mindustry.net.Packets;
import mindustry.net.Streamable;

public class NetPatched extends LoggableNet {
	
	
	public NetPatched(Net net) {
		super(net);
	}
	
	@Override
	public void connect(String ip, int port, Runnable success) {
		if (Vars.ui != null && Vars.ui.loadfrag != null) {
			Vars.ui.loadfrag.setProgress(() -> 0.05f);
		}
		Runnable r = () -> {
			InformationCenter.currentServerPort = port;
			InformationCenter.currentServerIP = ip;
			if (Vars.ui != null && Vars.ui.loadfrag != null) {
				Vars.ui.loadfrag.setProgress(() -> 1f);
			}
			success.run();
		};
		super.connect(ip, port, r);
	}
	
	@Override
	public void send(Object object, SendMode mode) {
		super.send(object, mode);
		if (!server()) {
			if (object instanceof Packets.ConnectPacket) if (Vars.ui != null && Vars.ui.loadfrag != null) {
				Vars.ui.loadfrag.setProgress(() -> 0.15f);
			}
		}
	}
	
	@Override
	public void handleClientReceived(Object object) {
		super.handleClientReceived(object);
		if (object instanceof Packets.Connect) {
			if (Vars.ui != null && Vars.ui.loadfrag != null) {
				Vars.ui.loadfrag.setProgress(() -> 0.1f);
			}
		}
		if (Vars.ui == null || Vars.ui.loadfrag == null) return;
		IntMap<Streamable.StreamBuilder> streams = Reflect.getField(Net.class, "streams", net);
		if (streams == null) return;
		if (object instanceof Packets.StreamBegin) {
			Streamable.StreamBuilder builder = streams.get(((Packets.StreamBegin) object).id);
			if (builder == null) return;
			Vars.ui.loadfrag.setProgress(builder::progress);
		}
		if (object instanceof Packets.StreamChunk) {
			Streamable.StreamBuilder builder = streams.get(((Packets.StreamChunk) object).id);
			if (builder == null) return;
			Vars.ui.loadfrag.setText("Downloading Map " + builder.stream.size() + "/" + builder.total);
		}
	}
}
