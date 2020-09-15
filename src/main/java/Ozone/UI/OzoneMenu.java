package Ozone.UI;


import Ozone.Commands.Commands;
import Ozone.Settings;
import arc.scene.ui.TextField;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;

public class OzoneMenu extends BaseDialog {
    private TextField commandsField;
    private String commands = "";

    public OzoneMenu(String title, DialogStyle style) {
        super(title, style);
        addCloseButton();
        this.shown(this::setup);
        this.onResize(this::setup);
    }

    public void setup() {
        cont.bottom();
        cont.clear();
        cont.table((s) -> {
            s.left();
            s.label(() -> "Commands: ");
            commandsField = s.field(commands, (res) -> commands = res).growX().get();
            s.button(Icon.zoom, () -> Commands.call(Settings.commandsPrefix + commands));
        }).fillX().padBottom(6.0F);

    }
}
