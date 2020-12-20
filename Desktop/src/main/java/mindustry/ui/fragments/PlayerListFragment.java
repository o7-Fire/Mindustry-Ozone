package mindustry.ui.fragments;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.scene.Group;
import arc.scene.event.Touchable;
import arc.scene.ui.Image;
import arc.scene.ui.Label;
import arc.scene.ui.SettingsDialog;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.util.Interval;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Structs;
import io.sentry.Sentry;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.net.NetConnection;
import mindustry.net.Packets.AdminAction;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import java.lang.reflect.Field;

import static mindustry.Vars.*;

public class PlayerListFragment extends Fragment {
	public Table content = new Table().marginRight(13f).marginLeft(13f);
	public Settings settings = new Settings("Player List Settings");
	private boolean visible = false;
	private Interval timer = new Interval();
	private TextField sField;
	private boolean found = false;
	
	@Override
	public void build(Group parent) {
		content.name = "players";
		parent.fill(cont -> {
			cont.name = "playerlist";
			cont.visible(() -> visible);
			cont.update(() -> {
				if (!(net.active() && state.isGame())) {
					visible = false;
					return;
				}
				
				if (visible && timer.get(20)) {
					rebuild();
					content.pack();
					content.act(Core.graphics.getDeltaTime());
					//hacky
					Core.scene.act(0f);
				}
			});
			
			cont.table(Tex.buttonTrans, pane -> {
				pane.label(() -> Core.bundle.format(Groups.player.size() == 1 ? "players.single" : "players", Groups.player.size()));
				pane.row();
				sField = pane.field(null, text -> {
					rebuild();
				}).grow().pad(8).get();
				sField.name = "search";
				sField.setMaxLength(maxNameLength);
				sField.setMessageText(Core.bundle.format("players.search"));
				
				pane.row();
				pane.pane(content).grow().get().setScrollingDisabled(true, false);
				pane.row();
				
				pane.table(menu -> {
					menu.defaults().growX().height(50f).fillY();
					menu.name = "menu";
					menu.button("Settings", settings::show).disabled(b -> !net.client());
					menu.button("@server.bans", ui.bans::show).disabled(b -> net.client());
					menu.button("@server.admins", ui.admins::show).disabled(b -> net.client());
					menu.button("@close", this::toggle);
				}).margin(0f).pad(10f).growX();
				
			}).touchable(Touchable.enabled).margin(14f);
		});
		
		rebuild();
	}
	
