package de.thejeterlp.commandapi;

import de.thejeterlp.commandapi.BaseCommand.Sender;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author TheJeterLP
 */
public class CommandManager implements CommandExecutor {

    private final HashMap<BaseCommand, MethodContainer> cmds = new HashMap<>();
    private final CommandMap cmap;
    private final JavaPlugin plugin;
    private final Logger logger;

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        CommandMap map;
        try {
            final Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            map = (CommandMap) f.get(Bukkit.getServer());
        } catch (Exception ex) {
            map = null;
            logger.log(Level.SEVERE, "", ex);
        }

        cmap = map;
    }

    private void registerCommand(String name) {
        if (cmap.getCommand(name) != null) return;
        BukkitCommand cmd = new BukkitCommand(name);
        cmap.register(plugin.getName().toLowerCase(), cmd);
        cmd.setExecutor(this);
    }

    private BaseCommand getCommand(Command c, CommandArgs args, Sender sender) {
        BaseCommand ret = null;
        for (BaseCommand bc : cmds.keySet()) {
            if (bc.sender() != sender) continue;
            if (bc.command().equalsIgnoreCase(c.getName())) {
                if (args.isEmpty() && bc.subCommand().trim().isEmpty()) {
                    ret = bc;
                } else if (!args.isEmpty() && bc.subCommand().equalsIgnoreCase(args.getString(0))) {
                    ret = bc;
                }
            }
        }
        return ret;
    }

    private Object getCommandObject(Command c, Sender sender, CommandArgs args) throws Exception {
        BaseCommand bcmd = getCommand(c, args, sender);
        if (bcmd == null) {
            for (BaseCommand bc : cmds.keySet()) {
                if (bc.sender() != sender) continue;
                if (bc.command().equalsIgnoreCase(c.getName()) && bc.subCommand().trim().isEmpty()) {
                    bcmd = bc;
                    break;
                }
            }
        }
        MethodContainer container = cmds.get(bcmd);
        Method me = container.getMethod(sender);
        return me.getDeclaringClass().newInstance();
    }

    /**
     * Use this to tell the system that there are commands in the class!
     *
     * @param clazz the classfile where your command /-s are stored.
     */
    public void registerClass(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(CommandHandler.class)) {
            plugin.getLogger().severe("Class is no CommandHandler");
            return;
        }

        HashMap<BaseCommand, HashMap<Sender, Method>> list = new HashMap<>();

        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(BaseCommand.class)) {
                BaseCommand bc = m.getAnnotation(BaseCommand.class);

                registerCommand(bc.command());

                if (!list.containsKey(bc)) {
                    list.put(bc, new HashMap<Sender, Method>());
                }

                HashMap<Sender, Method> map = list.get(bc);

                map.put(bc.sender(), m);

                list.remove(bc);
                list.put(bc, map);
            }
        }

        for (BaseCommand command : list.keySet()) {
            HashMap<Sender, Method> map = list.get(command);

            if (cmds.containsKey(command)) {
                MethodContainer container = cmds.get(command);
                for (Sender s : container.getMethodMap().keySet()) {
                    Method m = container.getMethod(s);
                    map.put(s, m);
                }
                cmds.remove(command);
            }
            cmds.put(command, new MethodContainer(map));
        }
    }

    private Method getMethod(Command c, Sender sender, CommandArgs args) {
        BaseCommand bcmd = getCommand(c, args, sender);
        if (bcmd == null) {
            for (BaseCommand bc : cmds.keySet()) {
                if (bc.sender() != sender) continue;
                if (bc.command().equalsIgnoreCase(c.getName()) && bc.subCommand().trim().isEmpty()) {
                    bcmd = bc;
                    break;
                }
            }
        }
        MethodContainer container = cmds.get(bcmd);
        Method m = container.getMethod(sender);
        try {
            return m;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "", ex);
            return null;
        }
    }

    private boolean executeCommand(Command c, CommandSender s, String[] args) {
        CommandArgs a = new CommandArgs(args);
        Sender sender;
        if (s instanceof Player) {
            sender = Sender.PLAYER;
        } else {
            sender = Sender.CONSOLE;
        }

        Method m = getMethod(c, sender, a);

        if (m != null) {
            m.setAccessible(true);

            BaseCommand bc = m.getAnnotation(BaseCommand.class);
            if (!bc.subCommand().trim().isEmpty() && bc.subCommand().equalsIgnoreCase(a.getString(0))) {
                a = new CommandArgs(args, 1);
            }

            CommandResult cr;

            try {
                if (sender == Sender.PLAYER) {
                    Player p = (Player) s;

                    if (bc.permission() != null && !bc.permission().trim().isEmpty()) {
                        if (!p.hasPermission(bc.permission())) {
                            cr = CommandResult.NO_PERMISSION;
                        } else {
                            cr = (CommandResult) m.invoke(getCommandObject(c, sender, a), p, a);
                        }
                    } else {
                        cr = (CommandResult) m.invoke(getCommandObject(c, sender, a), p, a);
                    }

                } else {
                    cr = (CommandResult) m.invoke(getCommandObject(c, sender, a), s, a);
                }

            } catch (Exception e) {
                logger.log(Level.SEVERE, "", e);
                cr = CommandResult.SUCCESS;
            }

            if (cr
                    != null && cr.getMessage()
                    != null) {
                String perm = bc.permission() != null ? bc.permission() : "";
                s.sendMessage(cr.getMessage().replace("%cmd%", bc.command()).replace("%perm%", perm));
            }
        } else {
            s.sendMessage("§4The command was not made for your sender type!");
        }

        return true;
    }

    /*
     * Bukkit method, just ignore it.
     * Commands will be executed by itself.
     */
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        return executeCommand(cmnd, cs, strings);
    }
}
