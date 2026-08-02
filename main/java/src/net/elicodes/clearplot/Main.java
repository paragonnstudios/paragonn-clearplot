package net.elicodes.clearplot;

import net.elicodes.clearplot.commands.ClearPlotCommand;
import net.elicodes.clearplot.listeners.ClickEvent;
import net.elicodes.clearplot.listeners.InteractEvent;
import net.elicodes.clearplot.listeners.WorldLoadEvent;
import net.elicodes.clearplot.manager.CPManager;
import net.elicodes.clearplot.repository.Database;
import net.elicodes.clearplot.repository.providers.MySQL;
import net.elicodes.clearplot.repository.providers.SQLite;
import net.elicodes.clearplot.utils.EC_Config;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public EC_Config config;
    public EC_Config messagesConfig;
    public EC_Config menusConfig;

    public CPManager manager;
    public Database database;

    private static Main instance;

    public static Main getPlugin() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        this.config = new EC_Config(this, "config.yml");
        this.config.reloadConfig();

        this.messagesConfig = new EC_Config(this, "messages.yml");
        this.messagesConfig.reloadConfig();

        this.menusConfig = new EC_Config(this, "menus.yml");
        this.menusConfig.reloadConfig();

        if(this.config.getBoolean("MySQL.ativar")) {
            (this.database = new MySQL()).open();
        }else (this.database = new SQLite()).open();

        this.manager = new CPManager(this);
        this.manager.loadBlackList();

        getCommand("clearplot").setExecutor(new ClearPlotCommand());
        Bukkit.getPluginManager().registerEvents(new WorldLoadEvent(), this);
        Bukkit.getPluginManager().registerEvents(new ClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new InteractEvent(), this);

        Bukkit.getConsoleSender().sendMessage("§2[HexagonClearPlot] §aPlugin habilitado com êxito.");
    }

    @Override
    public void onDisable() {
        this.manager.saveTasks();
        this.database.close();

        Bukkit.getConsoleSender().sendMessage("§4[HexagonClearPlot] §cPlugin desabilitado com êxito.");
    }
}
