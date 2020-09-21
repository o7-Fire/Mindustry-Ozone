package Ozone.Patch;

import arc.KeyBinds;
import arc.input.InputDevice;

public class ImprovisedKeybinding implements KeyBinds.KeyBind {
    private String name, category;
    private KeyBinds.KeybindValue value;
    public mode keyMode;

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
