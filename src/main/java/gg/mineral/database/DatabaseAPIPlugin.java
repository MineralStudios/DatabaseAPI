package gg.mineral.database;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import gg.mineral.database.sql.SQLManager;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.MinecraftServer;

public class DatabaseAPIPlugin extends JavaPlugin {
    public static DatabaseAPIPlugin INSTANCE;
    @Getter
    SQLManager sqlManager = new SQLManager();

    @Override
    public void onEnable() {

        INSTANCE = this;

        saveDefaultConfig();
        reloadConfig();

        String host = getConfig().getString("host", "host");
        String port = getConfig().getString("port", "3306");
        String database = getConfig().getString("database", "database");
        String username = getConfig().getString("username", "username");
        String password = getConfig().getString("password", "password");

        getLogger().info("Connecting to database...");

        if (!sqlManager.connect(host, port, username, password, database)) {
            getLogger().info("Failed to connect to database. Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        registerCommands();

        registerListeners();
    }

    @Override
    public void onDisable() {
        getSqlManager().close();
        Bukkit.getServer().getScheduler().cancelTasks(this);
    }

    public void registerCommands(Command... cmds) {
        for (Command c : cmds)
            MinecraftServer.getServer().server.getCommandMap().registerOverride(c.getName(), "DatabaseAPI", c);

    }

    public void registerListeners(Listener... listeners) {
        for (Listener l : listeners)
            Bukkit.getPluginManager().registerEvents(l, this);

    }
}
