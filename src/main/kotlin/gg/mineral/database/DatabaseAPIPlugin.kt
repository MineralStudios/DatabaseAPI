package gg.mineral.database

import gg.mineral.database.sql.SQLManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class DatabaseAPIPlugin : JavaPlugin() {
    private val host: String by lazy { config.getString("host", "host") }
    private val port: String by lazy { config.getString("port", "3306") }
    private val database: String by lazy { config.getString("database", "database") }
    private val username: String by lazy { config.getString("username", "username") }
    private val password: String by lazy { config.getString("password", "password") }
    val sqlManager: SQLManager? by lazy {
        val sqlManager = SQLManager()
        if (!sqlManager.connect(host, port, username, password, database)) {
            logger.info("Failed to connect to database.")
            sqlManager.close()
            null
        } else sqlManager
    }

    override fun onEnable() {
        INSTANCE = this
        saveDefaultConfig()
        reloadConfig()
    }

    override fun onDisable() {
        sqlManager?.close()
        Bukkit.getServer().scheduler.cancelTasks(this)
    }

    companion object {
        lateinit var INSTANCE: DatabaseAPIPlugin
    }
}
