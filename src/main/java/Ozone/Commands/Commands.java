package Ozone.Commands;

import Atom.Reflect.Reflect;
import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.Vars;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;

public class Commands {

    public static void init(CommandHandler c){
        c.register("task", "AI task", Commands::task);
        Log.infoTag("Ozone", "Commands Center Initialized");
    }
    public static void task(String[] arg){
        if(arg.length == 0){
            tellUser("Task not specified");
            help(new ArrayList<>());
            return;
        }
       ArrayList<Method> methods = Reflect.findDeclaredMethods(Commands.class, s -> s.getName().startsWith(arg[0]));
        if(methods.isEmpty()){
            tellUser("Task not found");
            help(new ArrayList<>());

        }else {
            try {
                ArrayList<String> a = new ArrayList<>();
                for(String s : arg){
                    if(s.equals(arg[0]))continue;
                    a.add(s);
                }
                for (Method m : methods) {
                    if(m.getTypeParameters().length != 1) continue;
                    if(!m.getTypeParameters()[0].getTypeName().equals(ArrayList.class.getTypeName()))continue;
                    m.invoke(Commands.class, a);
                    return;
                }
                tellUser("Task not found");
                tellUser("Possible task: " + methods.toString());
            } catch (IllegalAccessException | InvocationTargetException e) {
                tellUser(e.toString());
            }
        }
    }

    public static void help(ArrayList<String> a){
        ArrayList<Method> methods = Reflect.findDeclaredMethods(Commands.class, s ->{
            TypeVariable<Method>[] h =  s.getTypeParameters();
            if(h.length == 0)return false;
            else if(h.length == 1){
              return h[0].getTypeName().equals(ArrayList.class.getTypeName());
            }
            return false;
        });
        tellUser("Available commands");
        for(Method m : methods)
            tellUser("task " + m.getName());
    }

    public static void move(ArrayList<String> s){

    }

    public static void tellUser(String s){
        Vars.ui.chatfrag.addMessage(s, "[white][[royal]Ozone[white]]");
    }
}
