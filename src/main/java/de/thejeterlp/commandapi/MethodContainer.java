package de.thejeterlp.commandapi;

import de.thejeterlp.commandapi.BaseCommand.Sender;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author TheJeterLP
 */
public class MethodContainer {

    private final HashMap<Sender, Method> methods;

    public MethodContainer(HashMap<Sender, Method> map) {
        methods = map;
    }
      
    protected Method getMethod(Sender s) {
        return methods.get(s);
    }

    protected Collection<Method> getMethods() {
        return methods.values();
    }

    protected HashMap<Sender, Method> getMethodMap() {
        return methods;
    }
    
    

}
