package Atom.Annotation;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class AtomProcessor extends AbstractProcessor {

    private static Filer filer;
    private static Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return true;
    }

    public static void write(TypeSpec.Builder builder) throws Exception {
        write(builder, null);
    }

    public static void write(TypeSpec.Builder builder, Seq<String> imports) throws Exception {
        JavaFile file = JavaFile.builder(packageName, builder.build()).skipJavaLangImports(true).build();

        if (imports != null) {
            String rawSource = file.toString();
            ArrayList<String> result = new ArrayList<>();
            for (String s : rawSource.split("\n", -1)) {
                result.add(s);
                if (s.startsWith("package ")) {
                    result.add("");
                    for (String i : imports) {
                        result.add(i);
                    }
                }
            }

            String out =
                    JavaFileObject
            object = filer.createSourceFile(file.packageName + "." + file.typeSpec.name, file.typeSpec.originatingElements.toArray(new Element[0]));
            OutputStream stream = object.openOutputStream();
            stream.write(out.getBytes());
            stream.close();
        } else {
            file.writeTo(filer);
        }
    }

    public void info(CharSequence o) {
        messager.printMessage(Diagnostic.Kind.NOTE, o);
    }

    public void err(CharSequence o) {
        messager.printMessage(Diagnostic.Kind.ERROR, o);
    }

    public void warn(CharSequence o) {
        messager.printMessage(Diagnostic.Kind.WARNING, o);
    }
}
