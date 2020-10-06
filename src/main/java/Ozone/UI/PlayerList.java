package Ozone.UI;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.scene.Element;
import arc.scene.Group;
import arc.scene.event.Touchable;
import arc.scene.ui.Image;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.util.Interval;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Structs;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.net.NetConnection;
import mindustry.net.Packets;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.AdminsDialog;
import mindustry.ui.dialogs.BansDialog;
import mindustry.ui.fragments.Fragment;

import java.util.Objects;

public class PlayerList extends Fragment {
    public Table content = (new Table()).marginRight(13.0F).marginLeft(13.0F);
    private boolean visible = false;
    private Interval timer = new Interval();
    private TextField sField;
    private boolean found = false;

    public void build(Group parent) {
        parent.fill((cont) -> {
            cont.visible(() -> {
                return this.visible;
            });
            cont.update(() -> {
                if (Vars.net.active() && Vars.state.isGame()) {
                    if (this.visible && this.timer.get(20.0F)) {
                        this.rebuild();
                        this.content.pack();
                        this.content.act(Core.graphics.getDeltaTime());
                        Core.scene.act(0.0F);
                    }

                } else {
                    this.visible = false;
                }
            });
            cont.table(Tex.buttonTrans, (pane) -> {
                pane.label(() -> {
                    return Core.bundle.format(Groups.player.size() == 1 ? "players.single" : "players", new Object[]{Groups.player.size()});
                });
                pane.row();
                this.sField = (TextField) pane.field((String) null, (text) -> {
                    this.rebuild();
                }).grow().pad(8.0F).get();
                this.sField.setMaxLength(40);
                this.sField.setMessageText(Core.bundle.format("players.search", new Object[0]));
                pane.row();
                ((ScrollPane) pane.pane(this.content).grow().get()).setScrollingDisabled(true, false);
                pane.row();
                pane.table((menu) -> {
                    menu.defaults().growX().height(50.0F).fillY();
                    BansDialog var10002 = Vars.ui.bans;
                    Objects.requireNonNull(var10002);
                    menu.button("@server.bans", var10002::show).disabled((b) -> {
                        return Vars.net.client();
                    });
                    AdminsDialog var2 = Vars.ui.admins;
                    Objects.requireNonNull(var2);
                    menu.button("@server.admins", var2::show).disabled((b) -> {
                        return Vars.net.client();
                    });
                    menu.button("@close", this::toggle);
                }).margin(0.0F).pad(10.0F).growX();
            }).touchable(Touchable.enabled).margin(14.0F);
        });
        this.rebuild();
    }

    public void rebuild() {
        this.content.clear();
        float h = 74.0F;
        this.found = false;
        Groups.player.sort(Structs.comparing(Player::team));
        Groups.player.each((user) -> {
            this.found = true;
            NetConnection connection = user.con;
            if (connection != null || !Vars.net.server() || user.isLocal()) {
                if (this.sField.getText().length() <= 0 || user.name().toLowerCase().contains(this.sField.getText().toLowerCase()) || Strings.stripColors(user.name().toLowerCase()).contains(this.sField.getText().toLowerCase())) {
                    Table button = new Table();
                    button.left();
                    button.margin(5.0F).marginBottom(10.0F);
                    Table table = new Table() {
                        public void draw() {
                            super.draw();
                            Draw.color(Pal.gray);
                            Draw.alpha(this.parentAlpha);
                            Lines.stroke(Scl.scl(4.0F));
                            Lines.rect(this.x, this.y, this.width, this.height);
                            Draw.reset();
                        }
                    };
                    table.margin(8.0F);
                    table.add((new Image(user.icon())).setScaling(Scaling.bounded)).grow();
                    button.add(table).size(h);
                    button.labelWrap("[#" + user.color().toString().toUpperCase() + "]" + user.name()).width(170.0F).pad(10.0F);
                    button.add().grow();
                    ((Image) button.image(Icon.admin).visible(() -> {
                        return user.admin && (user.isLocal() || !Vars.net.server());
                    }).padRight(5.0F).get()).updateVisibility();
                    if ((Vars.net.server() || Vars.player.admin) && !user.isLocal() && (!user.admin || Vars.net.server())) {
                        button.add().growY();
                        float bs = h / 2.0F;
                        button.table((t) -> {
                            t.defaults().size(bs);
                            t.button(Icon.hammer, Styles.clearPartiali, () -> {
                                Vars.ui.showConfirm("@confirm", Core.bundle.format("confirmban", new Object[]{user.name()}), () -> {
                                    Call.adminRequest(user, Packets.AdminAction.ban);
                                });
                            });
                            t.button(Icon.cancel, Styles.clearPartiali, () -> {
                                Vars.ui.showConfirm("@confirm", Core.bundle.format("confirmkick", new Object[]{user.name()}), () -> {
                                    Call.adminRequest(user, Packets.AdminAction.kick);
                                });
                            });
                            t.row();
                            t.button(Icon.admin, Styles.clearTogglePartiali, () -> {
                                if (!Vars.net.client()) {
                                    String id = user.uuid();
                                    if (Vars.netServer.admins.isAdmin(id, connection.address)) {
                                        Vars.ui.showConfirm("@confirm", Core.bundle.format("confirmunadmin", new Object[]{user.name()}), () -> {
                                            Vars.netServer.admins.unAdminPlayer(id);
                                        });
                                    } else {
                                        Vars.ui.showConfirm("@confirm", Core.bundle.format("confirmadmin", new Object[]{user.name()}), () -> {
                                            Vars.netServer.admins.adminPlayer(id, user.usid());
                                        });
                                    }

                                }
                            }).update((b) -> {
                                b.setChecked(user.admin);
                            }).disabled((b) -> {
                                return Vars.net.client();
                            }).touchable(() -> {
                                return Vars.net.client() ? Touchable.disabled : Touchable.enabled;
                            }).checked(user.admin);
                            t.button(Icon.zoom, Styles.clearPartiali, () -> {
                                Call.adminRequest(user, Packets.AdminAction.trace);
                            });
                        }).padRight(12.0F).size(bs + 10.0F, bs);
                    } else if (!user.isLocal() && !user.admin && Vars.net.client() && Groups.player.size() >= 3 && Vars.player.team() == user.team()) {
                        button.add().growY();
                        button.button(Icon.hammer, Styles.clearPartiali, () -> {
                            Vars.ui.showConfirm("@confirm", Core.bundle.format("confirmvotekick", new Object[]{user.name()}), () -> {
                                Call.sendChatMessage("/votekick " + user.name());
                            });
                        }).size(h);
                    }

                    this.content.add(button).padBottom(-6.0F).width(350.0F).maxHeight(h + 14.0F);
                    this.content.row();
                    this.content.image().height(4.0F).color(Vars.state.rules.pvp ? user.team().color : Pal.gray).growX();
                    this.content.row();
                }
            }
        });
        if (!this.found) {
            this.content.add(Core.bundle.format("players.notfound", new Object[0])).padBottom(6.0F).width(350.0F).maxHeight(h + 14.0F);
        }

        this.content.marginBottom(5.0F);
    }

    public void toggle() {
        this.visible = !this.visible;
        if (this.visible) {
            this.rebuild();
        } else {
            Core.scene.setKeyboardFocus((Element) null);
            this.sField.clearText();
        }

    }
}
