package Ozone.Patch;


import Atom.Time.Countdown;
import Atom.Utility.Utility;
import Ozone.Commands.Commands;
import Ozone.Settings;
import arc.Core;
import arc.Input;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.GlyphLayout;
import arc.math.Mathf;
import arc.scene.Element;
import arc.scene.Group;
import arc.scene.ui.Label;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Scl;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.input.Binding;
import mindustry.ui.Fonts;
import mindustry.ui.fragments.Fragment;

import java.util.HashMap;

public class ChatFragment extends mindustry.ui.fragments.ChatFragment {

    private Seq<ChatMessage> messages = new Seq<>();
    private float fadetime;
    private boolean shown = false;
    private TextField chatfield;
    private Label fieldlabel = new Label(">");
    private Font font;
    private GlyphLayout layout = new GlyphLayout();
    private float offsetx = Scl.scl(4.0F);
    private float offsety = Scl.scl(4.0F);
    private float fontoffsetx = Scl.scl(2.0F);
    private float chatspace = Scl.scl(50.0F);
    private Color shadowColor = new Color(0.0F, 0.0F, 0.0F, 0.4F);
    private float textspacing = Scl.scl(10.0F);
    private Seq<String> history = new Seq<>();
    private int historyPos = 0;
    private int scrollPos = 0;
    private Fragment container = new Fragment() {
        public void build(Group parent) {
            Core.scene.add(ChatFragment.this);
        }
    };
    private HashMap<String, AntiSpam> antiSpam = new HashMap<>();

    public ChatFragment() {
        this.setFillParent(true);
        this.font = Fonts.def;
        this.visible(() -> {
            if (!Vars.net.active() && this.messages.size > 0) {
                this.clearMessages();
                if (this.shown) {
                    this.hide();
                }
            }

            return Vars.net.active();
        });
        this.update(() -> {
            if (Vars.net.active() && Core.input.keyTap(Binding.chat) && (Core.scene.getKeyboardFocus() == this.chatfield || Core.scene.getKeyboardFocus() == null || Vars.ui.minimapfrag.shown()) && !Vars.ui.scriptfrag.shown()) {
                this.toggle();
            }

            if (this.shown) {
                if (Core.input.keyTap(Binding.chat_history_prev) && this.historyPos < this.history.size - 1) {
                    if (this.historyPos == 0) {
                        this.history.set(0, this.chatfield.getText());
                    }

                    ++this.historyPos;
                    this.updateChat();
                }

                if (Core.input.keyTap(Binding.chat_history_next) && this.historyPos > 0) {
                    --this.historyPos;
                    this.updateChat();
                }

                this.scrollPos = (int) Mathf.clamp((float) this.scrollPos + Core.input.axis(Binding.chat_scroll), 0.0F, (float) Math.max(0, this.messages.size - 10));
            }

        });
        this.history.insert(0, "");
        this.setup();
    }

    @Override
    public Fragment container() {
        return this.container;
    }

    @Override
    public void clearMessages() {
        antiSpam.clear();
        this.messages.clear();
        this.history.clear();
        this.history.insert(0, "");
    }

    private void setup() {
        this.fieldlabel.setStyle(new Label.LabelStyle(this.fieldlabel.getStyle()));
        this.fieldlabel.getStyle().font = this.font;
        this.fieldlabel.setStyle(this.fieldlabel.getStyle());
        this.chatfield = new TextField("", new TextField.TextFieldStyle((TextField.TextFieldStyle) Core.scene.getStyle(TextField.TextFieldStyle.class)));
        this.chatfield.setMaxLength(150);
        this.chatfield.getStyle().background = null;
        this.chatfield.getStyle().font = Fonts.chat;
        this.chatfield.getStyle().fontColor = Color.white;
        this.chatfield.setStyle(this.chatfield.getStyle());
        this.bottom().left().marginBottom(this.offsety).marginLeft(this.offsetx * 2.0F).add(this.fieldlabel).padBottom(6.0F);
        this.add(this.chatfield).padBottom(this.offsety).padLeft(this.offsetx).growX().padRight(this.offsetx).height(28.0F);
        if (Vars.mobile) {
            this.marginBottom(105.0F);
            this.marginRight(240.0F);
        }

    }

    @Override
    public void draw() {
        float opacity = (float) Core.settings.getInt("chatopacity") / 100.0F;
        float textWidth = Math.min((float) Core.graphics.getWidth() / 1.5F, Scl.scl(700.0F));
        Draw.color(this.shadowColor);
        if (this.shown) {
            Fill.crect(this.offsetx, this.chatfield.y, this.chatfield.getWidth() + 15.0F, this.chatfield.getHeight() - 1.0F);
        }

        super.draw();
        float spacing = this.chatspace;
        this.chatfield.visible = this.shown;
        this.fieldlabel.visible = this.shown;
        Draw.color(this.shadowColor);
        Draw.alpha(this.shadowColor.a * opacity);
        float theight = this.offsety + spacing + this.getMarginBottom();

        for (int i = this.scrollPos; i < this.messages.size && i < 10 + this.scrollPos && ((float) i < this.fadetime || this.shown); ++i) {
            this.layout.setText(this.font, ((ChatMessage) this.messages.get(i)).formattedMessage, Color.white, textWidth, 12, true);
            theight += this.layout.height + this.textspacing;
            if (i - this.scrollPos == 0) {
                theight -= this.textspacing + 1.0F;
            }

            this.font.getCache().clear();
            this.font.getCache().addText(((ChatMessage) this.messages.get(i)).formattedMessage, this.fontoffsetx + this.offsetx, this.offsety + theight, textWidth, 12, true);
            if (!this.shown && this.fadetime - (float) i < 1.0F && this.fadetime - (float) i >= 0.0F) {
                this.font.getCache().setAlphas((this.fadetime - (float) i) * opacity);
                Draw.color(0.0F, 0.0F, 0.0F, this.shadowColor.a * (this.fadetime - (float) i) * opacity);
            } else {
                this.font.getCache().setAlphas(opacity);
            }

            Fill.crect(this.offsetx, theight - this.layout.height - 2.0F, textWidth + Scl.scl(4.0F), this.layout.height + this.textspacing);
            Draw.color(this.shadowColor);
            Draw.alpha(opacity * this.shadowColor.a);
            this.font.getCache().draw();
        }

        Draw.color();
        if (this.fadetime > 0.0F && !this.shown) {
            this.fadetime -= Time.delta / 180.0F;
        }

    }

