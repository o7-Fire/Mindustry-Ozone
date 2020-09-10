package Ozone.Patch;

import Ozone.Manifest;
import arc.scene.ui.layout.Table;
import mindustry.gen.Icon;
import mindustry.ui.Styles;

import static mindustry.Vars.ui;

public class DesktopInput extends mindustry.input.DesktopInput {
    @Override
    public void buildPlacementUI(Table table) {
        super.buildPlacementUI(table);
        table.button(Icon.settings, Styles.clearPartiali, () -> {
            Manifest.menu.show();
        }).tooltip("Ozone Menu");
    }
}
