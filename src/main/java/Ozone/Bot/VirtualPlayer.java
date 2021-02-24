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

package Ozone.Bot;

import Ozone.Experimental.Evasion.Identification;
import Ozone.Net.ExpandableNet;
import arc.Core;
import arc.util.Interval;
import arc.util.Time;
import arc.util.io.Reads;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.core.NetClient;
import mindustry.core.Version;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Rules;
import mindustry.gen.Mechc;
import mindustry.gen.Nulls;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.io.JsonIO;
import mindustry.io.SaveIO;
import mindustry.maps.Map;
import mindustry.net.Net;
import mindustry.net.Packets;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;

import static Ozone.Experimental.Evasion.Identification.getUsid;

public class VirtualPlayer extends Player {
	private static final float dataTimeout = 60 * 18;
	private static final float playerSyncTime = 2;
	public ExpandableNet net = new ExpandableNet();
	public boolean isMobile = Vars.mobile, typing = false, building = false;
	public Atom.Utility.Log log = new Atom.Utility.Log() {
		@Override
		protected void output(Object raw) {
			raw = "[" + name + "]" + raw.toString();
			super.output(raw);
		}
	};
	public GameState state = new GameState();
	public String defName = name;
	Player player = this;
	int lastSent = 0;
	private float timeoutTime = 0f;
	private Interval timer = new Interval(5);
	protected long virtualId = System.currentTimeMillis();
	
	protected VirtualPlayer() {
		state.set(GameState.State.menu);
		net.handleClient(Packets.Connect.class, packet -> {
			log.info("Connecting to server: @", packet.addressTCP);
			admin(false);
			reset();
			Packets.ConnectPacket c = new Packets.ConnectPacket();
			c.name = name;
			c.locale = Core.settings.getString("locale");
			c.mods = Vars.mods.getModStrings();
			c.mobile = isMobile;
			c.versionType = Version.type;
			c.color = color().rgba();
			c.usid = getUsid(packet.addressTCP + name);
			c.uuid = Identification.getUUID();
			defName = name;
			if (c.uuid == null) {
				log.warn("No uuid err");
				net.disconnect();
				return;
			}
			state.set(GameState.State.paused);
			net.send(c, Net.SendMode.tcp);
		});
		
		net.handleClient(Packets.Disconnect.class, packet -> {
			name = defName;
			if (packet.reason != null) {
				log.warn("Disconnect: " + packet.reason);
			}else {
				log.warn("Disconnect");
			}
			reset();
		});
		
		net.handleClient(Packets.WorldStream.class, data -> {
			log.info("Received world data: @ bytes.", data.stream.available());
			try (DataInputStream stream = new DataInputStream(data.stream)) {
				Time.clear();
				JsonIO.read(Rules.class, stream.readUTF());
				new Map(SaveIO.getSaveWriter().readStringMap(stream));
				stream.readInt();
				stream.readFloat();
				
				int id = stream.readInt();
				reset();
				read(Reads.get(stream));
				this.id = id;
				add();
				
				//SaveIO.getSaveWriter().readContentHeader(stream);
				//SaveIO.getSaveWriter().readMap(stream, world.context);
				stream.close();
			}catch (IOException e) {
				throw new RuntimeException(e);
			}
			net.call.connectConfirm();
			net.setClientLoaded(true);
			state.set(GameState.State.playing);
		});
		
		net.handleClient(Packets.InvokePacket.class, packet -> {
			//lol no
		});
	}
	
	@Override
	protected VirtualPlayer clone() throws CloneNotSupportedException {
		VirtualPlayer v = (VirtualPlayer) super.clone();
		v.virtualId = System.currentTimeMillis();
		return v;
	}
	
	public long vid() {
		return virtualId;
	}
	
	protected void delete() {
		net.dispose();
	}
	
	@Override
	public void update() {
		
		
		super.update();
		if (!net.client()) return;
		
		if (net.clientLoaded()) {
			sync();
		}else if (!net.clientLoaded() && net.active()) {
			net.disconnect();
		}else { //...must be connecting
			timeoutTime += Time.delta;
			if (timeoutTime > dataTimeout) {
				log.err("Failed to load data!");
				net.disconnect();
				timeoutTime = 0f;
			}
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		state.set(GameState.State.menu);
		lastSent = 0;
	}
	
	void sync() {
		if (timer.get(0, playerSyncTime)) {
			BuildPlan[] requests = null;
			if (player.isBuilder()) {
				//limit to 10 to prevent buffer overflows
				int usedRequests = Math.min(player.unit().plans().size, 10);
				
				int totalLength = 0;
				
				//prevent buffer overflow by checking config length
				for (int i = 0; i < usedRequests; i++) {
					BuildPlan plan = unit().plans().get(i);
					if (plan.config instanceof byte[]) {
						byte[] b = (byte[]) plan.config;
						int length = b.length;
						totalLength += length;
					}
					
					if (totalLength > 1024) {
						usedRequests = i + 1;
						break;
					}
				}
				
				requests = new BuildPlan[usedRequests];
				for (int i = 0; i < usedRequests; i++) {
					requests[i] = unit().plans().get(i);
				}
			}
			
			Unit unit = dead() ? Nulls.unit : unit();
			int uid = dead() ? -1 : unit.id;
			
			net.call.clientSnapshot(lastSent++, uid, dead(), unit.x, unit.y, player.unit().aimX(), player.unit().aimY(), unit.rotation, (unit instanceof Mechc) ? ((Mechc) unit).baseRotation() : 0, unit.vel.x, unit.vel.y, player.unit().mineTile, player.boosting, player.shooting, typing, building, requests, Core.camera.position.x, Core.camera.position.y, Core.camera.width * NetClient.viewScale, Core.camera.height * NetClient.viewScale);
		}
		
		if (timer.get(1, 60)) {
			net.call.ping(Time.millis());
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof VirtualPlayer)) return false;
		VirtualPlayer that = (VirtualPlayer) o;
		return virtualId == that.virtualId;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(virtualId);
	}
}
