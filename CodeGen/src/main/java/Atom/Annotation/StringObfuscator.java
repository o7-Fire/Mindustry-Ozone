package Atom.Annotation;


import lombok.Data;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

@Data
public class StringObfuscator extends AtomProcessor {
    private String parent = "Ozone";

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> h = new HashSet<>();
        h.add(ObfuscatorEntryPoint.class.getTypeName());
        return h;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (annotations.isEmpty()) return false;
        Set<? extends Element> s = roundEnv.getRootElements();
        for (javax.lang.model.element.Element a : s) {


        }
        info("oh yes");
        info("gabe ich");
        info(roundEnv.toString());
        return true;
    }


}
