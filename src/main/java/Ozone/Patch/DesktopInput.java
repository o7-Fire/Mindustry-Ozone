package Ozone.Patch;

import Ozone.Manifest;
import arc.scene.ui.layout.Table;
import mindustry.gen.Icon;
import mindustry.ui.Styles;

public class DesktopInput extends mindustry.input.DesktopInput {
    @Override
    public void buildPlacementUI(Table table) {
       super.buildPlacementUI(table);
        table.button(Icon.settings, Styles.colori, () -> Manifest.menu.show()).tooltip("@ozone.menu");
    }
}
