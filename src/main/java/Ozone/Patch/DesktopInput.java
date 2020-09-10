package Ozone.Patch;

import Ozone.Manifest;
import arc.scene.ui.layout.Table;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;

import static mindustry.Vars.*;
import static mindustry.Vars.player;


public class DesktopInput extends mindustry.input.DesktopInput {
    @Override
    public void buildPlacementUI(Table table) {
        table.image().color(Pal.gray).height(4f).colspan(4).growX();
        table.row();
        table.left().margin(0f).defaults().size(48f).left();

        table.button(Icon.paste, Styles.clearPartiali, () -> {
            ui.schematics.show();
        }).tooltip("@schematics");

        table.button(Icon.settings, Styles.clearPartiali, () -> {
            Manifest.menu.show();
        }).tooltip("Ozone Menu");

        table.button(Icon.tree, Styles.clearPartiali, () -> {
            ui.research.show();
        }).visible(() -> state.isCampaign()).tooltip("@research");

        table.button(Icon.map, Styles.clearPartiali, () -> {
            ui.planet.show();
        }).visible(() -> state.isCampaign()).tooltip("@planetmap");

        table.button(Icon.up, Styles.clearPartiali, () -> {
            ui.planet.show(state.getSector(), player.team().core());
        }).visible(() -> state.isCampaign())
                .disabled(b -> player.team().core() == null || !player.team().core().items.has(player.team().core().block.requirements)).tooltip("@launchcore");


    }
}
