package Ozone.UI;


import mindustry.ui.dialogs.BaseDialog;

public class OzoneMenu extends BaseDialog {

    public OzoneMenu(String title, DialogStyle style) {
        super(title, style);
        addCloseButton();
        this.shown(this::setup);
        this.onResize(this::setup);
    }

    public void setup() {

    }
}
