package Atom.Annotation;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;

public abstract class AtomProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;

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
