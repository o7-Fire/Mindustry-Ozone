package Main;

import Ozone.Desktop.OzoneLauncher;
import Ozone.Desktop.SharedBootstrap;
import arc.Files;
import arc.backend.sdl.SdlApplication;
import arc.backend.sdl.SdlConfig;
import arc.backend.sdl.jni.SDL;
import arc.func.Cons;
import arc.util.Strings;
import mindustry.Vars;

//basically its a patcher
public class OzoneMindustry {
    public static long start = System.currentTimeMillis() / 1000;

    public static void main(String[] args) {
        //  new Thread(()-> {
        SharedBootstrap.customBootstrap = true;
        try {
            Vars.loadLogger();
            new SdlApplication(new OzoneLauncher(args), new SdlConfig() {
                {
                    this.title = "Mindustry-Ozone";
                    this.maximized = true;
                    this.stencil = 8;
                    this.width = 900;
                    this.height = 700;
                    this.setWindowIcon(Files.FileType.internal, "icons/path47.png");
                }
            });
        }catch (Throwable var2) {
            var2.printStackTrace();
            handleCrash(var2);
        }
        // }).start();

    }

    static void handleCrash(Throwable e) {
        Cons<Runnable> dialog = Runnable::run;
        boolean badGPU = false;
        String finalMessage = Strings.getFinalMessage(e);
        String total = Strings.getCauses(e).toString();
        if (total.contains("Couldn't create window") || total.contains("OpenGL 2.0 or higher") || total.toLowerCase().contains("pixel format") || total.contains("GLEW") || total.contains("unsupported combination of formats")) {
            dialog.get(() -> {
                message(total.contains("Couldn't create window") ? "A graphics initialization error has occured! Try to update your graphics drivers:\n" + finalMessage : "Your graphics card does not support the right OpenGL features.\nTry to update your graphics drivers. If this doesn't work, your computer may not support Mindustry.\n\nFull message: " + finalMessage);
            });
            badGPU = true;
        }
     /*
        boolean finalBadGPU = badGPU;

        CrashSender.send(e, (file) -> {
            Throwable fc = Strings.getFinalCause(e);
            if (!finalBadGPU) {
                dialog.get(() -> {
                    message("A crash has occured. It has been saved in:\n" + file.getAbsolutePath() + "\n" + fc.getClass().getSimpleName().replace("Exception", "") + (fc.getMessage() == null ? "" : ":\n" + fc.getMessage()));
                });
            }
        });

         */
        SDL.SDL_ShowSimpleMessageBox(SDL.SDL_MESSAGEBOX_ERROR, "Oh Nein", e.toString());
        throw new RuntimeException(e);
    }

    private static void message(String message) {
        SDL.SDL_ShowSimpleMessageBox(16, "oh nein", message);
    }
}
