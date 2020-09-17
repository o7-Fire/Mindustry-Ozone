package Ozone.UI;


import Atom.Reflect.Reflect;
import Ozone.Commands.Commands;
import Ozone.Manifest;
import arc.Core;
import arc.input.KeyCode;
import arc.scene.ui.TextField;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;

public class OzoneMenu extends BaseDialog {
    private TextField commandsField;
    private String commands = "";

    public OzoneMenu(String title, DialogStyle style) {
        super(title, style);
        this.keyDown((key) -> {
            if (key == KeyCode.escape || key == KeyCode.back) {
                Core.app.post(this::hide);
            } else if (key == KeyCode.enter) {
                Commands.call(commands);
                commands = "";
                commandsField.clearText();
            }
        });
        this.shown(this::setup);
        this.onResize(this::setup);
    }

    @Override
    public void hide() {
        super.hide();
        try {
            if (!Vars.ui.hudfrag.shown())
                Reflect.getMethod(null, "toggleMenus", Vars.ui.hudfrag).invoke(Vars.ui.hudfrag);
        } catch (Throwable ignored) {
        }
    }

    public void setup() {
        cont.top();
        cont.clear();
        cont.button(Core.bundle.get("ozone.javaEditor"), Icon.pencil, () -> {
            Core.app.post(this::hide);
            try {
                if (!Vars.ui.hudfrag.shown())
                    Reflect.getMethod(null, "toggleMenus", Vars.ui.hudfrag).invoke(Vars.ui.hudfrag);
            } catch (Throwable ignored) {
            }
            Manifest.javaEditor.show();
        })
                .size(Core.graphics.getWidth() / 6, Core.graphics.getHeight() / 12);
        cont.row();
        cont.table((s) -> {
            s.left();
            s.label(() -> Core.bundle.get("Commands") + ": ");
            commandsField = s.field(commands, (res) -> commands = res).fillX().growX().get();
            s.button(Icon.zoom, () -> {
                //Commands.call(Settings.commandsPrefix + commands);
                Commands.call(commands);
                commands = "";
                commandsField.clearText();
            });
        }).growX().fillX().padBottom(6.0F).bottom().size(Core.graphics.getWidth(), Core.graphics.getHeight() / 12);
        ;


        try {
            if (Vars.ui.hudfrag.shown())
                Reflect.getMethod(null, "toggleMenus", Vars.ui.hudfrag).invoke(Vars.ui.hudfrag);
        } catch (Throwable ignored) {
        }
    }
}
