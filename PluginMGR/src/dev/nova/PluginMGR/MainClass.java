package dev.nova.PluginMGR;

import dev.nova.PluginMGR.commands.PluginMGR_CMD;
import dev.nova.PluginMGR.events.onCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MainClass extends JavaPlugin {
    private PluginMGR_CMD pluginMGRCmd;

    @Override
    public void onEnable(){
        pluginMGRCmd = new PluginMGR_CMD();
        getCommand(pluginMGRCmd.cmd).setExecutor(pluginMGRCmd);
        Bukkit.getServer().getPluginManager().registerEvents(new onCommand(), this);
    }


}
