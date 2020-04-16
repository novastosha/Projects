package dev.nova.PluginMGR.events;

import dev.nova.PluginMGR.commands.PluginMGR_CMD;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import static dev.nova.PluginMGR.commands.PluginMGR_CMD.blackListedcommands;

public class onCommand implements Listener
{
    private PluginManager pluginManager = Bukkit.getServer().getPluginManager();


    @EventHandler
    public void onCMD(PlayerCommandPreprocessEvent event){
        Plugin[] plugins = pluginManager.getPlugins();
        String message = event.getMessage();
        message = message.replaceAll("/ ","");
            for (String blacklist : blackListedcommands) {
                if (message.contains(blacklist)){
                    event.getPlayer().sendMessage("§cSorry, this command is blacklisted");
                    event.setCancelled(true);


            }
        }
    }
}
