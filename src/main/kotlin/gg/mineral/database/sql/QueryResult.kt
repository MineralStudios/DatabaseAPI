package gg.mineral.database.sql

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class QueryResult(
    private val connection: Connection,
    private val preparedStatement: PreparedStatement,
    private val resultSet: ResultSet
) : AutoCloseable {
    @Throws(Exception::class)
    override fun close() {
        resultSet.close()
        preparedStatement.close()
        connection.close()
    }
}
