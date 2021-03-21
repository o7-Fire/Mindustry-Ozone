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

package Ozone.Bot;

import Atom.Utility.MemoryLog;
import Atom.Utility.Random;
import Ozone.Commands.CommandsCenter;
import Ozone.Experimental.Evasion.Identification;
import Ozone.Internal.InformationCenter;
import Ozone.Internal.Interface;
import Ozone.Net.ExpandableNet;
import Shared.SharedBoot;
import arc.Core;
import arc.math.Mathf;
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
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.zip.InflaterInputStream;

public class VirtualPlayer extends Player {
	private static final float dataTimeout = 60 * 18;
	private static final float playerSyncTime = 2;
	public ExpandableNet net = new ExpandableNet();
	public boolean isMobile = Vars.mobile, typing = false, building = false;
	public Atom.Utility.Log log = new MemoryLog() {
		
		@Override
		protected void output(Object raw) {
			raw = raw + "\n";
			super.output(raw);
		}
	};
	public GameState.State state;
	public String defName = name;
	Player player = this;
	int lastSent = 0;
	private float timeoutTime = 0f;
	private Interval timer = new Interval(5);
	protected long virtualId = System.currentTimeMillis();
	
	protected VirtualPlayer() {
		log.info("Yikes");
		net.log = log;
		state = (GameState.State.menu);
		net.handleClient(Packets.Connect.class, packet -> {
			log.info("Connecting to server: @", packet.addressTCP);
			admin(false);
			
			Packets.ConnectPacket c = new Packets.ConnectPacket();
			c.name = name();
			c.locale = Core.settings.getString("locale");
			c.mods = Vars.mods.getModStrings();
			c.mobile = isMobile;
			c.versionType = Version.type;
			c.color = color().rgba();
			c.usid = getUSID(packet.addressTCP);
			c.uuid = getUUID();
			state = (GameState.State.paused);
			net.send(c, Net.SendMode.tcp);
			log.info("connect packet sent");
		});
		
		net.handleClient(Packets.Disconnect.class, packet -> {
			name = defName;
			if (packet.reason != null) {
				log.warn("Disconnect: " + packet.reason);
			}else {
				log.warn("Disconnect");
			}
			net.disconnect();
			reset();
		});
		
		net.handleClient(Packets.WorldStream.class, data -> {
			log.info("Received world data: @ bytes.", data.stream.available());
			
			try (DataInputStream stream = new DataInputStream(new InflaterInputStream(data.stream))) {
				Time.clear();
				JsonIO.read(Rules.class, stream.readUTF());
				new Map(SaveIO.getSaveWriter().readStringMap(stream));
				stream.readInt();
				stream.readFloat();
				
				int id = stream.readInt();
				read(Reads.get(stream));
				this.id = id;
				add();
				
				//SaveIO.getSaveWriter().readContentHeader(stream);
				//SaveIO.getSaveWriter().readMap(stream, world.context);
			}catch (Throwable e) {
				log.err(e);
				net.disconnect();
				reset();
				return;
			}
			net.call.connectConfirm();
			net.setClientLoaded(true);
			log.info("Connected");
			state = GameState.State.playing;
		});
		
		net.handleClient(Packets.InvokePacket.class, packet -> {
			if (SharedBoot.debug) if (!InformationCenter.isCommonPacketClientReceive(packet.type))
				log.debug("Received Packets: " + InformationCenter.getPacketNameClientReceive(packet.type) + " " + packet.type);
		});
	}
	
	@Override
	public void name(String name) {
		super.name(name);
		defName = name;
	}
	
	public void disconnect() {
		net.disconnect();
		reset();
	}
	
	public void followPlayer(Player target) {
		Player p = Interface.searchPlayer(target.id + "");
		if (p == null) {
			log.err("Can't find main player, on server");
			return;
		}
		CommandsCenter.virtualPlayer(this);
		if (CommandsCenter.targetPlayer.get(id) == null)
			CommandsCenter.followPlayer(new ArrayList<>(Collections.singletonList(p.id + "")), this);
		else CommandsCenter.followPlayer(new ArrayList<>(), this);
		CommandsCenter.virtualPlayer();
	}
	
	public String getUUID() {
		return Identification.getUUID("-" + name);
	}
	
	public String getUSID(String ip) {
		return Identification.getUsid(ip + "." + name);
	}
	
	public void connect(String ip, int port) {
		try {
			reset();
			state = GameState.State.paused;
			net.connect(ip, port, () -> {
			
			});
		}catch (Throwable t) {
			log.err(t);
		}
	}
	
	@Override
	protected VirtualPlayer clone() throws CloneNotSupportedException {
		VirtualPlayer v = new VirtualPlayer();
		v.name = name;
		v.name += Random.getInt();
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
		
		
		net.update();
		if (state.equals(GameState.State.menu)) return;
		if ((net.client() && !isLocal()) || isRemote()) {
			interpolate();
		}
		if (!unit.isValid()) {
			clearUnit();
		}
		CoreBlock.CoreBuild core;
		if (!dead()) {
			set(unit);
			unit.team(team);
			deathTimer = 0;
			if (unit.type.canBoost) {
				Tile tile = unit.tileOn();
				unit.elevation = Mathf.approachDelta(unit.elevation, (tile != null && tile.solid()) || boosting ? 1.0F : 0.0F, 0.08F);
			}
		}else if ((core = bestCore()) != null) {
			deathTimer += Time.delta;
			if (deathTimer >= deathDelay) {
				core.requestSpawn(this);
				deathTimer = 0;
			}
		}
		textFadeTime -= Time.delta / (60 * 5);
		if (!net.client()) return;
		
		if (net.clientLoaded() && net.active()) {
			sync();
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
		state = (GameState.State.menu);
		lastSent = 0;
		name = defName == null ? name : defName;
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
