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
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.gen.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CommandsClass<A extends CommandsArgument> {
	@NotNull
	public Player player = Vars.player;
	@NotNull
	public Callable callable = InformationCenter.getCallableMain();
	@NotNull
	public String description = "none", name = this.getClass().getSimpleName();//CommandsClass
	@NotNull
	public TextureRegionDrawable icon = Icon.box;
	public A argument;
	public boolean taskBound = false,//using task as mean to achieve objective
			supportNetwork = false;//support called from network diagram payload
	
	
	public CommandsClass() {
	
	}
	
	public void playerCallDefault() {
		player = Vars.player;
		callable = InformationCenter.getCallableMain();
	}
	
	public static void addTask(Task t) {
		TaskInterface.addTask(t);//bro wtf
	}
	
	public void run(A argument) {
		try {
			this.argument = argument;
			run();
		}catch (VirtualMachineError v) {
			throw new RuntimeException(v);
		}catch (Throwable t) {
			Vars.ui.showException(t);
		}
	}
	
	public abstract void run() throws Exception;
	
	public String nameTranslated() {
		return Translation.get(name);
	}
	
	@Nullable
	public abstract A getArgumentClass();
	
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
