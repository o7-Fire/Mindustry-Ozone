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

import arc.net.Connection;
import arc.net.DcReason;
import arc.net.FrameworkMessage;
import arc.net.NetListener;
import mindustry.net.Packets;

public class ClichedNetListener implements NetListener {
	
	
	@Override
	public void connected(Connection connection) {
		Packets.Connect c = new Packets.Connect();
		c.addressTCP = connection.getRemoteAddressTCP().getAddress().getHostAddress();
		if (connection.getRemoteAddressTCP() != null) c.addressTCP = connection.getRemoteAddressTCP().toString();
		post(() -> handleClientReceived(c));
	}
	
	@Override
	public void disconnected(Connection connection, DcReason reason) {
		Packets.Disconnect c = new Packets.Disconnect();
		c.reason = reason.toString();
		post(() -> handleClientReceived(c));
	}
	
	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof FrameworkMessage) return;
		post(() -> handleClientReceived(object));
	}
	
	public void handleClientReceived(Object o) {
		
	}
	
	protected void post(Runnable r) {
		r.run();
	}
}