    private void sendMessage() {
        String message = this.chatfield.getText();
        this.clearChatInput();
        if (!message.replace(" ", "").isEmpty()) {
            if (Commands.call(message)) return;
            this.history.insert(1, message);
            Call.sendChatMessage(message);
        }
    }

    @Override
    public void toggle() {
        if (!this.shown) {
            Core.scene.setKeyboardFocus(this.chatfield);
            this.shown = !this.shown;
            if (Vars.mobile) {
                Input.TextInput input = new Input.TextInput();
                input.maxLength = 150;
                input.accepted = (text) -> {
                    this.chatfield.setText(text);
                    this.sendMessage();
                    this.hide();
                    Core.input.setOnscreenKeyboardVisible(false);
                };
                input.canceled = this::hide;
                Core.input.getTextInput(input);
            } else {
                this.chatfield.fireClick();
            }
        } else {
            Core.scene.setKeyboardFocus((Element) null);
            this.shown = !this.shown;
            this.scrollPos = 0;
            this.sendMessage();
        }

    }

    @Override
    public void hide() {
        Core.scene.setKeyboardFocus(null);
        this.shown = false;
        this.clearChatInput();
    }

    @Override
    public void updateChat() {
        this.chatfield.setText(this.history.get(this.historyPos));
        this.chatfield.setCursorPosition(this.chatfield.getText().length());
    }

    @Override
    public void clearChatInput() {
        this.historyPos = 0;
        this.history.set(0, "");
        this.chatfield.setText("");
    }

    @Override
    public boolean shown() {
        return this.shown;
    }

    @Override
    public void addMessage(String message, String sender) {
        ChatMessage cm = new ChatMessage(message, sender);
        if (Settings.antiSpam) {
            if (antiSpam.containsKey(sender)) {
                AntiSpam victim = antiSpam.get(sender);
                ChatMessage message1 = messages.get(0);
                String filter = victim.filter(message);
                if (!filter.isEmpty()) {
                    if (message1.message.equals(victim.lastMessage)) messages.remove(0);
                    messages.insert(0, new ChatMessage(filter, sender, victim.reasons));
                } else this.messages.insert(0, cm);
            } else {
                antiSpam.put(sender, new AntiSpam(sender));
                this.messages.insert(0, cm);
                ++this.fadetime;
                this.fadetime = Math.min(this.fadetime, 10.0F) + 1.0F;
                if (this.scrollPos > 0) {
                    ++this.scrollPos;
                }
            }
            antiSpam.get(sender).setLastMessage(cm.message);
        } else {
            this.messages.insert(0, cm);
            ++this.fadetime;
            this.fadetime = Math.min(this.fadetime, 10.0F) + 1.0F;
            if (this.scrollPos > 0) {
                ++this.scrollPos;
            }
        }

    }

    private static class AntiSpam {
        public static int maxRepeatingChar = 100;
        public static long rateLimit = 300;
        public final String sender;
        public String lastMessage = "";
        public int lastMessageTimes = 1;
        public String reasons = "";
        private long lastMessageSent = 0;

        public AntiSpam(String name) {
            sender = name;
        }

        public void setLastMessage(String message) {
            if (message.equalsIgnoreCase(lastMessage)) lastMessageTimes++;
            else {
                lastMessageTimes = 1;
                lastMessage = message;
            }
            lastMessageSent = System.currentTimeMillis();
        }

        public String filter(String message) {
            if ((message.length() - Utility.shrinkString(message).length()) > maxRepeatingChar) {
                reasons = "Duplicate String from: " + message.length() + " to " + Utility.shrinkString(message).length();
            }
            if (message.equalsIgnoreCase(lastMessage)) {
                reasons = "Spam Last Message: " + lastMessageTimes;
            } else if ((System.currentTimeMillis() - lastMessageSent) > rateLimit) {
                reasons = "Too Fast: " + Countdown.result(lastMessageSent);
            } else reasons = "";

            return message;
        }
    }

    private static class ChatMessage {
        public final String sender;
        public final String message;
        public final String formattedMessage;

        public ChatMessage(String message, String sender) {
            this.message = message;
            this.sender = sender;
            if (sender == null) {
                this.formattedMessage = message;
            } else {
                this.formattedMessage = "[coral][[" + sender + "[coral]]:[white] " + message;
            }

        }

        public ChatMessage(String message, String sender, String antiSpam) {
            this.message = message;
            this.sender = sender;
            if (sender == null) {
                this.formattedMessage = message;
            } else {
                this.formattedMessage = "[royal][AntiSpam][white]" + antiSpam + "\n[coral][[" + sender + "[coral]]:[white] " + message;
            }

        }
    }
}
