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

package Ozone.Commands.Class;

import Ozone.Commands.CommandsCenter;
import Ozone.Commands.Task.Task;
import Ozone.Commands.TaskInterface;
import Ozone.Gen.Callable;
import Ozone.Internal.InformationCenter;
import Ozone.Patch.Translation;
import arc.scene.style.TextureRegionDrawable;
import com.beust.jcommander.Parameter;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.gen.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class CommandsClass implements Cloneable {
	@NotNull
	public Player player = Vars.player;
	@NotNull
	public Callable callable = InformationCenter.getCallableMain();
	@NotNull
	public String description = "none", name = this.getClass().getSimpleName();//CommandsClass
	@NotNull
	public TextureRegionDrawable icon = Icon.box;
	@Parameter
	public ArrayList<String> parameters = new ArrayList<>();
	public boolean supportNoArg = false,//Support no user input if there is none
			taskBound = false,//using task as mean to achieve objective
			supportNetwork = false,//support called from network diagram payload
			supportDirectInvoke = false;//support direct invoke without need to create new CommandsClass();
	
	public CommandsClass() {
	
	}
	
	public static void addTask(Task t) {
		TaskInterface.addTask(t);//bro wtf
	}
	
	abstract void run() throws Exception;
	
	public String nameTranslated() {
		return Translation.get(name);
	}
	
	public void runDirect(ArrayList<String> s) {
	
	}
	
	public static void tellUser(String s) {
		CommandsCenter.tellUser(s);
	}
	
	public CommandsClass copy(Player p, Callable c) {
		try {
			CommandsClass cc = (CommandsClass) clone();
			cc.callable = c;
			cc.player = p;
			return cc;
		}catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String toString() {
		return name + ": " + description;
	}
}
