package gg.mineral.database;

import java.util.Optional;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import gg.mineral.database.sql.SQLManager;
import lombok.Getter;

public class DatabaseAPIPlugin extends JavaPlugin {
    public static DatabaseAPIPlugin INSTANCE;
    @Getter
    @Nullable
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
            getLogger().info("Failed to connect to database.");
            sqlManager.close();
            sqlManager = null;
            return;
        }
    }

    @Override
    public void onDisable() {
        if (getSqlManager() != null)
            getSqlManager().close();
        Bukkit.getServer().getScheduler().cancelTasks(this);
    }

    public Optional<SQLManager> retrieveSqlManager() {
        return getSqlManager() == null ? Optional.empty() : Optional.of(getSqlManager());
    }
}
