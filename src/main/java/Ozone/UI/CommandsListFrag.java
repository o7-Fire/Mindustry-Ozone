package Ozone.UI;

import Atom.Utility.Random;
import Garbage.Settings;
import Ozone.Commands.Commands;
import Ozone.Commands.Task.CommandsSpam;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.scene.Group;
import arc.scene.event.Touchable;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.Styles;
import mindustry.ui.fragments.Fragment;

import java.util.Map;

import static mindustry.Vars.*;

//UI madness
public class CommandsListFrag extends Fragment {
    public boolean visible = false;
    private Table logs = new Table().marginRight(30f).marginLeft(20f);
    private Table content = new Table().marginRight(30f).marginLeft(20f);
    private float h = 70F;
    private TextField sField;
    private String commands = "";


    @Override
    public void build(Group parent) {

        parent.fill(cont -> {
            cont.visible(() -> visible);
            cont.update(() -> {
                if (!(net.active() && !state.is(GameState.State.menu))) {
                    visible = false;
                }
            });


            cont.table(Tex.buttonTrans, pane -> {
                pane.labelWrap(Commands.commandsList.size() + " Commands in total").marginLeft(20);
                pane.row();
                sField = pane.field(commands, (res) -> commands = res).fillX().growX().get();
                pane.button(Icon.exchange, () -> Vars.ui.showTextInput("Commands", "How many times you want to run this",
                        2, "1", true, c -> Vars.ui.showTextInput("Commands", "Delay ? in tick, 10 frame is the lowest standard, 1 frame if you persist",
                                6, "100", true, d -> Commands.commandsQueue.addLast(new CommandsSpam(c, d, commands)))));
                pane.row();
                pane.pane(content).grow().get().setScrollingDisabled(true, false);
                pane.row();
                pane.table(menu -> {
                    menu.defaults().growX().height(50f).fillY();
                    menu.button(Core.bundle.get("close"), this::toggle);
                }).margin(0f).pad(15f).growX();

            }).touchable(Touchable.enabled).margin(14f);
        });
        rebuild();
    }

    public void rebuild() {
        content.clear();
        for (Map.Entry<String, Commands.Command> cl : Commands.commandsList.entrySet()) {
            if (cl.getValue().icon == null) continue;
            String name = (Settings.colorPatch ? "[" + Random.getRandomHexColor() + "]" : "") + cl.getKey().replace("-", " ") + "[white]";
            Table button = new Table();
            button.left();
            button.margin(5).marginBottom(10);

            Table table = new Table() {
                @Override
                public void draw() {
                    super.draw();
                    Draw.color(Color.valueOf(Random.getRandomHexColor()));
                    Draw.alpha(parentAlpha);
                    Lines.stroke(Scl.scl(3f));
                    Lines.rect(x, y, width, height);
                    Draw.reset();
                }
            };
            if (cl.getValue().icon != null)
                table.image(cl.getValue().icon).fontScale(3.8f).center().grow();
            button.add(table).size(h);
            button.label(() -> cl.getValue().description);
            try {
                button.row();
                button.label(() -> name);
                button.button(Icon.settings, Styles.clearPartiali, () -> ui.showConfirm(name, "are you sure want to run commands: " + name, () -> {
                    String com = cl.getKey();
                    Commands.call(Settings.commandsPrefix + com);
                }));

            } catch (Throwable a) {
                ui.showException(a);
            }
            content.add(button).padBottom(-6).width(350f).maxHeight(h + 14);
            content.row();
            if (Settings.colorPatch)
                content.image().height(4f).color(Color.valueOf(Random.getRandomHexColor())).growX();
            else
                content.image().height(4f).color(Color.gray).growX();
            content.row();
        }


        content.marginBottom(5);
    }


    public void toggle() {
        visible = !visible;
        if (visible) {
            rebuild();
        } else {
            sField.clearText();
            Core.scene.setKeyboardFocus(null);
        }
    }

}
