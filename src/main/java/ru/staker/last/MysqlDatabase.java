package ru.staker.last;

import com.sun.rowset.CachedRowSetImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.rowset.CachedRowSet;
import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;


public class MysqlDatabase {

    private HikariDataSource dataSource;

    public MysqlDatabase(String className, String jdbcURL, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcURL);
        config.setUsername(username);
        config.setPassword(password);
        config.setLeakDetectionThreshold(600000L);
        config.setMaximumPoolSize(50);
        config.setMinimumIdle(5);
        config.setValidationTimeout(15000L);
        config.setIdleTimeout(30000L);
        config.setMaxLifetime(30000L);
        config.addDataSourceProperty("characterEncoding", "utf8");
        config.addDataSourceProperty("useUnicode", "true");
        this.dataSource = new HikariDataSource(config);
    }

    public void disconnect() {
        this.dataSource.close();
        ;
    }

    public CompletableFuture<ResultSet> query(PreparedStatement preparedStatement) {
        return CompletableFuture.supplyAsync(() -> {
            ResultSet resultSet = null;
            try {
                resultSet = preparedStatement.executeQuery();
                CachedRowSet cachedRowSet = new CachedRowSetImpl();
                cachedRowSet.populate(resultSet);
                if (cachedRowSet.next()) {
                    return cachedRowSet;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    preparedStatement.getConnection().close();
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }

    public CompletableFuture<Void> execute(PreparedStatement preparedStatement) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    preparedStatement.getConnection().close();
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }

    public PreparedStatement prepareStatement(String query, String... vars) {
        try {
            final PreparedStatement preparedStatement = this.getConnection().prepareStatement(query);
            int x = 0;
            if (query.contains("?") && vars.length != 0) {
                for (final String var : vars) {
                    ++x;
                    preparedStatement.setString(x, var);
                }
            }
            return preparedStatement;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
