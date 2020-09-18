package Ozone.Patch;

import arc.KeyBinds;

public class ImprovisedKeybinding {
    public KeyBinds.KeyBind keyBind;
    public mode keyMode;

    public ImprovisedKeybinding(KeyBinds.KeyBind keyBind, mode keyMode) {
        this.keyBind = keyBind;
        this.keyMode = keyMode;
    }

    public enum mode {down, release, tap}
}