	public void rebuild() {
		content.clear();
		
		float h = 74f;
		found = false;
		
		Groups.player.sort(Structs.comparing(Player::team));
		Groups.player.each(user -> {
			found = true;
			NetConnection connection = user.con;
			
			if (connection == null && net.server() && !user.isLocal()) return;
			if (sField.getText().length() > 0 && !user.name().toLowerCase().contains(sField.getText().toLowerCase()) && !Strings.stripColors(user.name().toLowerCase()).contains(sField.getText().toLowerCase()))
				return;
			
			Table button = new Table();
			button.left();
			button.margin(5).marginBottom(10);
			
			Table table = new Table() {
				@Override
				public void draw() {
					super.draw();
					Draw.color(Pal.gray);
					Draw.alpha(parentAlpha);
					Lines.stroke(Scl.scl(4f));
					Lines.rect(x, y, width, height);
					Draw.reset();
				}
			};
			table.margin(8);
			table.add(new Image(user.icon()).setScaling(Scaling.bounded)).grow();
			table.name = user.name();
			
			button.add(table).size(h);
			button.labelWrap("[#" + user.color().toString().toUpperCase() + "]" + user.name()).width(170f).pad(10);
			button.add().grow();
			
			button.image(Icon.admin).visible(() -> user.admin && !(!user.isLocal() && net.server())).padRight(5).get().updateVisibility();
			
			if ((net.server() || player.admin) && !user.isLocal() && (!user.admin || net.server())) {
				button.add().growY();
				
				float bs = (h) / 2f;
				
				button.table(t -> {
					t.defaults().size(bs);
					
					t.button(Icon.hammer, Styles.clearPartiali, () -> ui.showConfirm("@confirm", Core.bundle.format("confirmban", user.name()), () -> Call.adminRequest(user, AdminAction.ban)));
					t.button(Icon.cancel, Styles.clearPartiali, () -> ui.showConfirm("@confirm", Core.bundle.format("confirmkick", user.name()), () -> Call.adminRequest(user, AdminAction.kick)));
					
					t.row();
					
					t.button(Icon.admin, Styles.clearTogglePartiali, () -> {
						if (net.client()) return;
						
						String id = user.uuid();
						
						if (netServer.admins.isAdmin(id, connection.address)) {
							ui.showConfirm("@confirm", Core.bundle.format("confirmunadmin", user.name()), () -> netServer.admins.unAdminPlayer(id));
						}else {
							ui.showConfirm("@confirm", Core.bundle.format("confirmadmin", user.name()), () -> netServer.admins.adminPlayer(id, user.usid()));
						}
					}).update(b -> b.setChecked(user.admin)).disabled(b -> net.client()).touchable(() -> net.client() ? Touchable.disabled : Touchable.enabled).checked(user.admin);
					
					t.button(Icon.zoom, Styles.clearPartiali, () -> Call.adminRequest(user, AdminAction.trace));
					
				}).padRight(12).size(bs + 10f, bs);
			}else if (!user.isLocal() && !user.admin && net.client() && Groups.player.size() >= 3 && player.team() == user.team()) { //votekick
				button.add().growY();
				
				button.button(Icon.hammer, Styles.clearPartiali, () -> {
					ui.showConfirm("@confirm", Core.bundle.format("confirmvotekick", user.name()), () -> {
						Call.sendChatMessage("/votekick " + user.name());
					});
				}).size(h);
			}
			
			content.add(button).padBottom(-6).width(350f).maxHeight(h + 14);
			if (Settings.showPlayerID) content.add(new Label(user.isLocal() ? "ID" : String.valueOf(user.id())));
			if (Settings.showPlayerTyping)
				content.add(new Label(user.isLocal() ? "Typing" : user.typing() ? "[green]True[white]" : "False"));
			if (Settings.showPlayerShooting)
				content.add(new Label(user.isLocal() ? "Shooting" : user.shooting() ? "[green]True[white]" : "False"));
			content.row();
			content.image().height(4f).color(state.rules.pvp ? user.team().color : Pal.gray).growX();
			content.row();
		});
		
		if (!found) {
			content.add(Core.bundle.format("players.notfound")).padBottom(6).width(350f).maxHeight(h + 14);
		}
		
		content.marginBottom(5);
		content.pack();
	}
	
	public void toggle() {
		visible = !visible;
		if (visible) {
			rebuild();
		}else {
			Core.scene.setKeyboardFocus(null);
			sField.clearText();
		}
	}
	
	public static class Settings extends BaseDialog {
		public static boolean showPlayerID = true, showPlayerTyping, showPlayerShooting;
		
		
		public Settings(String title) {
			super(title);
			addCloseButton();
			setup();
			shown(this::setup);
			onResize(this::setup);
		}
		
		public void setup() {
			cont.clear();
			SettingsDialog.SettingsTable table = new SettingsDialog.SettingsTable();
			String t = boolean.class.getName();
			//so many unnecessary try and catch
			
			for (Field f : Settings.class.getDeclaredFields())
				if (f.getType().getName().equals(t)) {
					try {
						table.checkPref(f.getName(), f.getBoolean(null), s -> {
							try {
								f.setBoolean(null, s);
							}catch (IllegalAccessException e) {
								e.printStackTrace();
								Sentry.captureException(e);
							}
						});
						f.setBoolean(null, Core.settings.getBool(f.getName(), f.getBoolean(null)));
					}catch (IllegalAccessException e) {
						e.printStackTrace();
						Sentry.captureException(e);
					}
				}
			cont.add(table);
		}
	}
}
