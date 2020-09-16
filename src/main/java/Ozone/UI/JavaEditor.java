package Ozone.UI;

import Atom.Runtime.RuntimeClass;
import Atom.Runtime.RuntimeSource;
import arc.Core;
import arc.scene.ui.Label;
import arc.scene.ui.TextArea;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import javassist.compiler.CompileError;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public class JavaEditor extends BaseDialog {
    public static int pad = 8;
    private volatile RuntimeSource rs = null;
    private volatile RuntimeClass rc = null;
    public ArrayList<String> messages = new ArrayList<>();
    private HashMap<String, String> color = new HashMap<>();
    private TextArea a;
    private Label label;

    public JavaEditor(String name, DialogStyle style) {
        super(name, style);
        shouldPause = true;
        addCloseButton();
        shown(this::setup);
        update(this::updateChat);
        onResize(this::setup);
        //update(this::updateColor);
        addColor();
    }

    void Run() {
        try {
        rs.AssignSourceCode(a.getText().replace("\r", "\n"));
        rs.compile(new OutputStream() {
                @Override
                public void write(int b) {

                }
        });
        setup();
        } catch (Throwable t) {
           Vars.ui.showException("Compiler Error", t);
        }
    }

    void setup() {
        cont.clear();
        label = null;
        if (rs == null) {
            AtomicReference<String> name = new AtomicReference<>();
            AtomicReference<String> packages = new AtomicReference<>();
            Pattern pattern = Pattern.compile(" [~#@*+%{}<>\\[\\]|\"\\_^]");
            packages.set("Atom.Test");
            name.set("Test");
            Table table = new Table();
            table.add("Class Name: ");
            table.field(name.get(), name::set).growX();
            table.row();
            table.add("Package: ");
            table.field(packages.get(), packages::set).growX();
            table.row();
            table.button("save", Styles.clearPartialt, () -> {
                if (name.get().isEmpty())
                    Vars.ui.showInfo("Class name must not empty");
                else if (packages.get().isEmpty())
                    Vars.ui.showInfo("Package must not empty");
                else if (pattern.matcher(name.get()).find())
                    Vars.ui.showInfo("Name contain illegal character");
                else if (pattern.matcher(packages.get()).find())
                    Vars.ui.showInfo("Package contain illegal character");
                else {
                    rs = new RuntimeSource(packages.get(), name.get());
                    table.clear();
                    setup();

                }
            }).size(Core.graphics.getWidth() / 8, Core.graphics.getHeight() / 12);
            cont.row();
            cont.add(table);
        } else {
            buttons.clear();
            addCloseButton();
            buttons.button("Reformat/CheckCode", Icon.book, this::setup);
            buttons.button("Run", Icon.box, this::Run);
            buttons.button("Save", Icon.save, () -> Vars.platform.showFileChooser(false, ".java", fi -> {
                try {
                    rs.save(fi.file());
                } catch (IOException e) {
                    Vars.ui.showException("Failed to save", e);
                }
            }));
            buttons.button("Open", Icon.file, () -> Vars.platform.showFileChooser(true, ".java", fi -> rs = new RuntimeSource(fi.file())));
            if (a != null)
                try {
                    a = new TextArea(new Formatter().formatSource(a.getText()));
                } catch (FormatterException ignored) {
                    Log.err(ignored.getMessage());
                    Vars.ui.showException(ignored);
                }
            else
                a = new TextArea(rs.toString().replace("\n", "\r"));
            label = new Label("");
            label.setStyle(new Label.LabelStyle(label.getStyle()));
            label.getStyle().font = Fonts.def;
            label.setStyle(label.getStyle());
            label.getText();

            cont.add(a).size(Core.graphics.getWidth(), Core.graphics.getHeight() - 256);
            cont.row();
            cont.add(label).size(Core.graphics.getWidth(), 156);
        }
    }

    public void updateChat() {
        if (label == null)
            return;
        StringBuilder sb = new StringBuilder();
        for (String s : messages) {
            sb.append(s);
            sb.append("\n");
        }
        label.setText(sb.toString());
    }

    public void updateColor(){
        if(a == null)
            return;
        ArrayList<String> code = new ArrayList<>(Arrays.asList(a.getText().replace("\r", "\n").split(" ")));
        StringBuilder sb = new StringBuilder();
        boolean contain = false;
        for(String s : code){
            if(color.containsKey(s)) {
                sb.append("[").append(color.get(s)).append("]");
                contain = true;
            }
            sb.append(s);
            sb.append("[white]");
            sb.append(" ");
        }
        if(!contain)
            return;
        try {
            a.setText(new Formatter().formatSource(sb.toString().replace("\n", "\r")));
            return;
        } catch (FormatterException ignored) { }
        a.setText(sb.toString().replace("\n", "\r"));
    }

    public void addColor(){
        color.clear();
        color.put("class", "purple");
        color.put("static", "purple");
        color.put("void", "purple");
        color.put("int", "purple");
        color.put("new", "purple");
        color.put("if", "purple");
        color.put("else", "purple");
        color.put("try", "purple");
        color.put("catch", "purple");
        color.put("return", "purple");
        color.put("for", "purple");
        color.put("throw", "purple");
        color.put("public", "orange");
        color.put("private", "orange");
        color.put("protected", "orange");
        color.put("import", "orange");
        color.put("package", "orange");
        color.put("true", "orange");
        color.put("false", "orange");
        color.put("null", "orange");
        color.put("gaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaae", "white");
    }

}
