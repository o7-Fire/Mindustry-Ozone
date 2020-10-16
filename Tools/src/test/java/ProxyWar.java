import com.commentremover.app.CommentProcessor;
import com.commentremover.app.CommentRemover;

import java.io.File;

public class ProxyWar {

    public static void main(String[] args) throws Throwable {
        File test = new File("/Test/test/java/Obfuscate.java");
        CommentRemover commentRemover = new CommentRemover.CommentRemoverBuilder()
                .removeJava(true) // Remove Java file Comments....
                .removeJavaScript(true) // Remove JavaScript file Comments....
                .removeJSP(true) // etc..
                .removeTodos(true) // Remove todos
                .removeSingleLines(false) // Do not remove single line type comments
                .removeMultiLines(true) // Remove multiple type comments
                .preserveJavaClassHeaders(false) // Preserves class header comment
                .preserveCopyRightHeaders(false) // Preserves copyright comment
                .startExternalPath(test.getAbsolutePath())// Give it full path for external directories
                .setExcludePackages(new String[]{"src.main.java.model"}) // Refers to /Users/user/Projects/MyOtherProject/src/main/java/model and skips this directory.
                .build();

        CommentProcessor commentProcessor = new CommentProcessor(commentRemover);
        commentProcessor.start();
    }
}
