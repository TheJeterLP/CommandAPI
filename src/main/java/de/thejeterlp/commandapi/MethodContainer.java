/*
 * Copyright (C) 2019 TheJeterLP
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.thejeterlp.commandapi;

import de.thejeterlp.commandapi.BaseCommand.Sender;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;

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
