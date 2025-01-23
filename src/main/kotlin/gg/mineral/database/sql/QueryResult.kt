package gg.mineral.database.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class QueryResult implements AutoCloseable {
    private final Connection connection;
    private final PreparedStatement preparedStatement;
    @Getter
    private final ResultSet resultSet;

    @Override
    public void close() throws Exception {
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
}
