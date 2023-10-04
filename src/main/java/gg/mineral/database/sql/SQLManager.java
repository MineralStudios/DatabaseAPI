package gg.mineral.database.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;

public class SQLManager {

    private HikariDataSource dataSource;

    public boolean connect(String host, String port, String username, String password, String database) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s", host, port, database));
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(15); // You can adjust this as needed

        try {
            dataSource = new HikariDataSource(config);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public CompletableFuture<ResultSet> executeQuery(String query) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
                return statement.executeQuery(query);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Boolean> executeStatement(String statementStr) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
                statement.execute(statementStr);
                return true;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Close the data source when done
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
