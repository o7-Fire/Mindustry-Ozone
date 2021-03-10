package mindustry.ui.dialogs;

import Atom.Reflect.Reflect;
import arc.func.Cons;
import arc.graphics.Color;
import arc.scene.ui.Dialog;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import mindustry.gen.Tex;
import mindustry.ui.Styles;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;

import static mindustry.Vars.player;
import static mindustry.Vars.playerColors;

public class PaletteDialog extends Dialog {
	private static ArrayList<Field> colors = new ArrayList<>();
	protected int i = 0;
	HashSet<Color> already = new HashSet<>();
	private Cons<Color> cons;
	
	public PaletteDialog() {
		super("");
		colors = Reflect.findDeclaredField(Color.class, f -> {
			return f.getType().equals(Color.class);
		});
		build();
	}
	
	private void build() {
		cont.table(table -> {
			for (Color c : playerColors)
				addColor(c, table);
			for (Field f : colors) {
				try {
					addColor((Color) f.get(null), table);
				}catch (Throwable ignored) {}
			}
		});
		
		closeOnBack();
	}
	
	protected void addColor(Color color, Table table) {
		if (already.contains(color)) return;
		already.add(color);
		ImageButton button = table.button(Tex.whiteui, Styles.clearTogglei, 34, () -> {
			cons.get(color);
			hide();
		}).size(48).get();
		button.setChecked(player.color().equals(color));
		button.getStyle().imageUpColor = color;
		
		if (i % 16 == 0) {
			table.row();
		}
		i++;
	}
	
	public void show(Cons<Color> cons) {
		this.cons = cons;
		show();
	}
}
