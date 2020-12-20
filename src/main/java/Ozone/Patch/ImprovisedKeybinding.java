/*
 * Copyright 2020 Itzbenz
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

package Ozone.Patch;

import arc.KeyBinds;
import arc.input.InputDevice;

public class ImprovisedKeybinding implements KeyBinds.KeyBind {
	public mode keyMode;
	private String name, category;
	private KeyBinds.KeybindValue value;
	
	public ImprovisedKeybinding(String name, KeyBinds.KeybindValue value, String Category, mode keyMode) {
		this.name = name;
		this.value = value;
		this.category = Category;
		this.keyMode = keyMode;
	}
	
	@Override
	public String name() {
		return name;
	}
	
	@Override
	public KeyBinds.KeybindValue defaultValue(InputDevice.DeviceType deviceType) {
		return value;
	}
	
	public KeyBinds.KeybindValue defaultValue() {
		return value;
	}
	
	@Override
	public String category() {
		return category;
	}
	
	public enum mode {down, release, tap}
}
