package gg.mineral.database.sql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.SQLException
import java.util.concurrent.CompletableFuture

class SQLManager {
    private var dataSource: HikariDataSource? = null

    fun connect(host: String, port: String, username: String, password: String, database: String): Boolean {
        val config = HikariConfig()
        config.jdbcUrl = String.format("jdbc:mysql://%s:%s/%s", host, port, database)
        config.username = username
        config.password = password
        config.maximumPoolSize = 15 // You can adjust this as needed

        try {
            dataSource = HikariDataSource(config)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun <T> executeQuery(
        query: String,
        function: (QueryResult) -> T,
        vararg parameters: Any?
    ): CompletableFuture<T> {
        return CompletableFuture.supplyAsync {
            try {
                dataSource!!.connection.use { connection ->
                    connection.prepareStatement(query).use { preparedStatement ->
                        // Set query parameters
                        for (i in parameters.indices) {
                            preparedStatement.setObject(i + 1, parameters[i])
                        }

                        // Execute the query and process the result set
                        preparedStatement.executeQuery().use { resultSet ->
                            // Process the result using the provided function
                            return@supplyAsync function(QueryResult(connection, preparedStatement, resultSet))
                        }
                    }
                }
            } catch (e: SQLException) {
                throw RuntimeException("Error executing query", e)
            }
        }
    }


    fun executeStatement(statementStr: String?, vararg parameters: Any?): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            try {
                dataSource!!.connection.use { connection ->
                    connection.prepareStatement(statementStr).use { preparedStatement ->
                        for (i in parameters.indices) preparedStatement.setObject(i + 1, parameters[i])
                        preparedStatement.execute()
                        return@supplyAsync true
                    }
                }
            } catch (e: SQLException) {
                throw RuntimeException(e)
            }
        }
    }

    // Close the data source when done
    fun close() {
        if (dataSource != null && !dataSource!!.isClosed) dataSource!!.close()
    }
}
