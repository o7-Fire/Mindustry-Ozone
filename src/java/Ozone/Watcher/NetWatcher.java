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

package Ozone.Watcher;

import Ozone.Internal.InformationCenter;
import Ozone.Internal.Interface;
import Ozone.Net.LoggableNet;
import arc.util.Log;
import mindustry.Vars;
import mindustry.net.Net;
import mindustry.net.Packet;
import mindustry.net.Packets;

public class NetWatcher extends LoggableNet {
	
	public NetWatcher(Net net) {
		super(net);
		Object provider = Vars.platform.getNet();
		Log.infoTag("OzoneNet-Watcher", provider.getClass().getName());
		Interface.showInfo("OzoneNet Watcher: " + provider.getClass().getName());
		
	}
	
	@Override
	public void send(Object object, SendMode mode) {
		if (object instanceof Packets.InvokePacket) {
			if (!InformationCenter.isCommonPacketClientSend(((Packets.InvokePacket) object).type))
				Log.infoTag("Packet-" + mode.name() + "-Send", "[" + ((Packets.InvokePacket) object).type + "] " + InformationCenter.getPacketNameClientSend(((Packets.InvokePacket) object).type));
		}else if (object instanceof Packet) Log.infoTag("Packet-" + mode.name() + "-Send", object.getClass().getName());
		
		net.send(object, mode);
	}
	
	@Override
	public void handleClientReceived(Object object) {
		if (object instanceof Packets.InvokePacket) {
			if (!InformationCenter.isCommonPacketClientReceive(((Packets.InvokePacket) object).type))
				Log.infoTag("Packet-Receive", "[" + ((Packets.InvokePacket) object).type + "] " + InformationCenter.getPacketNameClientReceive(((Packets.InvokePacket) object).type));
		}else if (object instanceof Packet) Log.infoTag("Packet-Receive", object.getClass().getName());
		net.handleClientReceived(object);
	}
	
}
