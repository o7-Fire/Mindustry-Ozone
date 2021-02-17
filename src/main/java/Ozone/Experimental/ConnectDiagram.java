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

package Ozone.Experimental;

import Atom.String.WordGenerator;
import Atom.Utility.Pool;
import Atom.Utility.Random;
import Ozone.Experimental.Evasion.Identification;
import mindustry.Vars;

import static mindustry.Vars.*;

public class ConnectDiagram extends AttackDiagram {
	String ip;
	int port;
	String playerName = player.name;
	String uuid = Identification.getUUID();
	
	public ConnectDiagram(String ip, int port) {
		this.ip = ip;
		this.port = port;
		ui.loadfrag.show("@connecting");
		ui.loadfrag.setButton(() -> {
			completed = true;
		});
		
	}
	
	@Override
	public void onCompleted() {
		super.onCompleted();
		ui.loadfrag.hide();
		netClient.disconnectQuietly();
	}
	
	@Override
	public void run() {
		
		for (int i = 0; i < 3; i++) {
			Pool.daemon(() -> {
				try {
					Identification.changeID();
					player.name = WordGenerator.newWord(Random.getInt(maxNameLength));
					netClient.disconnectQuietly();
					logic.reset();
					Vars.net.reset();
					Vars.net.connect(ip, port, () -> {
						netClient.disconnectQuietly();
					});
				}catch (Throwable ignored) {}
			}).start();
		}
		
		
	}
}
