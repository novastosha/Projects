package dev.nova.PluginMGR.commands;

import dev.nova.PluginMGR.PluginsLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

public class PluginMGR_CMD implements CommandExecutor {

    public String cmd = "pluginmgr";

    private PluginManager pluginManager = Bukkit.getServer().getPluginManager();
    private PluginsLogger pluginsLogger = new PluginsLogger();
    public static ArrayList<String> blackListedcommands = new ArrayList<>();

    public void getPluginsInFolder(final File folder, CommandSender commandSender) {

        int pluginsFound = 0;
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
               getPluginsInFolder(fileEntry,commandSender);
            } else {

                    String file2 = fileEntry.getName();
                    if (file2.endsWith(".jar")) {
                        if(fileEntry.getName().endsWith(".jar")) {
                        file2 = file2.replaceAll(".jar", "");
                    }
                    if (!pluginManager.isPluginEnabled(file2)) {
                        try {

                            pluginManager.loadPlugin(fileEntry);
                        } catch (InvalidPluginException | InvalidDescriptionException e) {
                            e.printStackTrace();
                            Date now = new Date();
                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            pluginsLogger.logToFile("[" + format.format(now) + "] " + " Failed to enable: " + fileEntry.getName(), commandSender.getName());
                            commandSender.sendMessage("§8[§bPluginMGR§8] Failed to enable the plugin! §BCheck console");
                        }


                        pluginManager.enablePlugin(pluginManager.getPlugin(file2));

                        pluginsFound++;
                    }
                }
                if(pluginsFound > 0){

                    commandSender.sendMessage("§8Found: §b"+fileEntry.getName()+"§8 ("+pluginsFound+")");
                    Date now = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    pluginsLogger.logToFile("[" + format.format(now) + "] "+" Found: "+fileEntry.getName(),commandSender.getName());
                }else{
                    Date now = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    pluginsLogger.logToFile("[" + format.format(now) + "] "+" "+fileEntry.getName()+" is not a plugin! Skipping!",commandSender.getName());
                    //commandSender.sendMessage("§b"+fileEntry.getName()+" §8is not a plugin, Skipping!");
                }
            }
        }

    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(command.getName().equalsIgnoreCase("pluginmgr")){
            if(args.length != 0){
                if(args[0].equalsIgnoreCase("help")){

                        commandSender.sendMessage("§8-------------------------------------------------");
                        commandSender.sendMessage("§b/pluginmgr safereload [Path of plugin] §8Enables a plugin with out reload or from an other folder inside the plugins folder (TEMP)");
                        commandSender.sendMessage("§b/pluginmgr disable [Plugin name] §8Disables a plugin");
                        commandSender.sendMessage("§b/pluginmgr info [Plugin name] §8Get all info of a plugin");
                        commandSender.sendMessage("§b/pluginmgr list §8Gives you the list of all plugins");
                        commandSender.sendMessage("§8-------------------------------------------------");

                    return true;
                }
                if(args[0].equalsIgnoreCase("blacklist")){
                    if(args.length == 3) {
                        if(args[1].equalsIgnoreCase("add")){
                            String arg2 = args[2];
                            if(!blackListedcommands.contains(args[2])){

                                if(arg2.contains("/")){
                                    arg2 = arg2.replaceFirst("/","");
                                }
                                for(Plugin plugin : pluginManager.getPlugins()){
                                    if(!pluginManager.getPlugin(plugin.getName()).getDescription().getCommands().containsKey(arg2)){
                                        commandSender.sendMessage("§cWe can't find this command!");
                                        return true;
                                    }
                                }
                                blackListedcommands.add(arg2);
                                commandSender.sendMessage("§8[§bPluginMGR§8] §b/"+arg2+" §8has been added to the blacklist!");
                                return true;
                            }else{commandSender.sendMessage("§cNothing changed! This command was in the blacklist");return true;}
                        }
                        if(args[1].equalsIgnoreCase("remove")){
                            String arg2 = args[2];
                            if(blackListedcommands.contains(args[2])){

                                if(arg2.contains("/")){
                                    arg2 = arg2.replaceFirst("/","");
                                }
                                blackListedcommands.remove(arg2);
                                commandSender.sendMessage("§8[§bPluginMGR§8] §b/"+arg2+" §8has been removed from the blacklist!");
                                return true;
                            }else{commandSender.sendMessage("§cNothing changed! This command was not in the blacklist");return true;}
                        }
                        return true;
                    }else if(args.length == 1 && args[0].equalsIgnoreCase("blacklist")){
                        if(!blackListedcommands.isEmpty()) {
                            commandSender.sendMessage("§8[§bPluginMGR§8] Blacklisted commands: ");
                            int bcmd = 0;
                            for (String cmd : blackListedcommands) {

                                commandSender.sendMessage(" §b-/" + cmd);
                                bcmd++;
                            }
                            commandSender.sendMessage("§8Blacklisted commands found: §b" + bcmd);
                            return true;
                        }else{commandSender.sendMessage("§cNo blacklisted commands found.");return true;}
                    }
                    commandSender.sendMessage("§cInvaild arguments!");
                    return true;
                }
                if(args[0].equalsIgnoreCase("list")){
                    Plugin[] plugins = pluginManager.getPlugins();
                    int pluginsFound = 0;
                    commandSender.sendMessage("§8[§bPluginMGR§8] Your plugins: ");
                    for(Plugin plugin : plugins){
                        commandSender.sendMessage(" §b-"+plugin.getName());
                        pluginsFound++;

                    }

                    commandSender.sendMessage("§8Plugins found: §b"+pluginsFound);
                    return true;
                }
                if (args[0].equalsIgnoreCase("disable")){
                    if(args.length == 2) {

                        Plugin plugin = pluginManager.getPlugin(args[1]);
                        if (pluginManager.isPluginEnabled(plugin)) {
                            commandSender.sendMessage("§8[§bPluginMGR§8] Disabling the plugin: §b" + plugin.getName());
                            pluginManager.disablePlugin(plugin);
                            return true;
                        }else{commandSender.sendMessage("§cSorry, this plugin is disabled or doesn't exist!");return true;}
                    }

                }
                if (args[0].equalsIgnoreCase("info")){
                    if(args.length == 2) {

                        Plugin plugin = pluginManager.getPlugin(args[1]);

                        if (pluginManager.isPluginEnabled(plugin)) {
                            commandSender.sendMessage("§8[§bPluginMGR§8] Getting all info from: §b" + plugin.getName());
                            commandSender.sendMessage(" ");
                            commandSender.sendMessage("§8Authors: ");
                            for (String author : plugin.getDescription().getAuthors()) {
                                commandSender.sendMessage("§b -" + author);
                            }

                            commandSender.sendMessage("§8Description: §b" + plugin.getDescription().getDescription());
                            commandSender.sendMessage("§8Version: §b" + plugin.getDescription().getVersion());
                            if (plugin.getDescription().getWebsite() != null) {
                                commandSender.sendMessage("§8Website: §b" + plugin.getDescription().getWebsite());
                            }
                            if (!plugin.getDescription().getCommands().isEmpty()) {
                                int cmdsFound = 0;
                                commandSender.sendMessage("§8Commands: ");
                                for (String commandFound : plugin.getDescription().getCommands().keySet()) {
                                    commandSender.sendMessage("§b -/"+commandFound);
                                    cmdsFound++;
                                }
                                commandSender.sendMessage("§8Commands found: §b" + cmdsFound);
                            }
                            if (!plugin.getDescription().getDepend().isEmpty()) {
                                int dependsFound = 0;
                                commandSender.sendMessage("§8Depends: ");
                                for (String depend : plugin.getDescription().getDepend()) {
                                    commandSender.sendMessage("§b -" + depend);
                                    dependsFound++;
                                }
                                commandSender.sendMessage("§8Depends found: §b" + dependsFound);
                            }


                            if (!plugin.getDescription().getPermissions().isEmpty()) {
                                commandSender.sendMessage("§8Permissions: ");
                                int permsFound = 0;
                                for (Permission permission : plugin.getDescription().getPermissions()) {
                                    commandSender.sendMessage("§b -" + permission.getName());
                                    permsFound++;
                                }
                                commandSender.sendMessage("§8Permissions found: §b" + permsFound);


                            }
                            return true;
                        }else{commandSender.sendMessage("§cSorry, this plugin is disabled or doesn't exist!");return true;}
                    }

                }
                if(args[0].equalsIgnoreCase("safereload")){
                    if(args.length == 2) {
                        String pluginPath = args[1];
                        if (!pluginPath.contains("plugins\\")) {
                            pluginPath = "plugins\\" + pluginPath;
                        }
                        File plugin = new File(pluginPath);
                        
                        if (plugin.exists()) {
                            if (plugin.isFile()) {
                                String file2 = plugin.getName();
                                file2 = file2.replaceAll(".jar","");
                                if(!pluginManager.isPluginEnabled(pluginManager.getPlugin(file2))){
                                    commandSender.sendMessage("§8[§bPluginMGR§8] Trying to enable the plugin at: §b" + pluginPath);
                                    try {

                                        pluginManager.loadPlugin(plugin);
                                        pluginManager.enablePlugin(pluginManager.getPlugin(file2));

                                    } catch (InvalidPluginException | InvalidDescriptionException e) {
                                        commandSender.sendMessage("§8[§bPluginMGR§8] Failed to enable the plugin! §BCheck console");
                                        e.printStackTrace();
                                        return true;
                                    }
                                    commandSender.sendMessage("§8[§bPluginMGR§8] Plugin enabled!");

                                    return true;
                                }


                            } else if (plugin.isDirectory()) {
                                commandSender.sendMessage("§8[§bPluginMGR§8] Trying to find any plugins at the folder: §b" + pluginPath);
                                getPluginsInFolder(plugin, commandSender);
                            }
                            return true;
                        }else{commandSender.sendMessage("§8[§bPluginMGR§8] §b"+pluginPath+" §8not found!"); return true;}
                    }
                    commandSender.sendMessage("§c/pluginmgr safereload [Path of plugin]");
                    return true;
                }

            }else{
                commandSender.sendMessage("§c/pluginmgr help for more info");
            }
            return true;
        }
        return true;
    }
}
